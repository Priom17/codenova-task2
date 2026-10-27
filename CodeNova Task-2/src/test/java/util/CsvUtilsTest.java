package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CsvUtilsTest {
    @Test
    void parseCsvLineHandlesQuotedCommasAndQuotes() {
        String quoted = "TXN-001,2026-10-17T12:00:00,\"Lunch, \"\"family\"\" meal\"";

        String[] values = CsvUtils.parseCsvLine(quoted);

        assertArrayEquals(new String[] {"TXN-001", "2026-10-17T12:00:00", "Lunch, \"family\" meal"}, values);
    }
}
