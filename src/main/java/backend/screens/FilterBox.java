package backend.screens;

import backend.ProductDetails;
import backend.UIAttributes;
import backend.enums.DataType;
import backend.enums.OperatorType;
import backend.utility.ModuleUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import org.elasticsearch.common.collect.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterBox<Type> {
    private ChoiceBox choiceBox;
    private ComboBox comboBox;
    private ChoiceBox operatorBox;

    private List<UIAttributes> choiceAttributes = new ArrayList<>();

    public FilterBox(Class<Type> clazz) {
        choiceBox =  new ChoiceBox();
        comboBox = new ComboBox();
        operatorBox = new ChoiceBox();
        comboBox.setEditable(true);
        fillChoicesForFiltering(clazz);
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(returnChoiceBoxListener());
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

    private void fillChoicesForFiltering(Class kclass) {
        List<Tuple<String, DataType>> fieldName = ModuleUtils.getFieldsFromClassWithType(kclass);
        for (Tuple<String,DataType> tuple : fieldName) {
            choiceAttributes.add(new UIAttributes(tuple.v1(), ModuleUtils.beautifyName(tuple.v1()),tuple.v2()));
        }
        choiceBox.getItems().addAll(fieldName.stream().map(t -> ModuleUtils.beautifyName(t.v1())).collect(Collectors.toList()));
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

    private ChangeListener returnChoiceBoxListener() {
        return new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                Number number = ((Number)t1);
                Object selectedItem = choiceAttributes.get(number.intValue()).getKey();
                operatorBox.getItems().clear();
                operatorBox.getItems().addAll(fillChoicesForOperator(choiceAttributes.get(number.intValue()).getDataType()));
            }
        };
    }
}
