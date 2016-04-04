package com.kumuluz.ee.common.config;

import com.kumuluz.ee.common.utils.EnvUtils;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceConfig {

    public static final String DB_UNIT_ENV = "DATABASE_UNIT";

    public static final String DB_URL_ENV = "DATABASE_URL";

    public static final String DB_USER_ENV = "DATABASE_USER";

    public static final String DB_PASS_ENV = "DATABASE_PASS";

    private String unitName;

    private String url;

    private String username;

    private String password;

    public PersistenceConfig() {

        EnvUtils.getEnv(DB_UNIT_ENV, this::setUnitName);
        EnvUtils.getEnv(DB_URL_ENV, this::setUrl);
        EnvUtils.getEnv(DB_USER_ENV, this::setUsername);
        EnvUtils.getEnv(DB_PASS_ENV, this::setPassword);
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
