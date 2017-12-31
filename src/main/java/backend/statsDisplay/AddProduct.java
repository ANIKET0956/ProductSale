package backend.statsDisplay;

import backend.ProductDetails;
import backend.esutils.ESClient;
import backend.utility.HelperUtils;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.Notifications;
import org.omg.CORBA.NO_IMPLEMENT;

import javax.management.Notification;
import javax.print.DocFlavor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class AddProduct {

    private static final Double SCREEN_WIDTH = 600.0;
    private static final Double SCREEN_HEIGHT = 600.0;
    private ESClient esClient;

    private ObservableList<String> staticText = FXCollections.observableArrayList();
    private Map<String,Node> productValues;


    public AddProduct(ESClient esClient) {
        this.esClient = esClient;
        this.productValues = new HashMap<>();
    }

    public void start() {
        Stage stage  = new Stage();
        stage.setTitle("Adding Product");


        GridPane gridPane  = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(100);

        placeTextField(gridPane);

        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(50,5,5,5));
        Button saveButton = generateSaveButton();
        Button exitButton = generateExitButton(stage);
        saveButton.setTranslateY(10);
        saveButton.setTranslateX(50);
        exitButton.setTranslateY(10);
        exitButton.setTranslateX(-50.0);

        gridPane.add(saveButton,1,staticText.size());
        gridPane.add(exitButton,2,staticText.size());

        Scene scene = new Scene(gridPane,SCREEN_WIDTH,SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.show();

    }

    // ---------- Private Methods -------------------------- //

    private Button generateSaveButton() {
        Button saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean isProductSaved = saveProduct();
                if (isProductSaved) {
                    Notifications.create().text("Added Product")
                            .hideAfter(Duration.millis(1000L)).position(Pos.BOTTOM_CENTER).showInformation();
                }
                refreshTextField();
            }
        });
        return saveButton;
    }

    private Button generateExitButton(Stage onGoingStage) {
        Button saveButton = new Button("Exit");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                    onGoingStage.close();
                }
        });
        return saveButton;
    }


    private void addLabels(Class classType) {
        List<String> fieldNames = HelperUtils.getFieldNameWithIgnoreCase(classType);
        staticText.addAll(fieldNames);
    }

    private void placeTextField(GridPane gridPane) {
        addLabels(ProductDetails.class);
        for (int i = 0; i < staticText.size() ; i++) {
            gridPane.add(new Label(HelperUtils.beautifyName(staticText.get(i))),0, i);
            TextField textField = new TextField(StringUtils.EMPTY);
            productValues.put(staticText.get(i),textField);
            gridPane.add(textField,1,i);
        }
    }

    private boolean saveProduct() {
        try {
            Map<String, Object> toConvert = new HashMap<>();
            for (Map.Entry<String, Node> entry : productValues.entrySet()) {
                toConvert.put(entry.getKey(), ((TextField) entry.getValue()).getText());
            }
            List<String> addValues = productValues.values().stream().map(node -> ((TextField)node).getText()).collect(Collectors.toList());
            if (StringUtils.isBlank(String.join(StringUtils.EMPTY,addValues))){
                return false;
            }
            String productDetails = HelperUtils.convertToString(toConvert);
            esClient.saveDocument("product", "document", String.valueOf(System.currentTimeMillis()), productDetails);
            return true;
        } catch (Exception ex) {
            System.out.println("Error in saving new Product data : " + ex);
        }
        return false;
    }

    private void refreshTextField() {
        for (Map.Entry<String,Node> entry : productValues.entrySet()) {
            ((TextField)entry.getValue()).clear();
        }
    }

}
