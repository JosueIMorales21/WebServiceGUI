package com.palacio.webservicegui.ResponseHandler;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ValidateTextField {

    private int currentCursorCaretPosition = 0;
    private int oldCursorCaretPosition = 0;

    static final int BACK_SPACE = 8;
    static final int DEL = 127;
    static final int ENTER = 13;

    public ErrorMessage validateDigit(TextField textField, KeyEvent event, int textFieldMaxInput) {

        boolean error = false;
        String errorMsg = "";

        char currentKeyTyped = event.getCharacter().charAt(0);

        final Boolean INVALID_KEY
                = (!Character.isDigit(currentKeyTyped))
                && (currentKeyTyped != BACK_SPACE)
                && (currentKeyTyped != DEL)
                && (currentKeyTyped != ENTER)
                && (!Character.isISOControl(currentKeyTyped));

        currentCursorCaretPosition = textField.getCaretPosition();

        try {
            if (INVALID_KEY) {
                String replaceText = textField.getText().replace(Character.toString(currentKeyTyped), "");

                error = true;

                textField.setText(replaceText);

                textField.positionCaret(currentCursorCaretPosition - 1);
            }

            if (textField.getText().length() > textFieldMaxInput) {
                String textInput = textField.getText();

                error = true;
                errorMsg = "Sólo se aceptan 16 caracteres.";

                StringBuilder str = new StringBuilder(textInput);
                StringBuilder limitTextInput = str.deleteCharAt(currentCursorCaretPosition - 1);

                textField.setText(limitTextInput.toString());

                textField.positionCaret(currentCursorCaretPosition - 1);
            }

            setOldCursorCaretPosition(textField.getCaretPosition());

            return new ErrorMessage(error, errorMsg);
        } catch (Exception e) {
            return new ErrorMessage(true, "ERROR");
        }
    }


    public void setOldCursorCaretPosition(int oldCursorCaretPosition) {
        this.oldCursorCaretPosition = oldCursorCaretPosition;
    }
}