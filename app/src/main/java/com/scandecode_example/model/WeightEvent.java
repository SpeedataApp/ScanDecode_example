package com.scandecode_example.model;

/**
 * @author :xu in  2018/1/29 12:32.
 *         联系方式:QQ:2419646399
 *         功能描述:
 */
public class WeightEvent {

    private String message;

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public WeightEvent(String message) {
        this.message = message;
    }

    public WeightEvent(String message, String data) {
        this.message = message;
        this.data = data;
    }


    @Override
    public String toString() {
        return "WeightEvent{event ='" + this.message + '}';
    }

}
