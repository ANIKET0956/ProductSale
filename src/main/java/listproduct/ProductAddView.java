/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package listproduct;

import java.util.LinkedList;
import java.util.List;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sun.security.pkcs11.Secmod;
import utils.ModuleUtils;

/**
 *
 * @author aniket
 */
public class ProductAddView {

    private List<String> staticText = new LinkedList<>();

    public void showStage() {
        Stage listStage  = new Stage();
        listStage.setTitle("Add Product");

        GridPane gridPane  = new GridPane();
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setHgap(100);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(50,5,5,5));

        addStaticFieldName();

        for (int i=0; i< staticText.size(); i++) {
            gridPane.add(new Label(staticText.get(i)), 0, i);
            gridPane.add(new TextField(), 1, i);
        }

        addButtonForDynamicField(gridPane);

        // Create a new Scene and add root Node
        Scene scene  = new Scene(gridPane, 600, 550);
        listStage.setScene(scene);
        listStage.show();

    }


    // ---------------------  Private Methods --------------------------- //

    private void addStaticFieldName() {
        // List of columns
        staticText.add("Product Name");
        staticText.add("Product Count");
        staticText.add("Prodouct Cost");
    }

    private void addButtonForDynamicField(GridPane grid) {
        Button btn = new Button();
        btn.setText("Add New Column");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                getStageForNewField().show();
            }
        });
        grid.add(btn,1,staticText.size());
    }


    public Stage getStageForNewField() {
        Stage toReturn = new Stage();
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10.0,10,10,10));
        gridPane.add(new Label("Column Name"), 0, 0);
        gridPane.add(new TextField(), 1, 0);
        gridPane.add(new Label("Chose Type"), 0, 1);
        ObservableList<String> fieldTypes = FXCollections.observableArrayList(
                ModuleUtils.transformArrayToList(FieldType.values(),FieldType::getName));
        gridPane.add(new ComboBox(fieldTypes),1,1);
        Scene scene = new Scene(gridPane,300,300);
        toReturn.setScene(scene);
        return toReturn;
    }


}
