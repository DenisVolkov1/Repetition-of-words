package controllers;


import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Word;
import run.AppRun;
import utillity.FieldTextUtil;


public class EditDialog {

    private final String css = this.getClass().getResource("/view/theme.css").toExternalForm();
    private static final Border ERR_BORDER = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0,2,0,2),new Insets(0,0,0,0)));
    private Stage dialogStage;
    private Word selectedWord;

    @FXML
    private TextField textFieldWord;
    @FXML
    private TextField textFieldTranslate;
    @FXML
    private void initialize() {
        textFieldWord.focusedProperty().addListener((ob, o, n) -> {
            if (n) textFieldWord.setBorder(null);
        });
        textFieldTranslate.focusedProperty().addListener((ob, o, n) -> {
            if (n) textFieldTranslate.setBorder(null);
        });
    }
    @FXML
    private void okHandle() {
        setBorderTextFieldNull();
        //edit exist word
        String fieldSelectedWord = selectedWord.getWord();
        String inputWord = textFieldWord.getText();
        String inputTranslate = textFieldTranslate.getText();
        //correct input
        if (fieldSelectedWord.equals(inputWord)) {
            selectedWord.setTranslate(inputTranslate);
            dialogStage.close();
        } else {
            boolean enterError = false;

            if (FieldTextUtil.isEmptyFields(textFieldWord)) {
                if(!enterError) {
                    alertNotFilled();
                    enterError = true;
                }
                textFieldWord.setBorder(ERR_BORDER);
            }
            if (FieldTextUtil.isEmptyFields(textFieldTranslate)) {
                if(!enterError) {
                    alertNotFilled();
                    enterError = true;
                }
                textFieldTranslate.setBorder(ERR_BORDER);
            }
            Word temper = new Word(inputWord, inputTranslate, selectedWord.getDateCreation());
            if (AllWords.duplicateCheck(temper)) {
                if(!enterError) {
                    alertExistSelectedWord();
                    enterError = true;
                }
                textFieldWord.setBorder(ERR_BORDER);
            }
            if (enterError) return;

            selectedWord.setWord(textFieldWord.getText());
            selectedWord.setTranslate(textFieldTranslate.getText());
            //save bases
            Settings settings = AppRun.getControllerTabSettings();
            AllWords allWords = AppRun.getControllerTabAllWords();
            if (settings.xmlToggleSelected()) settings.saveDataToFileXML(settings.getFilePathXML());
            if (settings.jsonToggleSelected()) settings.saveDataToFileJSON(settings.getFilePathJSON());
            if (settings.serializableToggleSelected()) settings.saveFileSerializable();

            allWords.setCurrentCountWords();
            dialogStage.close();
        }
    }
    private void setBorderTextFieldNull() {
        textFieldWord.setBorder(null);
        textFieldTranslate.setBorder(null);
    }
    private void alertExistSelectedWord() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(dialogStage);
        alert.setTitle("Exist selected word");
        alert.setHeaderText("This selected word already exists!!!");
        alert.setContentText("Please input new word.");
        alert.showAndWait();
    }
    private void alertNotFilled() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(dialogStage);
        alert.setTitle("Empty fields");
        alert.setHeaderText("One or several fields are not filled !!!");
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSelectedWord(Word selectedWord) {
        this.selectedWord = selectedWord;
        String word = selectedWord.getWord();
        String translateWord = selectedWord.getTranslate();
        textFieldWord.setText(word);
        textFieldTranslate.setText(translateWord);
    }
}
