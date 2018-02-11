package backend.screens;

import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public interface ScreenAttributes<Type> {

    Double getScreenHeight();

    Double getScreenWidth();

    TableView<Type> getTableForScreen();

    HBox getHBox();

    VBox getVBox();

}
