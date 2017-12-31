package backend.esutils;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;

public class ESClientImpl implements ESClient {

    private TransportClient transportClient;

    public ESClientImpl(TransportClient transportClient) {
        this.transportClient = transportClient;
    }

    @Override
    public IndexResponse prepareIndex(String index, String type) {
        return transportClient.prepareIndex(index,type).get();
    }

    @Override
    public CreateIndexResponse prepareIndexWithMapping(String index, String type, String mappingFile) {
        return transportClient.admin().indices().prepareCreate(index).addMapping(type,mappingFile).get();
    }

    @Override
    public IndexResponse saveDocument(String index, String type, String docId, String source) {
        return transportClient.prepareIndex(index,type,docId).setSource(source).get();
    }

    @Override
    public GetResponse getDocumentFromId(String index, String type, String docId) {
        return transportClient.prepareGet(index,type,docId).get();
    }

    @Override
    public DeleteResponse deleteDocumentFromId(String index, String type, String docId) {
        return transportClient.prepareDelete(index,type,docId).get();
    }

    @Override
    public SearchResponse searchDocument(SearchRequest searchRequest) {
        return transportClient.search(searchRequest).actionGet();
    }

    @Override
    public void close() {
        transportClient.close();
    }
}
