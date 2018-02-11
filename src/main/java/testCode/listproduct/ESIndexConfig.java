package testCode.listproduct;

import backend.ProductDetails;
import backend.ServerConfig;
import backend.esutils.ESClient;
import backend.esutils.ESClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.ReindexPlugin;


import com.fasterxml.jackson.*;
import java.net.InetAddress;
import java.util.*;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.*;
import org.elasticsearch.percolator.PercolatorPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.mustache.MustachePlugin;
import org.elasticsearch.transport.Netty3Plugin;
import org.elasticsearch.transport.Netty4Plugin;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ESIndexConfig {


    private static final Collection<Class<? extends Plugin>> PRE_INSTALLED_PLUGINS =
            Collections.unmodifiableList(
                    Arrays.asList(
                            Netty3Plugin.class,
                            Netty4Plugin.class,
                            ReindexPlugin.class,
                            PercolatorPlugin.class,
                            MustachePlugin.class));



    @Test
    public void bulkProcessingTest() throws Exception {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setHost("127.0.0.1");
        serverConfig.setPort(9300);
        ESClient esClient = new ESClientBuilder().addServerDetails(serverConfig).build();
        String mapping = IOUtils.toString(new ClassPathResource("product_detail.json").getInputStream());
        esClient.prepareIndexWithMapping("product","document",mapping);



        // Index to operate on:  // on shutdown
        String indexName = "weather_data";
        Settings settings = Settings.builder().put("transport.type.default","netty4").build();


        PreBuiltTransportClient preBuiltTransportClient = new PreBuiltTransportClient(settings);

        CustomisedTransportClient customisedTransportClient = new CustomisedTransportClient(settings,PRE_INSTALLED_PLUGINS);

        customisedTransportClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));

        IndexResponse response = customisedTransportClient.prepareIndex("twitter", "tweet", "2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("value", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()
                )
                .get();
        RestStatus status = response.status();
    }

    private static class CustomisedTransportClient extends TransportClient {

        public CustomisedTransportClient(Settings settings, Collection<Class<? extends Plugin>> plugins) {
            super(settings, plugins);
        }

        protected CustomisedTransportClient(Settings settings, Settings defaultSettings, Collection<Class<? extends Plugin>> plugins, HostFailureListener hostFailureListener) {
            super(settings, defaultSettings, plugins, hostFailureListener);
        }
    }

}
