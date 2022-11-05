package com.truncon.settings;

public interface DbSettings {
    String getDriver();
    String getUrl();
    String getUsername();
    String getPassword();
    String getSchema();
    boolean isRollbackOnly();
}
