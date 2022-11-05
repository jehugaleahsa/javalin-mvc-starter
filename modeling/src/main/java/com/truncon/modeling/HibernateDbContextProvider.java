package com.truncon.modeling;

import javax.inject.Inject;
import javax.inject.Provider;

public final class HibernateDbContextProvider implements Provider<HibernateDbContext> {
    private final HibernateDbContextFactory factory;

    @Inject
    public HibernateDbContextProvider(HibernateDbContextFactory factory) {
        this.factory = factory;
    }

    @Override
    public HibernateDbContext get() {
        return factory.createContext();
    }
}
