package listproduct;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aniket
 */
public enum FieldType {
    RAW_VALUE("Value"),
    METRIC("Measurement"),
    TIME("Time");

    private String name;

    FieldType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

