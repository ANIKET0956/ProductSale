package backend.esutils;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

public interface ESClient {

    IndexResponse prepareIndex(String index, String type);

    CreateIndexResponse prepareIndexWithMapping(String index, String type, String mappingFile);

    IndexResponse saveDocument(String index, String type, String docId, String source);

    GetResponse getDocumentFromId(String index, String type,String docId);

    DeleteResponse deleteDocumentFromId(String index, String type, String docId);

    SearchResponse searchDocument(SearchRequest searchRequest);

    void close();

}
