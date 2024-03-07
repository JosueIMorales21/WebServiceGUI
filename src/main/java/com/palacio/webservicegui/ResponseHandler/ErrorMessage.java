package com.palacio.webservicegui.ResponseHandler;

public class ErrorMessage {

    static private boolean error = false;
    static private String errMsg = "";

    public ErrorMessage() {
    }

    public ErrorMessage(boolean error, String errMsg) {

        ErrorMessage.error = error;
        ErrorMessage.errMsg = errMsg;
    }

    public static boolean isError() {
        return error;
    }

    public static String getErrMsg() {
        return errMsg;
    }
}
