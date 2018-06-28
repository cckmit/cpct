package com.zjtelcom.cpct.validator;

public class ValidateResult {
    private Boolean result;
    private String code;
    private String message;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public boolean getResult() {
        return result;
    }
    public void setResult(Boolean result) {
        this.result = result;
    }
}
