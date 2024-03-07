package com.palacio.webservicegui;

import com.palacio.webservicegui.LogConfig.LogConfig;
import com.palacio.webservicegui.ResponseHandler.ErrorMessage;
import com.palacio.webservicegui.ResponseHandler.ValidateTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.palacio.webservicegui.Connection.FirstConnection.*;
import static com.palacio.webservicegui.Connection.SearchNewCard.*;
import static com.palacio.webservicegui.Connection.SecURL.*;
import static com.palacio.webservicegui.Properties.ConfigProperties.*;


public class Controller {

    private static final Logger logger = Logger.getLogger(LogConfig.class.getName());

    private final StringProperty ipDesarrollo = new SimpleStringProperty(IP_DESARROLLO + " (DESARROLLO)");
    private final StringProperty ipProduccion = new SimpleStringProperty(IP_PRODUCCION + " (PRODUCCIÓN)");

    @FXML
    private RadioButton URL_1_BUTTON;
    @FXML
    private RadioButton URL_2_BUTTON;
    @FXML
    private RadioButton card_M;
    @FXML
    private RadioButton card_A;
    @FXML
    public TextField cardId_tf;
    @FXML
    public Button submit_button;
    @FXML
    public Button clean_button;
    @FXML
    public Label labelErrorMsg;
    @FXML
    public Label labelResponse;
    @FXML
    private ToggleGroup toggleGroup;
    @FXML
    private ToggleGroup toggleGroup2;

    static final int TEXT_FIELD_MAX_INPUT = 16;
    ValidateTextField validateTextField = new ValidateTextField();
    ErrorMessage errorMessage = new ErrorMessage();

    @FXML
    public void radioButtonsConfig() {
        toggleGroup = new ToggleGroup();
        toggleGroup2 = new ToggleGroup();

        URL_1_BUTTON.setToggleGroup(toggleGroup);
        URL_2_BUTTON.setToggleGroup(toggleGroup);
        URL_1_BUTTON.textProperty().bind(ipDesarrollo);
        URL_2_BUTTON.textProperty().bind(ipProduccion);

        card_M.setToggleGroup(toggleGroup2);
        card_A.setToggleGroup(toggleGroup2);
    }

    @FXML
    public void handleAcceptButton(ActionEvent event) {
        outPut.clear();
        Toggle selectedToggle = toggleGroup.getSelectedToggle();
        Toggle selectedType = toggleGroup2.getSelectedToggle();
        String selectedToken = "";
        String selectedCardType = "";
        String selectedUrl = "";
        String selectedCardSearch = "";
        String userURL = "";
        if (selectedToggle != null) {
            String cardId = cardId_tf.getText();
            OLD_CARD = cardId;
            try {
                 try {
                     validateCard(cardId);
                 } catch (Exception e) {
                     logger.log(Level.SEVERE, "Dígitos de tarjeta insuficientes.");
                     labelErrorMsg.setText("Dígitos insuficientes.");
                     return;
                 }
                RadioButton selectedRadioButton = (RadioButton) selectedToggle;
                selectedToken = selectedRadioButton.getText();

                RadioButton selectedTypeCard = (RadioButton) selectedType;
                selectedCardType = selectedTypeCard.getText();

                if (selectedToken.equals("10.10.208.36 (DESARROLLO)")) {
                    selectedToken = URL_TOKEN_DESARROLLO;
                    selectedUrl = URL_DESARROLLO;
                    selectedCardSearch = URL_CARD_DESARROLLO;
                    userURL = ipDesarrollo.get();
                } else if (selectedToken.equals("10.10.1.7 (PRODUCCIÓN)")) {
                    selectedToken = URL_TOKEN_PRODUCCION;
                    selectedUrl = URL_PRODUCCION;
                    selectedCardSearch = URL_CARD_PRODUCCION;
                    userURL = ipProduccion.get();
                }
                logger.log(Level.INFO, "URL seleccionada: {0}", userURL);
                try {
                    connectURL_1(selectedToken);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error al obtener el token {0}", e);
                    labelResponse.setText("SIN RESPUESTA");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error al obtener las URL {0}", e);
                labelResponse.setText("SIN RESPUESTA");
            }
            if (AUTH_X != null) {
                try {
                    searchCards(selectedCardSearch, cardId, selectedCardType);
                    if (cancelResult.isEmpty() && !infoResult.isEmpty()) {
                        for (String element: cardResult) {
                            try {
                                connectToURL(selectedUrl, element);
                            } catch (Exception e) {
                                logger.log(Level.SEVERE, "Error al intentar conectar con la tarjeta: {0} {1}", new Object[]{lastFour(element), e});
                            }
                        }
                    } else if (!cancelResult.isEmpty() && infoResult.isEmpty()) {
                        try {
                            connectToURL(selectedUrl, OLD_CARD);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error al intentar conectar con la tarjeta: {0} {1}", new Object[]{lastFour(OLD_CARD), e});
                        }
                    }

                    if (infoResult.isEmpty() && !cancelResult.isEmpty()) {
                        List<String> cancel = new ArrayList<>(cancelResult);
                        List<String> outPutList = new ArrayList<>(outPut);
                        String response = "STATUS: CANCELADA \n" + cancel.get(0) + "-" + outPutList.get(0);
                        labelResponse.setText(response);
                    } else {
                        List<String> infoResultList = new ArrayList<>(infoResult);
                        List<String> outPutList = new ArrayList<>(outPut);
                        StringBuilder responseText = new StringBuilder();
                        for (int i = 0; i < outPut.size(); i++) {
                            responseText.append(infoResultList.get(i)).append("-").append(outPutList.get(i)).append("\n");
                        }
                        labelResponse.setText(responseText.toString());
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error al conectar a la URL: {0}", userURL);
                    labelResponse.setText("SIN RESPUESTA");
                }
            } else {
                logger.log(Level.SEVERE, "Token no obtenido.");
                labelResponse.setText("SIN RESPUESTA");
            }
        } else {
            logger.log(Level.SEVERE, "No se ha seleccionado ninguna URL.");
            labelResponse.setText("SIN RESPUESTA");
        }
    }

    @FXML
    void textFieldClicked(MouseEvent event) {
        displayErrMsg("");

        validateTextField.setOldCursorCaretPosition(cardId_tf.getCaretPosition());
    }

    @FXML
    void validateTextFieldKeyTyped(KeyEvent event) {

        displayErrMsg("");

        errorMessage = validateTextField.validateDigit(cardId_tf, event, TEXT_FIELD_MAX_INPUT);
        if (ErrorMessage.isError()) {
            displayErrMsg(ErrorMessage.getErrMsg());
        }
    }

    void displayErrMsg(String msg) {
        labelErrorMsg.setText(msg);
    }

    @FXML
    void clearTextField() {
        cardId_tf.clear();
        labelResponse.setText("");
        labelErrorMsg.setText("");
        outPut.clear();
    }

}
