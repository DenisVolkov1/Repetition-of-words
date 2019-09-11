package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Word;
import run.AppRun;
import utillity.FieldTextUtil;

import java.io.File;
import java.time.LocalDate;
import java.util.prefs.Preferences;

/**
 * Класс контроллер
 */

public class NewWords {

    private static ObservableList<Word> newWordsList = FXCollections.observableArrayList();
    private static WordsDay wordsDay;
    private static AllWords allWords;
    private static Settings settings;
    private static Preferences prefs;

    static {
        wordsDay = AppRun.getControllerTabWordsDay();
        allWords = AppRun.getControllerTabAllWords();
        settings = AppRun.getControllerTabSettings();
        prefs = settings.getPrefs();
    }
    private final String css = this.getClass().getResource("/view/theme.css").toExternalForm();
    private static final Border ERR_BORDER = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(0,2,0,2),new Insets(0,0,0,0)));
    private Stage stageNewWords;

    @FXML
    private VBox vBoxInScrollPane;
    @FXML
    private void initialize() {

        //add listeners for set normal border in focused.
        ObservableList<Node> vBoxes = vBoxInScrollPane.getChildren();
        for (Node node : vBoxes) {
            VBox vBox = (VBox) node;
            HBox hBox0 = (HBox) vBox.getChildren().get(0);
            HBox hBox1 = (HBox) vBox.getChildren().get(1);
            TextField word = (TextField) hBox0.getChildren().get(1);
            TextField translate = (TextField) hBox1.getChildren().get(1);
            word.focusedProperty().addListener((ob, o, n) -> {
                if (n) word.setBorder(null);
            });
            translate.focusedProperty().addListener((ob, o, n) -> {
                if (n) translate.setBorder(null);
            });
        }
    }
    @FXML
    private void saveHandle() {
        if (!validInputTextFields()) return;
        if (settings.xmlToggleSelected()) {
            File saveFile = settings.getFilePathXML();
            if (saveFile == null) {
                if (settings.saveFileXMLFileChooser() != null) {
                    AllWords.getWords().addAll(newWordsList);
                    settings.saveDataToFileXML(saveFile);

                    String name = saveFile.getName();
                    settings.getNameFileXML().setText(name);
                    settings.setNameBase("XML");
                    //устанавливаем активность кнопок
                    wordsDay.isButtonsEnabled(true);
                    wordsDay.refresh(settings);

                    newWordsList.clear();
                    stageNewWords.close();
                }
            } else {
                AllWords.getWords().addAll(newWordsList);
                settings.saveDataToFileXML(saveFile);
                stageNewWords.close();
            }
        }
        if (settings.jsonToggleSelected()) {
            File saveFile = settings.getFilePathJSON();
            if (saveFile == null) {
                if (settings.saveFileJSONFileChooser() != null) {
                    AllWords.getWords().addAll(newWordsList);
                    settings.saveDataToFileJSON(saveFile);

                    String name = saveFile.getName();
                    settings.getNameFileJSON().setText(name);
                    settings.setNameBase("JSON");
                    //устанавливаем активность кнопок
                    wordsDay.isButtonsEnabled(true);
                    wordsDay.refresh(settings);

                    newWordsList.clear();
                    stageNewWords.close();
                }
            } else {
                AllWords.getWords().addAll(newWordsList);
                settings.saveDataToFileJSON(saveFile);
                stageNewWords.close();
            }
        }
        if (settings.serializableToggleSelected()) {
            AllWords.getWords().addAll(newWordsList);
            settings.saveFileSerializable();
            // устанавливаем имена
            settings.setNameBase("Ser");
            //устанавливаем активность кнопок
            wordsDay.isButtonsEnabled(true);
            boolean isExistFileSerializ = new File("dictonary.base").exists();
            if (!isExistFileSerializ) wordsDay.refresh(settings);
            stageNewWords.close();
        }

        newWordsList.clear();
        allWords.setCurrentCountWords();
    }
    private boolean validInputTextFields() {
        boolean enterError = false;

        setBorderTextFieldNull();
        ObservableList<Node> vBoxes = vBoxInScrollPane.getChildren();
        for (Node node : vBoxes) {
            VBox vBox = (VBox) node;
            HBox hBox0 = (HBox) vBox.getChildren().get(0);
            HBox hBox1 = (HBox) vBox.getChildren().get(1);
            TextField word = (TextField) hBox0.getChildren().get(1);
            TextField translate = (TextField) hBox1.getChildren().get(1);
            if (FieldTextUtil.isEmptyFields(word)) {
                if (!enterError) {
                    enterError = true;
                    alertNotFilled();
                }
                word.setBorder(ERR_BORDER);
            }
            if (FieldTextUtil.isEmptyFields(translate)) {
                if (!enterError) {
                    alertNotFilled();
                    enterError = true;
                }
                translate.setBorder(ERR_BORDER);
            }
            if (FieldTextUtil.isCloneExist(AllWords.getWords(), word)) {
                if (!enterError) {
                    alertExistWord(word.getText());
                    enterError = true;
                }
                word.setBorder(ERR_BORDER);
            }
            if (FieldTextUtil.isCloneExist(newWordsList, word)) {
                if (!enterError) {
                    alertExistWordNewList(word.getText());
                    enterError = true;
                }
                word.setBorder(ERR_BORDER);
            }
            newWordsList.add(new Word(word.getText(), translate.getText(), LocalDate.now()));
        }
        if (enterError) {
            newWordsList.clear();
            return false;
        }
        return true;
    }
    private void setBorderTextFieldNull() {
        ObservableList<Node> vBoxes = vBoxInScrollPane.getChildren();
        for (Node node : vBoxes) {
            VBox vBox = (VBox) node;
            HBox hBox0 = (HBox) vBox.getChildren().get(0);
            HBox hBox1 = (HBox) vBox.getChildren().get(1);
            TextField word = (TextField) hBox0.getChildren().get(1);
            TextField translate = (TextField) hBox1.getChildren().get(1);
            word.setBorder(null);
            translate.setBorder(null);
        }
    }
    private void alertNotFilled() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(stageNewWords);
        alert.setTitle("Empty fields");
        alert.setHeaderText("One or several fields are not filled !!!");
        alert.setContentText("Please fill in all fields.");
        alert.showAndWait();
    }
    private void alertExistWord(String word) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(stageNewWords);
        alert.setTitle("Word already exist");
        alert.setHeaderText("This word (\""+word+"\") already exists!!!");
        alert.setContentText("Please input only new words.");
        alert.showAndWait();
    }
    private void alertExistWordNewList(String word) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(stageNewWords);
        alert.setTitle("Word already exist");
        alert.setHeaderText("This word (\""+word+"\") already input in this form!!!");
        alert.setContentText("Please fill out the form with only new words.");
        alert.showAndWait();
    }
    public void setStageNewWords(Stage stageNewWords) {
        this.stageNewWords = stageNewWords;
    }

    public VBox getvBoxInScrollPane() {
        return vBoxInScrollPane;
    }
}
