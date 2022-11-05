package com.truncon.modeling;

public final class RollbackOnlyTransactionScope implements TransactionScope {
    private final TransactionScope scope;

    public RollbackOnlyTransactionScope(TransactionScope scope) {
        this.scope = scope;
    }

    @Override
    public void begin() throws DbException {
        this.scope.begin();
    }

    @Override
    public void commit() throws DbException {
        // DO NOTHING - never commit
    }

    @Override
    public void rollback() throws DbException {
        this.scope.rollback();
    }

    @Override
    public void close() throws DbException {
        this.scope.close();
    }
}
