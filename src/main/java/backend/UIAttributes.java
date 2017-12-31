package backend;

import backend.enums.DataType;

public class UIAttributes {
    private String key;
    private String value;
    private DataType dataType;

    public UIAttributes(String key, String value) {
        this(key,value,DataType.STRING);
    }

    public UIAttributes(String key, String value, DataType dataType) {
        this.key = key;
        this.value = value;
        this.dataType = dataType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
