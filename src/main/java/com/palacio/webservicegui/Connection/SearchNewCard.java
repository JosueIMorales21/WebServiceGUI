package com.palacio.webservicegui.Connection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

import static com.palacio.webservicegui.Connection.FirstConnection.AUTH_X;
import static com.palacio.webservicegui.Connection.FirstConnection.RESPONSE;
import static com.palacio.webservicegui.Connection.SecURL.lastFour;
import static com.palacio.webservicegui.ResponseHandler.ResponseHandler.generalHandler;

public class SearchNewCard {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());

    public static Set<String> infoResult = new HashSet<>();
    public static Set<String> cardResult = new HashSet<>();
    public static Set<String> canceledCards = new HashSet<>();
    public static Set<String> cancelResult = new HashSet<>();
    public static String OLD_CARD;

    public static void searchCards(String url_card, String cardId, String cardType) throws Exception {

        try {
            logger.log(Level.INFO, "Analizando tarjeta {0}...", lastFour(cardId));

            URL url = new URL(url_card);

            String jsonPayload = "{\"cardId\": \"" + cardId + "\"}";

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-AUTH-TOKEN", AUTH_X);
            connection.setRequestProperty("ENTITY", "PH_ARCAJS");
            connection.setRequestProperty("IP_HOST_ORIGIN", "MJ06201");
            connection.setRequestProperty("FUNCTIONALITY", "SEARCHCONSOLIDATECUSTOMERCARD");

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

                JsonObject jsonResponse = (JsonObject) JsonParser.parseString(response.toString());
                logger.log(Level.INFO, "RESPONSE: {0}", response.toString());

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

                    infoResult.clear();
                    cardResult.clear();
                    canceledCards.clear();
                    cancelResult.clear();
                    for (JsonElement element : dataArray) {
                        JsonObject dataObject = element.getAsJsonObject();
                        String currentCardId = "";
                        String currentTypeCard = "";
                        String currentStatusCard = "";

                        if (dataObject.has("plasticCardStatus")) {
                            currentStatusCard = dataObject.get("plasticCardStatus").getAsString();
                        }
                        if (dataObject.has("cardType")) {
                            currentTypeCard = dataObject.get("cardType").getAsString();
                        }
                        if (dataObject.has("cardId")) {
                            currentCardId = dataObject.get("cardId").getAsString();
                        }

                        if ("ACTIVE".equals(currentStatusCard) || "INACTIVE".equals(currentStatusCard)) {
                            if (currentTypeCard.equals(cardType)) {
                                String result = currentCardId + "-" + currentStatusCard + "-" + currentTypeCard;
                                infoResult.add(result);
                                cardResult.add(currentCardId);
                            } else {
                                logger.log(Level.WARNING, "Sin coincidencias.");
                                return;
                            }
                        } else if ("CANCEL".equals(currentStatusCard) && OLD_CARD.matches(currentCardId)){
                            String result = currentCardId + "-" + currentStatusCard + "-" + currentTypeCard;
                            cancelResult.add(result);
                            canceledCards.add(currentCardId);
                        }
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

}
