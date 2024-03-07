package com.palacio.webservicegui.Connection;

import com.palacio.webservicegui.LogConfig.LogConfig;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.palacio.webservicegui.Properties.ConfigProperties.*;
import static com.palacio.webservicegui.ResponseHandler.ResponseHandler.generalHandler;

public class FirstConnection {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());
    public static int RESPONSE;

    public static String AUTH_X;

    public static void connectURL_1(String urlCon) {

        try {
            URL url = new URL(urlCon);

            String jsonPayload = "{\"user\": \"" + USER + "\", \"password\": \"" + PASSWORD + "\"}";
            logger.log(Level.INFO, "Intentando obtener el token de autenticación...");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(jsonPayload);
                wr.flush();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                String token = "X-AUTH-TOKEN";
                try {
                    String[] headerFields = connection.getHeaderField(token).split(";");
                    String authToken = headerFields[0];
                    AUTH_X = authToken;
                } catch(Exception e) {
                    RESPONSE = 92;
                    logger.log(Level.SEVERE, "ERROR {0}", token);
                    generalHandler(RESPONSE, e);
                }

                if (AUTH_X == null) {
                    logger.log(Level.INFO, "Token no extraído");
                } else {
                    logger.log(Level.INFO, "Token extraído correctamente.");
                }
            } catch (IOException e) {
                RESPONSE = 91;
                generalHandler(RESPONSE, e);
            }
        } catch (IOException e) {
            RESPONSE = 90;
            generalHandler(RESPONSE, e);
        }
    }
}
