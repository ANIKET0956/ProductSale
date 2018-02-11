package backend.screens;

import backend.ProductDetails;
import backend.esutils.ESClient;
import backend.esutils.ESUtils;
import backend.statsDisplay.AddProduct;
import backend.statsDisplay.LookupService;
import backend.statsDisplay.LookupServiceImpl;
import backend.statsDisplay.ProductShow;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GodownScreen extends Application {

    private GodownScreenAttributes godownScreenAttributes;

    public GodownScreen() {
        super();
    }

    @Override
    public void init() throws Exception {
        super.init();
        initAttributes();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        stage.setMaximized(true);

        // Create table view
        TableView tableView = godownScreenAttributes.getTableForScreen();

        // Create hbox above table.
        HBox hBox = godownScreenAttributes.getHBox();

        // Create vbox beside table.
        VBox vBox = godownScreenAttributes.getVBox();
        root.getChildren().addAll(hBox,tableView,vBox);

        Scene scene = new Scene(root, godownScreenAttributes.getScreenWidth(),godownScreenAttributes.getScreenHeight());
        scene.setFill(Color.LIGHTGRAY);
        stage.setScene(scene);
        stage.show();
    }


    // -------------------------------- PRIVATE METHODS -------------------------------------------

    private void initAttributes() {
        ESClient esClient = ESUtils.getDefaultInstance();
        LookupService lookupService = new LookupServiceImpl(esClient);
        godownScreenAttributes = new GodownScreenAttributes(ProductDetails.class,lookupService);
    }

    // --------------------------------- INNER CLASSES --------------------------------------------

    private class GodownScreenAttributes extends AbstractScreenAttributes<ProductDetails> {
        @Override
        protected void populateTableData(TableView tableView) {
            super.populateTableData(tableView);
        }

        @Override
        protected List<Button> addAdditionalButtonsInHBox() {
            final Button addButton = new Button("Add New Product");
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    AddProductScreen addProduct = new AddProductScreen();
                    addProduct.start();
                }
            });
            List<Button> toAdd = new ArrayList<>();
            toAdd.add(addButton);
            return toAdd;
        }

        public GodownScreenAttributes(Class<ProductDetails> productDetailsClass, LookupService<ProductDetails> lookupService) {
            super(productDetailsClass, lookupService);
        }
    }

}
