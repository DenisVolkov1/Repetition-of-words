package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Word;
import run.AppRun;
import utillity.DateUtil;


import java.io.IOException;
import java.time.LocalDate;

public class AllWords {
    private final String css = this.getClass().getResource("/css/theme.css").toExternalForm();
    private static ObservableList<Word> words = FXCollections.observableArrayList();

    @FXML
    private Label nameBase;
    @FXML
    private Label countWords;
    @FXML
    private TableView<Word> tableAll;
    @FXML
    private TableColumn<Word, String> dateCreationColumn;
    @FXML
    private TableColumn<Word, String> wordColumn;
    @FXML
    private TableColumn<Word, String> translateColumn;
    @FXML
    private void initialize() {
       dateCreationColumn.setCellValueFactory(cellData -> cellData.getValue().getDateCreationProperty());
       wordColumn.setCellValueFactory(cellData -> cellData.getValue().wordProperty());
       translateColumn.setCellValueFactory(cellData -> cellData.getValue().translateProperty());
       tableAll.setItems(words);
       setCurrentCountWords();
    }
    @FXML
    private void editHandle() throws IOException {
        if (AllWords.getWords().size() == 0) {
            alertNotBase();
            return;
        }
        FXMLLoader loader = new FXMLLoader(AppRun.class.getResource("/view/EditWords.fxml"));
        AnchorPane anchorPane = (AnchorPane) loader.load();
        // Создаём диалоговое окно Stage.
        Stage dialogStage = new Stage();
        dialogStage.getIcons().add(new Image("img/icon.png"));

        Word word = selectedWord();
        if (word != null) {
            //Date creation word
            LocalDate localDate = word.getDateCreation();
            dialogStage.setTitle("Edit Word to : " + DateUtil.format(localDate));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(AppRun.getMainStage());
            Scene scene = new Scene(anchorPane);
            dialogStage.setScene(scene);
            //
            EditDialog controller = loader.getController();
            controller.setDialogStage(dialogStage);
            // передача выбраного слова из таблицы
            controller.setSelectedWord(word);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();
        } else return;
    }
    @FXML
    private void deleteWordHandle() {
       Settings settings = AppRun.getControllerTabSettings();
        if (AllWords.getWords().size() == 0) {
            alertNotBase();
            return;
        }
        if (AllWords.getWords().size() == 1) {
            alertOneWord();
            return;
        }
       Word word = selectedWord();
        if (word != null) {
            words.remove(word);
            if (settings.xmlToggleSelected()) settings.saveDataToFileXML(settings.getFilePathXML());
            if (settings.serializableToggleSelected()) settings.saveFileSerializable();
            if (settings.jsonToggleSelected()) settings.saveDataToFileJSON(settings.getFilePathJSON());
            setCurrentCountWords();
        }
    }
    private Word selectedWord() {
        Word word = tableAll.getSelectionModel().getSelectedItem();
        if (word != null) return word;
        else {
            alertNoSelection();
            return null;
        }
    }
    private void alertNoSelection() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("No Selection");
        alert.setHeaderText("No word Selected!!!");
        alert.setContentText("Please select the word in the table.");
        alert.showAndWait();
    }
    private void alertNotBase() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Empty base");
        alert.setHeaderText("Your base is empty !!!");
        alert.setContentText("Load base or create her.");
        alert.showAndWait();
    }
    private void alertOneWord() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Last word");
        alert.setHeaderText("In your base is one word !!!");
        alert.setContentText("Base must contain at least 1 word.");
        alert.showAndWait();
    }
    public static boolean duplicateCheck(Word word) {
       return words.contains(word);
    }
    public static ObservableList<Word> getWords() {
        return words;
    }
    public void setCurrentCountWords() {
       countWords.setText(String.valueOf(words.size()));
    }
    public Label getNameBase() {
        return nameBase;
    }
    }
