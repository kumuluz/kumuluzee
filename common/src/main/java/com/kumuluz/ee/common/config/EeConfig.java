package com.kumuluz.ee.common.config;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    private String version;

    private ServerConfig serverConfig = new ServerConfig();
    private List<PersistenceConfig> persistenceConfigs = new ArrayList<>();
    private List<DataSourceConfig> datasources = new ArrayList<>();

    public EeConfig() {
        this.version = ResourceBundle.getBundle("version").getString("version");

        persistenceConfigs.add(new PersistenceConfig());

        InputStream is = getClass().getClassLoader().getResourceAsStream("config.yml");

        if (is != null) {

            Yaml yaml = new Yaml();

            Object rootCfg = yaml.load(getClass().getClassLoader().getResourceAsStream("config.yml"));

            if (rootCfg instanceof Map) {
                Map rootCfgMap = (Map) rootCfg;

                Object keeCfg = rootCfgMap.get("kumuluzee");

                if (keeCfg instanceof Map) {
                    Map keeCfgMap = (Map) keeCfg;

                    Object dssCfg = keeCfgMap.get("datasources");

                    if (dssCfg instanceof List) {
                        List dssCfgList = (List) dssCfg;

                        for (int i = 0; i < dssCfgList.size(); i++) {
                            Object dsCfg = dssCfgList.get(i);

                            if (dsCfg instanceof Map) {
                                Map dsCfgMap = (Map) dsCfg;

                                DataSourceConfig dsc = new DataSourceConfig();

                                Optional<String> jndiName = ConfigurationUtil.getInstance().get("kumuluzee.datasources[" + i + "].jndi-name");
                                Optional<String> conUrl = ConfigurationUtil.getInstance().get("kumuluzee.datasources[" + i + "].connection-url");
                                Optional<String> user = ConfigurationUtil.getInstance().get("kumuluzee.datasources[" + i + "].username");
                                Optional<String> pass = ConfigurationUtil.getInstance().get("kumuluzee.datasources[" + i + "].password");
                                Optional<Integer> maxPool = ConfigurationUtil.getInstance().getInteger("kumuluzee.datasources[" + i + "].max-pool-size");

                                dsc.setJndiName(jndiName.isPresent() ? jndiName.get() : (String) dsCfgMap.get("jndi-name"));
                                dsc.setConnectionUrl(conUrl.isPresent() ? conUrl.get() : (String) dsCfgMap.get("connection-url"));
                                dsc.setUsername(user.isPresent() ? user.get() : (String) dsCfgMap.get("username"));
                                dsc.setPassword(pass.isPresent() ? pass.get() : (String) dsCfgMap.get("password"));

                                if (maxPool.isPresent()) {
                                    dsc.setMaxPoolSize(maxPool.get());
                                } else {
                                    Object maxPoolSize = dsCfgMap.get("max-pool-size");

                                    if (maxPoolSize != null && maxPoolSize instanceof Integer) {
                                        dsc.setMaxPoolSize((Integer) maxPoolSize);
                                    }
                                }

                                datasources.add(dsc);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public List<PersistenceConfig> getPersistenceConfigs() {
        return persistenceConfigs;
    }

    public List<DataSourceConfig> getDatasources() {
        return datasources;
    }
}
