package backend.statsDisplay;

import backend.enums.DataType;
import backend.ProductDetails;
import backend.ServerConfig;
import backend.UIAttributes;
import backend.enums.OperatorType;
import backend.esutils.ESClient;
import backend.esutils.ESClientBuilder;
import backend.utility.HelperUtils;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ProductShow extends Application {

    private static final Double SCREEN_HEIGHT = 1300.0;
    private static final Double SCREEN_WIDTH = 1800.0;

    private ESClient esClient;
    private ObservableList<ProductDetails> listProducts = FXCollections.observableArrayList();

    public ProductShow() {
        super();
    }

    @Override
    public void init() throws Exception {
        super.init();
        esClient = new ESClientBuilder().addServerDetails(ServerConfig.getDefaultESServerConfig()).build();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        esClient.close();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();

        // Create table view
        TableView<ProductDetails> table = new TableView<ProductDetails>();
        table.setTranslateX(0);
        table.setTranslateY(200);
        setUpProductTable(table,ProductDetails.class);
        fillTableData(table);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        // Create functional buttons
        HBox hBox = createButtonInHBox(table);

        HBox filterRow = new HBox();
        Button filterButton = new Button("Filter Product");
        FilterBox filterBox = new FilterBox();
        filterRow.getChildren().addAll(filterButton);
        filterBox.getOperatorBox().hide();
        filterRow.getChildren().addAll(filterBox.getChoiceBox(),filterBox.getOperatorBox(),filterBox.getComboBox());
        filterRow.setTranslateY(150);
        filterRow.setSpacing(5L);
        filterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                applyFilterOnProduct(table,Collections.singletonList(filterBox));
                table.refresh();
            }
        });

        root.getChildren().addAll(hBox,filterRow,table);

        Scene scene = new Scene(root, SCREEN_WIDTH,SCREEN_HEIGHT);
        scene.setFill(Color.LIGHTGRAY);
        stage.setScene(scene);
        stage.show();

    }


    // -------------  Private Methods ---------------------- //

    private HBox createButtonInHBox(TableView tableView) {
        final Button addButton = new Button("Add New Product");
        final Button updateButton = new Button("Update Product");
        final Button refreshButton = new Button("Refresh");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println(((ProductDetails)tableView.getItems().get(0)).getSelected());
                AddProduct addProduct = new AddProduct(esClient);
                addProduct.start();
            }
        });

        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                fillTableData(tableView);
                tableView.refresh();
            }
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(addButton,updateButton,refreshButton);
        hBox.setTranslateX(100);
        hBox.setTranslateY(100);
        hBox.setSpacing(SCREEN_WIDTH/(hBox.getChildren().size()+2));
        return hBox;
    }

    private <T> void setUpProductTable(TableView<T> table, Class<T> type) {
        table.setEditable(true);
        List<String> fieldNames = HelperUtils.getFieldNameWithIgnoreCase(type);
        double sizeColumn = SCREEN_WIDTH/(fieldNames.size()+0.7);
        TableColumn<T, Boolean> radioColumn = getRadioButtonCellColumn();
        table.getColumns().add(radioColumn);
        for (String name : fieldNames) {
            TableColumn<T,String> tableColumn = new TableColumn<>(HelperUtils.beautifyName(name));
            tableColumn.setMinWidth(sizeColumn);
            tableColumn.setCellValueFactory(new PropertyValueFactory<T,String>(name));
            table.getColumns().add(tableColumn);
        }
    }

    @SuppressWarnings("unchecked")
    private void fillTableData(TableView tableView) {
        tableView.getItems().clear();
        listProducts.clear();
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.source(searchSourceBuilder);
            searchRequest.indices("product");
            SearchResponse searchResponse = esClient.searchDocument(searchRequest);
            SearchHits hits = searchResponse.getHits();
            for (SearchHit searchHit : hits) {
                ProductDetails productDetails = HelperUtils.parseFromString(searchHit.getSourceAsString(), ProductDetails.class);
                listProducts.add(productDetails);
            }
        } catch (Exception ex) {
            System.out.println("Error in fetching details for table : " + ex);
        } finally {
            tableView.getItems().addAll(listProducts);
        }
    }

    private class FilterBox {
        private ChoiceBox choiceBox;
        private ComboBox comboBox;
        private ChoiceBox operatorBox;

        private List<UIAttributes> choiceAttributes = new ArrayList<>();

        public FilterBox() {
            choiceBox =  new ChoiceBox();
            comboBox = new ComboBox();
            operatorBox = new ChoiceBox();
            comboBox.setEditable(true);
            fillChoicesForFiltering();
            choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Object selectedItem = choiceAttributes.get(t1.intValue()).getKey();
                    System.out.println(selectedItem);
                    comboBox.getItems().clear();
                    comboBox.getItems().addAll(getValuesForFilterType(String.valueOf(selectedItem)));
                    operatorBox.getItems().clear();
                    operatorBox.getItems().addAll(fillChoicesForOperator(choiceAttributes.get(t1.intValue()).getDataType()));
                }
            });
        }

        private void fillChoicesForFiltering() {
            List<Tuple<String, DataType>> fieldName = HelperUtils.getFieldsFromClassWithType(ProductDetails.class);
            for (Tuple<String,DataType> tuple : fieldName) {
                choiceAttributes.add(new UIAttributes(tuple.v1(),HelperUtils.beautifyName(tuple.v1()),tuple.v2()));
            }
            choiceBox.getItems().addAll(fieldName.stream().map(t -> HelperUtils.beautifyName(t.v1())).collect(Collectors.toList()));
        }

        private List<String> fillChoicesForOperator(DataType type) {
            List<String> choices = new ArrayList<>();
            switch (type) {
                case NUMBER:
                    choices.add(OperatorType.EQUAL.getLabel());
                    choices.add(OperatorType.GREATER.getLabel());
                    choices.add(OperatorType.LESSER.getLabel());
                    break;
                case STRING:
                default:
                    choices.add(OperatorType.EQUAL.getLabel());
                    choices.add(OperatorType.CONTAINS.getLabel());
                    break;

            }
           return choices;
        }

        public List<UIAttributes> getChoiceAttributes() {
            return choiceAttributes;
        }

        public ChoiceBox getOperatorBox() {
            return operatorBox;
        }

        public ChoiceBox getChoiceBox() {
            return choiceBox;
        }

        public ComboBox getComboBox() {
            return comboBox;
        }
    }

    private Set<String> getValuesForFilterType(String filterType) {
        Set<String> toReturn = new HashSet<>();
        try {
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            AggregationBuilder aggregationBuilder = AggregationBuilders.terms("field").field(filterType).size(1000);
            searchSourceBuilder.aggregation(aggregationBuilder);
            searchRequest.source(searchSourceBuilder);
            searchRequest.indices("product");
            SearchResponse searchResponse = esClient.searchDocument(searchRequest);
            Terms terms = searchResponse.getAggregations().get("field");
            toReturn = terms.getBuckets().stream().map(bucket -> String.valueOf(bucket.getKey())).collect(Collectors.toSet());
        } catch (Exception ex) {
            System.out.println("Error in fetching distinct values for filterType : " + ex);
        }
        return toReturn;
    }

    private void applyFilterOnProduct(TableView tableView, List<FilterBox> filterBoxes) {
        tableView.getItems().clear();
        listProducts.clear();
        try {
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            for (FilterBox filterBox : filterBoxes) {
                boolQueryBuilder.must(createQueryForDataType(filterBox));
            }
            searchSourceBuilder.query(boolQueryBuilder);
            searchRequest.source(searchSourceBuilder);
            System.out.println(searchSourceBuilder.toString());
            SearchResponse searchResponse = esClient.searchDocument(searchRequest);
            for (SearchHit hit : searchResponse.getHits()) {
                listProducts.add(HelperUtils.parseFromString(hit.getSourceAsString(),ProductDetails.class));
            }
        } catch (Exception ex){
            System.out.println("Error in applying filter on products : " +  ex);
        }
        tableView.getItems().addAll(listProducts);
    }

    private QueryBuilder createQueryForDataType(FilterBox filterBox) {
        String key = String.valueOf(filterBox.getChoiceAttributes().get(filterBox.getChoiceBox().getSelectionModel().getSelectedIndex()).getKey());
        Object value = filterBox.getComboBox().getSelectionModel().getSelectedItem();
        if(value == null) {
            value = filterBox.getComboBox().getEditor().getText();
        }
        String operator = String.valueOf(filterBox.getOperatorBox().getSelectionModel().getSelectedItem());
        switch (OperatorType.getOperatorFromLabel(operator)){
            case EQUAL:
                return QueryBuilders.termQuery(key,value);
            case CONTAINS:
                return QueryBuilders.regexpQuery(key,".*"+value+".*");
            case GREATER:
                return QueryBuilders.rangeQuery(key).gt(value);
            case LESSER:
                return QueryBuilders.rangeQuery(key).lt(value);
            default:
                return QueryBuilders.termQuery(key,value);
        }
    }

    private <T> TableColumn<T, Boolean> getRadioButtonCellColumn() {
        TableColumn<T, Boolean> radioColumn = new TableColumn<>("Select");
        radioColumn.setCellFactory(param -> new TableCell<T, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                RadioButton radioButton = new RadioButton("");
                if (item == null) {
                    radioButton.setSelected(false);
                } else {
                    radioButton.setSelected(item);
                }

                radioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        listProducts.get(getIndex()).setSelected(t1);
                    }
                });

                setGraphic(radioButton);
            }
        });
        radioColumn.setCellValueFactory(new PropertyValueFactory<T, Boolean>("Select"));
        return radioColumn;
    }


}
