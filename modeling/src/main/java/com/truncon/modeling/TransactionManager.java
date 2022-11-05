package com.truncon.modeling;

public interface TransactionManager {
    TransactionScope createTransaction() throws DbException;

    default TransactionScope beginTransaction() throws DbException {
        TransactionScope scope = createTransaction();
        scope.begin();
        return scope;
    }
}
