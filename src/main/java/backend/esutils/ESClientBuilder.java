package backend.esutils;

import backend.ServerConfig;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.reindex.ReindexPlugin;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.transport.Netty3Plugin;
import org.elasticsearch.transport.Netty4Plugin;

import java.net.InetAddress;
import java.util.*;


public class ESClientBuilder {

    private Settings settings;
    private Collection<Class<? extends Plugin>> plugins = Collections.emptyList();
    private DefaultTransportClient transportClient;
    private ServerConfig serverConfig;

    private static final Collection<Class<? extends Plugin>> PRE_INSTALLED_PLUGINS = Collections.unmodifiableList(
                    Arrays.asList(Netty3Plugin.class, Netty4Plugin.class, ReindexPlugin.class, PercolatorPlugin.class, MustachePlugin.class));

    //By default.
    public ESClientBuilder() {

    }

    public ESClientBuilder(Settings settings, Collection<Class<? extends Plugin>> plugins) {
        this.settings = settings;
        this.plugins = plugins;
    }

    public ESClientBuilder addServerDetails(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public ESClientBuilder addSettings(Settings settings) {
        this.settings  = settings;
        return this;
    }

    public ESClientBuilder addPlugins(Class<? extends  Plugin> plugin) {
        this.plugins.add(plugin);
        return this;
    }

    public ESClient build() throws Exception {
        try {
            this.settings = addDefaultSettings(settings);
            this.plugins = addDefaultPlugins(plugins);
            this.transportClient  = new DefaultTransportClient(settings,plugins);
            if(serverConfig != null) {
                this.transportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverConfig.getHost()),serverConfig.getPort()));
            }
            return new ESClientImpl(transportClient);

        } catch (Exception ex) {
            System.out.println("Error in building ES Client" + ex);
            throw new RuntimeException(ex);
        }
    }

    private static class DefaultTransportClient extends TransportClient {

        public DefaultTransportClient(Settings settings, Collection<Class<? extends Plugin>> plugins) {
            super(settings, plugins);
        }

        public DefaultTransportClient(Settings settings, Settings defaultSettings, Collection<Class<? extends Plugin>> plugins, HostFailureListener hostFailureListener) {
            super(settings, defaultSettings, plugins, hostFailureListener);
        }
    }


    // ---------------------------  Private Methods ---------------------------------  //

    private Settings addDefaultSettings(Settings settings) {
        final Settings.Builder settingBuilder = Settings.builder();
        settingBuilder.put("transport.type.default","netty4");
        settingBuilder.put("client.type","transport");
        settingBuilder.put("http.type.default","netty4");
        if (settings != null) {
            settingBuilder.put(settings);
        }
        return settingBuilder.build();
    }

    private Collection<Class<? extends  Plugin>> addDefaultPlugins(Collection<Class<? extends Plugin>> plugins) {
        this.plugins = new LinkedList<>();
        this.plugins.addAll(PRE_INSTALLED_PLUGINS);
        this.plugins.addAll(plugins);
        return this.plugins;
    }

}
