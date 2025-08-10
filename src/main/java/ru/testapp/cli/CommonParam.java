package ru.testapp.cli;

public enum CommonParam {
    PATH("path"),
    OUT("out"),
    FROM("from"),
    TO("to");

    private final String value;

    CommonParam(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
