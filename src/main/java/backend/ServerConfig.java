package backend;

import backend.enums.ConfigType;

import java.util.Collections;
import java.util.Map;

public class ServerConfig {

    private String hostName;
    private String host;
    private Integer port;
    private ConfigType configType;

    private Map<String,Object> additional;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public Map<String, Object> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, Object> additional) {
        this.additional = additional;
    }

    public static  ServerConfig getDefaultESServerConfig(){
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setHostName("elasticsearch");
        serverConfig.setHost("127.0.0.1");
        serverConfig.setAdditional(Collections.emptyMap());
        serverConfig.setConfigType(ConfigType.ELASTIC_SEARCH);
        serverConfig.setPort(9300);
        return serverConfig;
    }

}
