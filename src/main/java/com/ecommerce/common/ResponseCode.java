package com.ecommerce.common;

// This enum class represents response codes used in the application
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGIN(10, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    // Constructor for ResponseCode enum
    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // Getter method to retrieve the code value
    public int getCode() {
        return code;
    }

    // Getter method to retrieve the description
    public String getDesc() {
        return desc;
    }
}