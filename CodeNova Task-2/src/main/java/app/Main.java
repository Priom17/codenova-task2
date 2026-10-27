package app;

import controller.FinanceController;
import repository.CsvTransactionRepository;
import service.FinanceReportService;
import service.FinanceService;
import view.FinanceView;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        Path storagePath = Path.of("transactions.csv");
        CsvTransactionRepository repository = new CsvTransactionRepository(storagePath);
        FinanceService financeService = new FinanceService(repository);
        FinanceReportService financeReportService = new FinanceReportService();

        try (Terminal terminal = TerminalBuilder.builder().system(true).dumb(true).build()) {
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();
            FinanceView view = new FinanceView(lineReader);
            FinanceController controller = new FinanceController(financeService, financeReportService, view);
            controller.run();
        }
    }
}
