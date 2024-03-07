package com.palacio.webservicegui.ResponseHandler;

import com.palacio.webservicegui.LogConfig.LogConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.palacio.webservicegui.Connection.FirstConnection.RESPONSE;

public class ResponseHandler {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());

    public static void generalHandler(int response, Exception e) {

        if (response >= 90 && response <= 99) {
            negativeHandler(response, e);
        } else {
            positiveHandler(response);
        }
    }

    private static void negativeHandler(int response, Exception e) {

        String result = null;

        switch (response) {
            case 90:
                result = "SIN CONEXIÓN " + e;
                break;
            case 91:
                result = "CREDENCIALES INCORRECTAS " + e;
                break;
            case 92:
                result = "CAMPO NO ENCONTRADO " + e;
                break;
            case 93:
                result = "TARJETA INVÁLIDA " + e;
                break;
            default:
                result = "ERROR NO MANEJADO.";
                break;
        }

        logger.log(Level.SEVERE, "Error {0} {1}", new Object[]{RESPONSE, result});
    }

    public static int positiveHandler(int response) {

        response = RESPONSE;
        return response;
    }
}
