package com.truncon.auditing;

import com.truncon.models.User;

public final class AuditContext {
    private static final ThreadLocal<AuditContext> CURRENT = ThreadLocal.withInitial(AuditContext::new);
    private User user;

    private AuditContext() {
    }

    public static AuditContext getCurrent() {
        return CURRENT.get();
    }

    public User getUser() {
        return this.user;
    }

    public User setUser(User user) {
        User old = this.user;
        this.user = user;
        return old;
    }

    public Scope runAs(User user) {
        return new Scope(this, user);
    }

    public <T extends Throwable> void runAs(User user, ThrowingRunnable<T> runnable) throws T {
        try (Scope scope = runAs(user)) {
            runnable.run();
        }
    }

    public <V, T extends Throwable> V runAs(User user, ThrowingSupplier<V, T> supplier) throws T {
        try (Scope scope = runAs(user)) {
            return supplier.get();
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable<T extends Throwable> {
        void run() throws T;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<V, T extends Throwable> {
        V get() throws T;
    }

    public static final class Scope implements AutoCloseable {
        private final AuditContext context;
        private final User old;

        private Scope(AuditContext context, User user) {
            this.context = context;
            this.old = context.setUser(user);
        }

        @Override
        public void close() {
            context.setUser(this.old);
        }
    }
}
