package backend.callbacks;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

public class RadioCellCallback<T> implements Callback<TableColumn<T, Boolean>, TableCell<T, Boolean>> {

    private ChangeListener<Boolean> changeListener;

    public RadioCellCallback(ChangeListener<Boolean> changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public TableCell<T, Boolean> call(TableColumn<T, Boolean> tBooleanTableColumn) {
        return new TableCell<T, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                RadioButton radioButton = new RadioButton(StringUtils.EMPTY);
                if (item == null) {
                    radioButton.setSelected(false);
                } else {
                    radioButton.setSelected(item);
                }
                getIndex();
                radioButton.selectedProperty().addListener(changeListener);
                setGraphic(radioButton);
            }
        };
    }
}
