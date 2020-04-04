package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Word;
import run.AppRun;
import utillity.voiceprovider.VoiceProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class WordsDay {
    private final String css = this.getClass().getResource("/css/theme.css").toExternalForm();
    private Preferences prefs = Preferences.userNodeForPackage(Settings.class);

    private List<Word> wordsDayList = new ArrayList<>();
    private Integer countLastWords;
    private Integer initialCount;

    @FXML
    private Polyline polyline;
    @FXML
    private Label nameBase;
    @FXML
    private Line line;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lastWords;
    @FXML
    private Label word;
    @FXML
    private Label translation;
    @FXML
    private Button refreshButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button prefButton;
    @FXML
    private Button viewTranslationButton;
    @FXML
    private Button newWordsButton;
    @FXML
    private Button voiceButton;

    @FXML
    private void initialize() {
        progressBar.setStyle("-fx-accent: #99ff33;");
        translation.setVisible(false);
        viewTranslationButton.setOnMousePressed(n -> {
            if (translation.getText().length() > 34) {
                translation.setFont(Font.font("System", FontWeight.BOLD, 13));
            }
            translation.setVisible(true);
        });
        viewTranslationButton.setOnMouseReleased(n -> {
            translation.setFont(Font.font("System", FontWeight.BOLD, 15));
            translation.setVisible(false);
        });
    }
    public void isButtonsEnabled(Boolean b) {
        if (b) {
            lastWords.setVisible(true);
            word.setVisible(true);
                viewTranslationButton.setDisable(false);
                voiceButton.setDisable(false);
                    nextButton.setDisable(false);
                    prefButton.setDisable(false);
            refreshButton.setDisable(false);
            line.setVisible(true);
            newWordsButton.setText("New words");
            polyline.setOpacity(0.4);
        } else {
            lastWords.setVisible(false);
            word.setVisible(false);
                viewTranslationButton.setDisable(true);
                voiceButton.setDisable(true);
                    nextButton.setDisable(true);
                    prefButton.setDisable(true);
            refreshButton.setDisable(true);
            line.setVisible(false);
            polyline.setOpacity(0.14);
            newWordsButton.setText("New base..");
        }
    }
    @FXML
    private void refreshHandle() {
        refresh(AppRun.getControllerTabSettings());
    }
    @FXML
    public void prefHandle() {
        if ((countLastWords+1) <= initialCount) {
            ++countLastWords;
            setProgressInProgressBar();
            viewWordForIndex(countLastWords);
            lastWords.setText((countLastWords).toString());
            prefs.putInt("CountLastWords", countLastWords);
        }
        boolean b = prefs.getBoolean("AutoVoiceScrolling", false);
        if (b) voicePlay();
    }
    @FXML
    public void nextHandle() {
        if ((countLastWords-1) >= 0) {
            --countLastWords;
            setProgressInProgressBar();
            viewWordForIndex(countLastWords);
            lastWords.setText(countLastWords.toString());
            //
            prefs.putInt("CountLastWords", countLastWords);
        } else {
            alertIsEnd();
            wordsDayList.clear();
            refresh(AppRun.getControllerTabSettings());
        }
        boolean b = prefs.getBoolean("AutoVoiceScrolling", false);
        if (b) voicePlay();
    }
    @FXML
    public void voicePlay() {
        File theDir = new File("voice");
        if (!theDir.exists()) theDir.mkdir();
        File fileMp3WordPronounce = new File("voice/"+word.getText()+".mp3");
        if (!fileMp3WordPronounce.exists()) {
            try {
               if(VoiceProvider.createFileMp3WordPronounce(word.getText()) == -1) return;
            } catch (IOException e) {
               alertIOErr(e.getMessage());
               return;
            }
        }
        String uriString = fileMp3WordPronounce.toURI().toString();
        AudioClip player = new AudioClip(uriString);
        player.play();

    }
    @FXML
    private void newWordsHandle() {
        Settings settings = AppRun.getControllerTabSettings();
        FXMLLoader loader = new FXMLLoader(AppRun.class.getResource("/view/new_words/NewWords.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = (AnchorPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage dialogStage = new Stage();
        dialogStage.getIcons().add(new Image("img/icon.png"));
        dialogStage.setTitle("New Words");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(AppRun.getMainStage());
        Scene scene = new Scene(anchorPane);
        //
        NewWords controller = loader.getController();
        controller.setStageNewWords(dialogStage);
        // count fields input
        Integer countRemovedFields = settings.countNewWords();
        controller.getvBoxInScrollPane().getChildren().remove(countRemovedFields,10);

        dialogStage.setScene(scene);
        dialogStage.showAndWait();

    }
    public void viewWordForIndex(Integer index) {
        Word wordByIndex = wordsDayList.get(index);
        word.setText(wordByIndex.getWord());
        translation.setText(wordByIndex.getTranslate());
    }
    public void refresh(Settings settings) {
        wordsDayList.clear();
        if (!settings.saveWordsForDay()) return;

        Integer countAllWords = AllWords.getWords().size();
        Integer settingsCountDayWords = settings.countDayWords();

        if (countAllWords < settingsCountDayWords) {
            countLastWords = countAllWords-1;
            initialCount = countLastWords;
            lastWords.setText(countLastWords.toString());

            wordsDayList.addAll(AllWords.getWords());
            Collections.shuffle(wordsDayList);
        } else {
            countLastWords = settingsCountDayWords-1;
            initialCount = countLastWords;

            lastWords.setText(countLastWords.toString());
            Integer fromIndex = AllWords.getWords().size() - settings.countDayWords();
            Integer toIndex = AllWords.getWords().size();
            List<Word> temper = AllWords.getWords().subList(fromIndex, toIndex);

            wordsDayList.addAll(temper);
            Collections.shuffle(wordsDayList);
        }
        viewWordForIndex(countLastWords);
        //progressBar bar
        setProgressInProgressBar();
    }
    public void setProgressInProgressBar() {
        Double progressStep = 1.0 / initialCount;
        Integer multplCount = initialCount - countLastWords;
        Double progress = progressStep * multplCount;
        progressBar.setProgress(progress);
    }
    private void alertIsEnd() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("End");
        alert.setHeaderText("The end!!!");
        alert.showAndWait();
    }
    private void alertIOErr(String messageExeption) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Input Output Err");
        alert.setHeaderText("Error : "+messageExeption);
        alert.showAndWait();
    }
    public Label getNameBase() {
        return nameBase;
    }

    public List<Word> getWordsDayList() {
        return wordsDayList;
    }
    public void setWordsDayList(List<Word> wordsDayList) {
        this.wordsDayList = wordsDayList;
    }

    public Label getTranslation() {
        return translation;
    }

    public void setCountLastWords(Integer countLastWords) {
        this.countLastWords = countLastWords;
    }
    public Label getLastWords() {
        return lastWords;
    }
}
