package repository;

import model.Transaction;

import java.util.List;

public interface TransactionRepository {
    List<Transaction> findAll();
    void saveAll(List<Transaction> transactions);
}
