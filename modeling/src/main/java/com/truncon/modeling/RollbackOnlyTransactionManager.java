package com.truncon.modeling;

public final class RollbackOnlyTransactionManager implements TransactionManager {
    private final TransactionManager transactionManager;

    public RollbackOnlyTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionScope createTransaction() throws DbException {
        return new RollbackOnlyTransactionScope(transactionManager.createTransaction());
    }
}
