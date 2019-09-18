package run;

import controllers.AllWords;
import controllers.Settings;
import controllers.WordsDay;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.event.FocusEvent;
import java.io.IOException;

public class AppRun extends Application {

    private static AnchorPane anchorPaneAllWords;
    private static AnchorPane anchorPaneWordsDay;
    private static AnchorPane anchorPaneSettings;
    private static Stage mainStage;
    private static FXMLLoader loaderMainApp = new FXMLLoader(AppRun.class.getResource("/view/MainApp.fxml"));
    private static FXMLLoader loaderTabAllWords = new FXMLLoader(AppRun.class.getResource("/view/AllWords.fxml"));
    private static FXMLLoader loaderTabWordsDay = new FXMLLoader(AppRun.class.getResource("/view/WordsDay.fxml"));
    private static FXMLLoader loaderTabSettings = new FXMLLoader(AppRun.class.getResource("/view/Settings.fxml"));

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("icon.png"));
        mainStage = stage;
        //main pane
        TabPane tabPaneMain = (TabPane) loaderMainApp.load();
        tabPaneMain.getTabs().get(0).setContent(loadTabAllWords());
        tabPaneMain.getTabs().get(1).setContent(loadTabWordsDay());
        tabPaneMain.getTabs().get(2).setContent(loadTabSettings());
        // Отображаем сцену, содержащую корневой макет.
        Scene mainScene = new Scene(tabPaneMain);
        // add listeners for space , right , left key handle
        tabPaneMain.getTabs().get(1).getContent().addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            boolean tabWordsDayIsFocuse = tabPaneMain.getTabs().get(1).isSelected();
            WordsDay wordsDay = loaderTabWordsDay.getController();

            if (event.getCode() == KeyCode.SPACE) {
                if ( wordsDay.getTranslation().getText().length() > 34) {
                    wordsDay.getTranslation().setFont(Font.font("System", FontWeight.BOLD, 13));
                }
                wordsDay.getTranslation().setVisible(true);
            }
            if (event.getCode() == KeyCode.RIGHT && tabWordsDayIsFocuse) {
                wordsDay.nextHandle();
            }
            if (event.getCode() == KeyCode.LEFT && tabWordsDayIsFocuse) {
                wordsDay.prefHandle();
            }
            event.consume();
        });
        tabPaneMain.getTabs().get(1).getContent().addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {

            if (event.getCode() == KeyCode.SPACE) {
                WordsDay wordsDay = loaderTabWordsDay.getController();
                if ( wordsDay.getTranslation().getText().length() > 34) {
                    wordsDay.getTranslation().setFont(Font.font("System", FontWeight.BOLD, 15));
                }
                wordsDay.getTranslation().setVisible(false);
            }
            event.consume();
        });
        stage.setScene(mainScene);
        stage.show();
    }
    private static AnchorPane loadTabAllWords() {
        if (anchorPaneAllWords == null) {
            try {
                anchorPaneAllWords = (AnchorPane) loaderTabAllWords.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return anchorPaneAllWords;
    }
    private static AnchorPane loadTabSettings() {
        if (anchorPaneSettings == null) {
            try {
                anchorPaneSettings = (AnchorPane) loaderTabSettings.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return anchorPaneSettings;
    }
    private static AnchorPane loadTabWordsDay() {
        if (anchorPaneWordsDay == null) {
            try {
                anchorPaneWordsDay = (AnchorPane) loaderTabWordsDay.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return anchorPaneWordsDay;
    }
    public static AllWords getControllerTabAllWords() {
        loadTabAllWords();
        return loaderTabAllWords.getController();
    }
    public static WordsDay getControllerTabWordsDay() {
        loadTabWordsDay();
        return loaderTabWordsDay.getController();
    }
    public static Settings getControllerTabSettings() {
        loadTabSettings();
        return loaderTabSettings.getController();
    }
    public static Stage getMainStage() {
        return mainStage;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
