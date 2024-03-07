package com.palacio.webservicegui.Connection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.palacio.webservicegui.LogConfig.LogConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonElement;

import static com.palacio.webservicegui.Connection.FirstConnection.AUTH_X;
import static com.palacio.webservicegui.Connection.FirstConnection.RESPONSE;
import static com.palacio.webservicegui.ResponseHandler.ResponseHandler.generalHandler;

public class SecURL {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());
    public static Set<String> outPut = new HashSet<>();

    public static JsonElement jsonResponse;

    public static void connectToURL(String urlCon, String cardId) throws Exception{

        try {
            logger.log(Level.INFO, "Intentando conectar con la tarjeta: {0}", lastFour(cardId));
            lastFour(cardId);

            URL url = new URL(urlCon);

            String jsonPayload = "{\"cardId\": \"" + cardId + "\"}";

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-AUTH-TOKEN", AUTH_X);
            connection.setRequestProperty("ENTITY", "PH_ARCAJS");
            connection.setRequestProperty("IP_HOST_ORIGIN", "MJ06201");
            connection.setRequestProperty("FUNCTIONALITY", "SEARCHCARDS");

            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                jsonResponse = JsonParser.parseString(response.toString());
                try {
                    JsonObject jsonObject = jsonResponse.getAsJsonObject();
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");
                    try {
                        if (dataArray.isEmpty()) {
                            throw new Exception("Tarjeta no encontrada.");
                        }
                    } catch (Exception e) {
                        RESPONSE = 93;
                        generalHandler(RESPONSE, e);
                        throw new Exception();
                    }

                    try {
                        JsonObject dataObject = dataArray.get(0).getAsJsonObject();
                        String resp = dataObject.get("responseCodeId").getAsString();
                        outPut.add(resp);
                        logger.log(Level.INFO, "Consulta realizada exitosamente. Código de autorización: {0}", resp);
                    } catch (Exception e) {
                        RESPONSE = 92;
                        generalHandler(RESPONSE, e);
                        throw new Exception();
                    }

                } catch (Exception e) {
                    RESPONSE = 93;
                    generalHandler(RESPONSE, e);
                    throw new Exception();
                }
            } catch (IOException e) {
                RESPONSE = 91;
                generalHandler(RESPONSE, e);
                throw new Exception();
            }
        } catch (Exception e) {
            RESPONSE = 90;
            generalHandler(RESPONSE, e);
            throw new Exception();
        }
    }

    public static void validateCard(String cardId) throws Exception {
        if (cardId.length() == 16) {
            String lastFour = cardId.substring(Math.max(0, cardId.length() - 4));
            logger.log(Level.INFO, "Tarjeta a utilizar: {0}", lastFour);
        } else {
            throw new Exception("Tarjeta no reconocida");
        }
    }

    public static String lastFour(String cardId) {
        return cardId.substring(Math.max(0, cardId.length() - 4));
    }

}
