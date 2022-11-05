package com.truncon.web.settings;

import com.truncon.settings.DbSettings;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.OptionalInt;

public final class HoconSettings implements ApplicationSettings, DbSettings {
    private final ConfigWrapper config;

    private HoconSettings(ConfigWrapper config) {
        this.config = config;
    }

    public static HoconSettings newInstance() {
        Path configurationPath = Paths.get("./config/application.conf");
        Config config = ConfigFactory.parseFile(configurationPath.toFile());
        ConfigWrapper wrapper = new ConfigWrapper(config);
        return new HoconSettings(wrapper);
    }

    public static HoconSettings newInstance(Path overrides) {
        Path configurationPath = Paths.get("./config/application.conf");
        Config config = ConfigFactory.parseFile(configurationPath.toFile());

        Config overrideConfig = ConfigFactory.parseFile(overrides.toFile());

        ConfigWrapper wrapper = new ConfigWrapper(overrideConfig.withFallback(config));
        return new HoconSettings(wrapper);
    }

    @Override
    public int getPort() {
        return webServer().getInteger("port").orElse(8080);
    }

    @Override
    public String getDriver() {
        return database().getString("driver").orElse(null);
    }

    @Override
    public String getUrl() {
        return database().getString("url").orElse(null);
    }

    @Override
    public String getUsername() {
        return database().getString("username").orElse(null);
    }

    @Override
    public String getPassword() {
        return database().getString("password").orElse(null);
    }

    @Override
    public String getSchema() {
        return database().getString("schema").orElse(null);
    }

    @Override
    public boolean isRollbackOnly() {
        return database().getBoolean("rollbackOnly").orElse(false);
    }

    private ConfigWrapper webServer() {
        return config.atPath("web.server");
    }

    private ConfigWrapper database() {
        return config.atPath("database");
    }

    private static final class ConfigWrapper {
        private final Config config;

        public ConfigWrapper(Config config) {
            this.config = config;
        }

        public ConfigWrapper atPath(String path) {
            if (this.config == null || !this.config.hasPath(path)) {
                return new ConfigWrapper(null);
            }
            Config childConfig = this.config.getConfig(path);
            return new ConfigWrapper(childConfig);
        }

        public Optional<String> getString(String path) {
            if (this.config == null || !this.config.hasPath(path)) {
                return Optional.empty();
            }
            ConfigValue value = this.config.getValue(path);
            return Optional.of(value.unwrapped().toString());
        }

        public Optional<Boolean> getBoolean(String path) {
            if (this.config == null || !this.config.hasPath(path)) {
                return Optional.empty();
            }
            ConfigValue value = this.config.getValue(path);
            if (value.valueType() == ConfigValueType.BOOLEAN) {
                return Optional.of((Boolean) value.unwrapped());
            }
            return Optional.empty();
        }

        public OptionalInt getInteger(String path) {
            return getString(path)
                .stream()
                .flatMapToInt(s -> toInteger(s).stream())
                .findFirst();
        }

        private static OptionalInt toInteger(String value) {
            if (value == null || value.isBlank()) {
                return OptionalInt.empty();
            }
            try {
                int intValue = Integer.parseInt(value);
                return OptionalInt.of(intValue);
            } catch (NumberFormatException exception) {
                return OptionalInt.empty();
            }
        }
    }
}
