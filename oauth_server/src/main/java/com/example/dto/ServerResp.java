package com.example.dto;

import lombok.Data;

/**
 * @author Liangzhifeng
 */
@Data
public class ServerResp {

    /**
     * 返回信息code对应的tag
     */
    public static final String RESPONSE_CODE_TAG = "respCode";

    /**
     * 返回信息message对应的tag
     */
    public static final String RESPONSE_MESSAGE_TAG = "respMessage";

    /**
     * 成功code
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 失败code
     */
    public static final int ERROR_CODE = 500;

    /**
     * 登陆失败code
     */
    public static final int LOGIN_ERROR_CODE = 401;


    private int respCode = SUCCESS_CODE;

    private String respMessage;

    private String respRemark;

    private Object data;

    private int total;

    public ServerResp() {

    }

    public ServerResp(String message, int code, String remark, Object data, int total) {
        this.respCode = code;
        this.respMessage = message;
        this.respRemark = remark;
        this.data = data;
        this.total = total;
    }

    public ServerResp(String message, int code, String remark, Object data) {
        this.respCode = code;
        this.respMessage = message;
        this.respRemark = remark;
        this.data = data;
    }

    public ServerResp(String message, int code, String remark) {
        this.respCode = code;
        this.respMessage = message;
        this.respRemark = remark;
    }

    public ServerResp(String message, int code, Object data) {
        this.respCode = code;
        this.respMessage = message;
        this.data = data;
    }


    public ServerResp(Object data, int total) {
        this.data = data;
        this.total = total;
    }

    public ServerResp(String respMessage, int respCode) {
        this.respMessage = respMessage;
        this.respCode = respCode;
    }

    public ServerResp(Object data) {
        this.data = data;
    }

    public static ServerResp error(String respMessage){
        ServerResp resp = new ServerResp();
        resp.respMessage = respMessage;
        resp.setRespCode(ERROR_CODE);
        return resp;
    }
}
