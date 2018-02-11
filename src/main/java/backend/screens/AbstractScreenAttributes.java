package backend.screens;

import backend.ProductDetails;
import backend.statsDisplay.LookupService;
import backend.utility.ModuleUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScreenAttributes<Type> implements ScreenAttributes<Type> {

    private static final Double SCREEN_HEIGHT = 1300.0;
    private static final Double SCREEN_WIDTH = 1800.0;

    private Class<Type> kClass;
    private LookupService<Type> lookupService;
    private TableView table;

    public AbstractScreenAttributes(Class<Type> typeClass, LookupService<Type> lookupService) {
        this.kClass  = typeClass;
        this.lookupService = lookupService;
        this.table = new TableView<>();
    }

    @Override
    public Double getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    @Override
    public Double getScreenWidth() {
        return SCREEN_HEIGHT;
    }

    @Override
    public TableView<Type> getTableForScreen() {
        table.setTranslateX(0);
        table.setTranslateY(200);
        setUpProductTable();
        populateTableData(table);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        return table;
    }

    @Override
    public HBox getHBox() {
        return setupHBox();
    }

    @Override
    public VBox getVBox() {
        return setupVBox();
    }

    protected void populateTableData(TableView tableView) {
        tableView.getItems().clear();
        List<Type> presentProductDetails = lookupService.getPresentProductDetails();
        ObservableList<Type> listProducts = FXCollections.observableArrayList();
        listProducts.addAll(presentProductDetails);
        tableView.getItems().addAll(presentProductDetails);
    }

    protected List<Button> addAdditionalButtonsInHBox() {
        return new ArrayList<>();
    }

    // ---------------------------------------------------- PRIVATE METHODS -----------------------------------------------------

    private void setUpProductTable() {
        table.setEditable(true);
        List<String> fieldNames = ModuleUtils.getFieldNameWithIgnoreCase(kClass);
        double sizeColumn = getScreenWidth()/(fieldNames.size()+0.7);
        TableColumn<Type, Boolean> radioColumn = getRadioButtonCellColumn();
        table.getColumns().add(radioColumn);
        for (String name : fieldNames) {
            TableColumn<Type,String> tableColumn = new TableColumn<>(ModuleUtils.beautifyName(name));
            tableColumn.setMinWidth(sizeColumn);
            tableColumn.setCellValueFactory(new PropertyValueFactory<Type,String>(name));
            table.getColumns().add(tableColumn);
        }
    }

    private TableColumn<Type, Boolean> getRadioButtonCellColumn() {
        TableColumn<Type, Boolean> radioColumn = new TableColumn<>("Select");
        radioColumn.setCellValueFactory(new PropertyValueFactory<Type, Boolean>("Select"));
        return radioColumn;
    }

    private HBox setupHBox() {
        final Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                populateTableData(table);
                table.refresh();
            }
        });

        List<Button> toAdd = new ArrayList<>();
        toAdd.addAll(addAdditionalButtonsInHBox());
        toAdd.add(refreshButton);
        HBox hBox = new HBox();
        hBox.setTranslateX(100.0);
        hBox.setTranslateY(100.0);
        hBox.getChildren().addAll(toAdd);
        hBox.setSpacing(getScreenHeight()/(hBox.getChildren().size()+2));
        return hBox;
    }

    private VBox setupVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10.0);
        vBox.setTranslateX(1300);
        vBox.setTranslateY(100);
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setStyle("-fx-background-color: #FFFFFF;");
        final Button filterButton = new Button("Apply Filters");
        filterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
            }
        });


        HBox plusBox = new HBox();
        Image imageOk = new Image(getClass().getResourceAsStream("/plus.png"));
        ImageView imageView = new ImageView(imageOk);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        Button button = new Button("Add Filter");
        button.setGraphic(imageView);
        plusBox.getChildren().add(button);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                HBox hBox = addFilterBoxinHorizontalRow(vBox);
                Node lastELement  = vBox.getChildren().remove(vBox.getChildren().size()-1);
                vBox.getChildren().add(hBox);
                vBox.getChildren().add(lastELement);
            }
        });

        vBox.getChildren().addAll(filterButton,addFilterBoxinHorizontalRow(vBox),plusBox);
        return vBox;
    }

    private HBox addFilterBoxinHorizontalRow(VBox vBox) {
        HBox hBox = new HBox();
        FilterBox filterBox = new FilterBox(kClass);
        filterBox.getOperatorBox().hide();
        Image imageOk = new Image(getClass().getResourceAsStream("/cross.png"));
        ImageView imageView = new ImageView(imageOk);
        imageView.setFitHeight(10);
        imageView.setFitWidth(10);
        Button crossButton = new Button();
        crossButton.setGraphic(imageView);
        crossButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Node found = findChildNode(vBox, crossButton);
                vBox.getChildren().remove(found);
            }
        });
        hBox.getChildren().addAll(filterBox.getChoiceBox(), filterBox.getOperatorBox(), filterBox.getComboBox(),crossButton);
        return hBox;
    }

    private Node findChildNode(Parent node, Node childToFind) {
        if (node == childToFind) {
            return node;
        }
        List<Node> children = node.getChildrenUnmodifiable();
        if (children.size() > 0) {
            for (Node child : children) {
                if (child instanceof Parent) {
                    Node result = findChildNode((Parent) child, childToFind);
                    if (result != null) {
                        return child;
                    }
                }
            }
        }
        return null;
    }
}


