package backend.screens;

import backend.ProductDetails;
import backend.esutils.ESClient;
import backend.esutils.ESUtils;
import backend.statsDisplay.LookupService;
import backend.statsDisplay.LookupServiceImpl;
import backend.utility.ModuleUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.Notifications;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddProductScreen {

    private static final Double SCREEN_WIDTH = 600.0;
    private static final Double SCREEN_HEIGHT = 600.0;
    private final LookupService lookupService;


    private Map<String,Node> productValues = new HashMap<>();


    public AddProductScreen() {
        ESClient esClient = ESUtils.getDefaultInstance();
        this.lookupService = new LookupServiceImpl(esClient);
    }

    public void start() {
        Stage stage  = new Stage();
        stage.setTitle("Adding Product");

        ObservableList<String> staticText = FXCollections.observableArrayList();

        GridPane gridPane  = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(100);

        placeTextField(gridPane, staticText);

        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(50,5,5,5));
        HBox buttonBox = new HBox();
        Button saveButton = generateSaveButton();
        Button exitButton = generateExitButton(stage);
        buttonBox.getChildren().addAll(saveButton,exitButton);

        gridPane.addRow(staticText.size(),buttonBox);

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


    private void addLabels(List<String> staticText, Class classType) {
        List<String> fieldNames = ModuleUtils.getFieldNameWithIgnoreCase(classType);
        staticText.addAll(fieldNames);
    }

    private void placeTextField(GridPane gridPane, List<String> staticText) {
        addLabels(staticText,ProductDetails.class);
        for (int i = 0; i < staticText.size() ; i++) {
            gridPane.add(new Label(ModuleUtils.beautifyName(staticText.get(i))),0, i);
            TextField textField = new TextField(StringUtils.EMPTY);
            productValues.put(staticText.get(i),textField);
            gridPane.add(textField,1,i);
        }
    }

    private void refreshTextField() {
        for (Map.Entry<String,Node> entry : productValues.entrySet()) {
            ((TextField)entry.getValue()).clear();
        }
    }
}
