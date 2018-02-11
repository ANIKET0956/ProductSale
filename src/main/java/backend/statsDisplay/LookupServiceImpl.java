package backend.statsDisplay;

import backend.ProductDetails;
import backend.esutils.ESClient;
import backend.esutils.ESUtils;
import backend.utility.ModuleUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LookupServiceImpl implements LookupService<ProductDetails> {

    private ESClient esClient;

    public LookupServiceImpl(ESClient esClient) {
        this.esClient = esClient;
    }

    @Override
    public List<ProductDetails> getPresentProductDetails() {
        List<ProductDetails> listProducts = new ArrayList<>();
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(searchSourceBuilder);
            searchRequest.indices("product");
            SearchResponse searchResponse = esClient.searchDocument(searchRequest);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit searchHit : hits) {
                ProductDetails productDetails = ModuleUtils.parseFromString(searchHit.getSourceAsString(), ProductDetails.class);
                listProducts.add(productDetails);
            }
        } catch (Exception ex) {
            System.out.println("Error in fetching details for table : " + ex);
        }
        return listProducts;
    }

    @Override
    public void saveProduct(ProductDetails productDetails) {
        try {
            ObjectMapper objectMapper = ESUtils.getMapperInstance();
            String product = objectMapper.writeValueAsString(productDetails);
            IndexResponse indexResponse = esClient.saveDocument("product","document", productDetails.getId(),product);
            if (indexResponse.status() == RestStatus.ACCEPTED) {
                
            }

        } catch (Exception ex) {
            System.out.println("Error in saving new Product data : " + ex);
        }
    }
}
