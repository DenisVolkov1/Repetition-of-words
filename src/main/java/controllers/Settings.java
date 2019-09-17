package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import model.Word;
import model.WordListWrapper;
import model.WordSerializable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import run.AppRun;
import utillity.Utillity;

import javax.xml.bind.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

public class Settings {
    private final String css = this.getClass().getResource("/view/theme.css").toExternalForm();
    private static SpinnerValueFactory.IntegerSpinnerValueFactory spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 10);
    private static WordsDay wordsDay;
    private static AllWords allWords;
    private Preferences prefs = Preferences.userNodeForPackage(Settings.class);
    static {
        wordsDay = AppRun.getControllerTabWordsDay();
        allWords = AppRun.getControllerTabAllWords();
    }
    @FXML
    private CheckBox saveListDayWordsExit;
    @FXML
    private Label prompt;
    @FXML
    private Button buttonLoadXMLFile;
    @FXML
    private Button buttonLoadJSONFile;
    @FXML
    private Line lineXML;
    @FXML
    private Line lineJSON;
    @FXML
    private Label nameFileXML;
    @FXML
    private Label nameFileJSON;
    @FXML
    private Spinner<Integer> countNewWords;
    @FXML
    private TextField fieldCountDayWords;
    @FXML
    private RadioButton radioBtnXML;
    @FXML
    private RadioButton radioBtnJSON;
    @FXML
    private RadioButton radioBtnSerializ;
    @FXML
    private void initialize() {
        setSystemPropertyEncoding();
        try {
           preInitialize();
       } catch (Exception e) {
           e.printStackTrace();
       }
       Utillity.printAllPreferences();
    }
    private void setSystemPropertyEncoding() {
        System.setProperty("file.encoding","UTF-8");
        Field charset = null;
        try {
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null,null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private void preInitialize() {
        // set name label
        File xmlFile = getFilePathXML();
        File jsonFile= getFilePathJSON();

        if (getFilePathXML() == null) {
            nameFileXML.setText("empty...");
        } else nameFileXML.setText(xmlFile.getName());
        if (getFilePathJSON() == null) {
            nameFileJSON.setText("empty...");
        } else nameFileJSON.setText(jsonFile.getName());
            wordsDay.isButtonsEnabled(false);
            setJSONInterfaceActive(false);
        //set save new words day
        String savedNewWords = prefs.get("NewWordsForDays", null);
        if (savedNewWords == null) {
            spinnerValueFactory.setValue(2);
            countNewWords.setValueFactory(spinnerValueFactory);
            saveNewWordsForDays();
        } else {
            countNewWords.setValueFactory(spinnerValueFactory);
            spinnerValueFactory.setValue(Integer.valueOf(savedNewWords));
        }
        //set save day words
        String savedDayWords = prefs.get("WordsForDay", null);
        if (savedDayWords == null) {
            saveWordsForDay();
        } else {
            fieldCountDayWords.setText(savedDayWords);
        }
        //set save bases
        boolean xmlToggle = xmlToggleSelected();
        boolean serializableToggle = serializableToggleSelected();
        boolean jsonToggle = jsonToggleSelected();

        if (!xmlToggle && !serializableToggle && !jsonToggle) {
            saveToggle();
        } else {
            if (xmlToggle) {
                setXMLInterfaceActive(true);
                radioBtnXML.setSelected(true);
                loadBaseXMLIfExistsAndSet();
            }
            if (jsonToggle) {
                setJSONInterfaceActive(true);
                radioBtnJSON.setSelected(true);
                loadBaseJSONIfExistsAndSet();
            }
            if (serializableToggle) {
                setXMLInterfaceActive(false);
                radioBtnSerializ.setSelected(true);
                if (loadFileSerializable()) {
                    // устанавливаем имена
                    setNameBase("Ser");
                    //устанавливаем активность кнопок
                    wordsDay.isButtonsEnabled(true);
                    wordsDay.getWordsDayList().clear();
                    wordsDay.refresh(this);
                    //устанавливаем счётчик слов
                    allWords.setCurrentCountWords();
                }
            }
        }
        //set day words is save
        if (saveListDayWordsExitSelected()) {
            saveListDayWordsExit.setSelected(true);
            loadListDayWords();

        }
        //set format words day
        fieldCountDayWords.setTextFormatter(getTextFormatter());
        fieldCountDayWords.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.length() >= 4) {
                String s = fieldCountDayWords.getText().substring(0, 3);
                fieldCountDayWords.setText(s);
                alertCountWords();
            }
        });
        //exit action
        AppRun.getMainStage().setOnCloseRequest(e -> {
            if (saveListDayWordsExitSelected()) {
                saveListDayWords();
            } else deleteLastWordsFileAndSettings();
        });
    }
    @FXML
    private void clickSpinnerNewWordsHandle() {
        saveNewWordsForDays();
    }
    private void saveNewWordsForDays() {
        String saveCountNewWords = countNewWords().toString();
        prefs.put("NewWordsForDays", saveCountNewWords);
    }
    @FXML
    private void pressedKeyInFieldWordsDayHandle() {
        prompt.setVisible(true);
    }
    public boolean saveWordsForDay() {
        String countDayWordsStr = fieldCountDayWords.getText();
        boolean notZeroLength = countDayWordsStr.length() > 0;
        if (notZeroLength) {
            boolean validCount = Integer.valueOf(countDayWordsStr) >= 1;
            if (validCount) {
                prompt.setVisible(false);
                prefs.put("WordsForDay", countDayWordsStr);
            }
            return true;
        } else {
            alertCountWords();
            return false;
        }
    }
    @FXML
    private void listOfDayHandle() {
        saveToggleListDayWordsExit();
    }
    @FXML
    private void xmlHandleToggle() {
        setXMLInterfaceActive(true);
        radioBtnXML.setSelected(true);
        saveToggle();
        //
        deleteLastWordsFileAndSettings();
        //
        if (getFilePathXML() != null) {
            loadBaseXMLIfExistsAndSet();
        } else {
            clearAndSetDefault();
            alertNotBase("Load base or create her.");
        }
    }
    @FXML
    private void jsonHandleToggle() {
        setJSONInterfaceActive(true);
        radioBtnJSON.setSelected(true);
        saveToggle();
        //
        deleteLastWordsFileAndSettings();
        //
        if (getFilePathJSON() != null) {
            loadBaseJSONIfExistsAndSet();
        } else {
            clearAndSetDefault();
            alertNotBase("Load base or create her.");
        }
    }
    @FXML
    private void serializHandleToggle() {
        setXMLInterfaceActive(false);
        setJSONInterfaceActive(false);
        radioBtnSerializ.setSelected(true);
        saveToggle();
        //
        deleteLastWordsFileAndSettings();
        //
        boolean isLoadFileSerializable = loadFileSerializable();
        if (isLoadFileSerializable) {
            setNameBase("Ser");
            wordsDay.isButtonsEnabled(true);
            wordsDay.getWordsDayList().clear();
            wordsDay.refresh(this);
            allWords.setCurrentCountWords();
        } else {
            clearAndSetDefault();
            alertNotBase("Create an internal base.");
        }
    }
    @FXML
    private void clearAllSave() {
        prefs.remove("filePathXML");
        prefs.remove("filePathJSON");
        clearAndSetDefault();
        deleteLastWordsFileAndSettings();
            spinnerValueFactory.setValue(2);
            countNewWords.setValueFactory(spinnerValueFactory);
            saveNewWordsForDays();
                radioBtnXML.setSelected(true);
                saveToggle();
                    saveListDayWordsExit.setSelected(false);
                    saveToggleListDayWordsExit();
                        fieldCountDayWords.setText("20");
                        saveWordsForDay();
        setXMLInterfaceActive(true);
            radioBtnXML.setSelected(true);
            saveToggle();
            prompt.setVisible(false);
            setNameBase("DEFAULT");


            Utillity.printAllPreferences();
    }
    public boolean xmlToggleSelected() {
        return prefs.getBoolean("XMLToggle", false);
    }
    public boolean jsonToggleSelected() {
        return prefs.getBoolean("JSONToggle", false);
    }
    public boolean serializableToggleSelected() {
        return prefs.getBoolean("Serializable", false);
    }
    public boolean saveListDayWordsExitSelected() {
        return prefs.getBoolean("SaveListDayWordsExit", false);
    }
    private void saveToggleListDayWordsExit() {
        prefs.putBoolean("SaveListDayWordsExit", saveListDayWordsExit.isSelected());
    }
    private void saveToggle() {
        prefs.putBoolean("XMLToggle", radioBtnXML.isSelected());
        prefs.putBoolean("Serializable", radioBtnSerializ.isSelected());
        prefs.putBoolean("JSONToggle", radioBtnJSON.isSelected());
    }
    public void loadBaseXMLIfExistsAndSet() {
        File xmlFile = getFilePathXML();
        if (xmlFile != null) {
            if (xmlFile.exists()) {
                loadDataFromFileXML(xmlFile);
                // устанавливаем имена
                String name = xmlFile.getName();
                nameFileXML.setText(name);
                setNameBase("XML");
                //устанавливаем активность кнопок и настройка
                wordsDay.isButtonsEnabled(true);
                wordsDay.getWordsDayList().clear();
                wordsDay.refresh(this);
                //устанавливаем счётчик слов
                allWords.setCurrentCountWords();
            } else alertErrorLoadSaveDate(xmlFile);
        }
    }
    public void clearAndSetDefault() {
        AllWords.getWords().clear();
        wordsDay.getWordsDayList().clear();
        wordsDay.getNameBase().setText(". . .");
        wordsDay.isButtonsEnabled(false);
        allWords.getNameBase().setText(". . .");
        allWords.setCurrentCountWords();
            if (getFilePathXML() == null) nameFileXML.setText("empty...");
            if (getFilePathJSON() == null) nameFileJSON.setText("empty...");
    }
    private void setXMLInterfaceActive(boolean b) {
        if (b) {
            buttonLoadXMLFile.setDisable(false);
            lineXML.setOpacity(0.4);
            nameFileXML.setOpacity(1);
            setJSONInterfaceActive(false);
        } else {
            buttonLoadXMLFile.setDisable(true);
            lineXML.setOpacity(0.16);
            nameFileXML.setOpacity(0.5);
        }
    }
    private void setJSONInterfaceActive(boolean b) {
        if (b) {
            buttonLoadJSONFile.setDisable(false);
            lineJSON.setOpacity(0.5);
            nameFileJSON.setOpacity(1);
            setXMLInterfaceActive(false);
        } else {
            buttonLoadJSONFile.setDisable(true);
            lineJSON.setOpacity(0.16);
            nameFileJSON.setOpacity(0.5);
        }
    }
    private void loadBaseJSONIfExistsAndSet() {
        File jsonFile = getFilePathJSON();
        if (jsonFile != null) {
            if (jsonFile.exists()) {
                loadDataFromFileJSON(jsonFile);
                // устанавливаем имена
                String name = jsonFile.getName();
                nameFileJSON.setText(name);
                setNameBase("JSON");
                //устанавливаем активность кнопок и настройка
                wordsDay.isButtonsEnabled(true);
                wordsDay.getWordsDayList().clear();
                wordsDay.refresh(this);
                //устанавливаем счётчик слов
                allWords.setCurrentCountWords();
            } else alertErrorLoadSaveDate(jsonFile);
        }
    }
    @FXML
    public void loadFileXMLFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(AppRun.getMainStage());
        if (file != null) {
            loadDataFromFileXML(file);
            //
            String name = file.getName();
            nameFileXML.setText(name);
            setNameBase("XML");
            //устанавливаем активность кнопок
            wordsDay.isButtonsEnabled(true);
            wordsDay.refresh(this);
            //устанавливаем счётчик слов
            allWords.setCurrentCountWords();
        }
    }
    public void loadDataFromFileXML(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(WordListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            // Чтение XML из файла и демаршализация.
            WordListWrapper wrapper = (WordListWrapper) um.unmarshal(file);
            //print
            if (wrapper.getWords() != null) {
                System.out.println(wrapper.getWords());
            }
            //устанавливаем загруженные
            AllWords.getWords().clear();
            AllWords.getWords().addAll(wrapper.getWords());
            // Сохраняем путь к файлу в реестре.
            saveFilePathXML(file);

        } catch (Exception e) { // catches ANY exception
            alertErrorLoadSaveDate(file);
            e.printStackTrace();
        }
    }
    public File saveFileXMLFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        // Показываем диалог сохранения файла
        File file = fileChooser.showSaveDialog(AppRun.getMainStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            saveFilePathXML(file);
            saveDataToFileXML(file);
            return file;
        }
        return null;
    }
    public void saveDataToFileXML(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(WordListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Обёртываем наши данные об адресатах.
            WordListWrapper wrapper = new WordListWrapper();
            wrapper.setWords(AllWords.getWords());
            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);

        } catch (JAXBException e) {
            e.printStackTrace();
            alertErrorLoadSaveDate(file);
        }
    }
    public void loadFileJSONFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(AppRun.getMainStage());
        if (file != null) {
            loadDataFromFileJSON(file);
            String name = file.getName();
            nameFileJSON.setText(name);
            setNameBase("JSON");
            //устанавливаем активность кнопок
            wordsDay.isButtonsEnabled(true);
            wordsDay.refresh(this);
            //устанавливаем счётчик слов
            allWords.setCurrentCountWords();
        }
    }
    public void loadDataFromFileJSON(File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonParser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
            alertErrorLoadSaveDate(file);
        } catch (ParseException e) {
            alertErrorParseJSON(file);
            e.printStackTrace();
        }

        ObservableList<Word> result = Utillity.jsonObjInObservableList(jsonObject);
        System.out.println(result);

        AllWords.getWords().clear();
        AllWords.getWords().addAll(result);
        saveFilePathJSON(file);
    }
    public File saveFileJSONFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        // Показываем диалог сохранения файла
        File file = fileChooser.showSaveDialog(AppRun.getMainStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".json")) {
                file = new File(file.getPath() + ".json");
            }
            saveFilePathJSON(file);
            saveDataToFileJSON(file);
            return file;
        }
        return null;
    }
    public void saveDataToFileJSON(File file) {
        JSONObject mainjson = Utillity.observableListInJsonList(AllWords.getWords());
        try {
            byte[] bytes = mainjson.toJSONString().getBytes();
            Path path = Paths.get(file.getPath());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
            alertErrorLoadSaveDate(file);
        }
    }
    public boolean loadFileSerializable() {
        File fileBase = new File("dictonary.base");
        if (fileBase.exists()) {
            ObjectInputStream objectInputStream = null;
            ArrayList<WordSerializable> wordSerializables = null;
            try {
                objectInputStream = new ObjectInputStream(
                        new FileInputStream("dictonary.base"));
                wordSerializables = (ArrayList<WordSerializable>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                alertErrorLoadSaveDate(fileBase);
                e.printStackTrace();
            } finally {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    alertErrorLoadSaveDateForClose(fileBase);
                    e.printStackTrace();
                }
            }
            System.out.println(wordSerializables);
            AllWords.getWords().clear();
            ObservableList<Word> words = Utillity.serializableListInObservableList(wordSerializables);
            AllWords.getWords().addAll(words);
            return true;
        }
        return false;
    }
    public void saveFileSerializable() {
        List<WordSerializable> words = Utillity.observableListInSerializableList(AllWords.getWords());
        //Сериализация в файл с помощью класса ObjectOutputStream
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("dictonary.base"));
            objectOutputStream.writeObject(words);
        } catch (IOException e) {
            alertErrorLoadSaveDate(new File("dictonary.base"));
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                alertErrorLoadSaveDateForClose(new File("dictonary.base"));
                e.printStackTrace();
            }
        }
    }
    public void saveListDayWords() {
        List<Word> saveList = wordsDay.getWordsDayList();
        if (saveList.size() == 0) return;
        ObservableList<Word> words = FXCollections.observableArrayList(saveList);

        List<WordSerializable> wordsDay = Utillity.observableListInSerializableList(words);
        //Сериализация в файл с помощью класса ObjectOutputStream
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream("listwords.list"));
            objectOutputStream.writeObject(wordsDay);
        } catch (IOException e) {
            alertErrorLoadSaveDate(new File("listwords.list"));
            e.printStackTrace();
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                alertErrorLoadSaveDateForClose(new File("listwords.list"));
                e.printStackTrace();
            }
        }
    }
    public boolean loadListDayWords() {
        File listWords = new File("listwords.list");
        if (listWords.exists()) {
            ObjectInputStream objectInputStream = null;
            ArrayList<WordSerializable> wordSerializables = null;
            try {
                objectInputStream = new ObjectInputStream(
                        new FileInputStream("listwords.list"));
                wordSerializables = (ArrayList<WordSerializable>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                alertErrorLoadSaveDate(listWords);
                e.printStackTrace();
            } finally {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    alertErrorLoadSaveDateForClose(listWords);
                    e.printStackTrace();
                }
            }
            System.out.println(wordSerializables);

            ObservableList<Word> words = Utillity.serializableListInObservableList(wordSerializables);
            wordsDay.setWordsDayList(new ArrayList<>(words));
            //
            Integer setCountLastWord = prefs.getInt("CountLastWords", 0);
            wordsDay.setCountLastWords(setCountLastWord);
            wordsDay.setProgressInProgressBar();

            wordsDay.viewWordForIndex(setCountLastWord);
            wordsDay.getLastWords().setText(setCountLastWord.toString());
            return true;
        }
        return false;
    }
    public void saveFilePathXML(File file) {
        if (file != null) {
            prefs.put("filePathXML", file.getPath());
        } else {
            prefs.remove("filePathXML");
        }
    }
    public void saveFilePathJSON(File file) {
        if (file != null) {
            prefs.put("filePathJSON", file.getPath());
        } else {
            prefs.remove("filePathJSON");
        }
    }
    public File getFilePathJSON() {
        String filePath = prefs.get("filePathJSON", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }
    public File getFilePathXML() {
        String filePath = prefs.get("filePathXML", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }
    private void deleteLastWordsFileAndSettings() {
        prefs.remove("CountLastWords");
            File file = new File("listwords.list");
            if (file.exists()) {
                file.delete();
            }
    }
    public Integer countDayWords() {
        String wordsForDay = prefs.get("WordsForDay", null);
        return Integer.valueOf(wordsForDay);
    }
    public Integer countNewWords() {
        return countNewWords.getValue();
    }
    private TextFormatter<String> getTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = getFilter();
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        return textFormatter;
    }
    private UnaryOperator<TextFormatter.Change> getFilter() {
        return change -> {
            String text = change.getText();
            if (!change.isContentChange()) {
                return change;
            }
            if (text.matches("[0-9]{1,3}") || text.isEmpty()) {
                return change;
            }
            return null;
        };
    }
    public void setNameBase(String str) {
        File fileJSON = getFilePathJSON();
        File fileXML = getFilePathXML();

            switch (str) {
                case "XML" :
                    if (fileXML != null) {
                        String nameFile = fileXML.getName();
                        nameFileXML.setText(nameFile);
                        allWords.getNameBase().setText(nameFile);
                        wordsDay.getNameBase().setText(nameFile);
                    } else {
                        nameFileXML.setText("empty...");
                        allWords.getNameBase().setText(". . .");
                        wordsDay.getNameBase().setText(". . .");
                    }
                    break;
                case "JSON" :
                    if (fileJSON != null) {
                        String nameFile = fileJSON.getName();
                        nameFileJSON.setText(nameFile);
                        allWords.getNameBase().setText(nameFile);
                        wordsDay.getNameBase().setText(nameFile);
                    } else {
                        nameFileJSON.setText("empty...");
                        allWords.getNameBase().setText(". . .");
                        wordsDay.getNameBase().setText(". . .");
                    }
                    break;
                case "Ser" :
                    File fileSerializable = new File("dictonary.base");
                    if (fileSerializable.exists()) {
                        allWords.getNameBase().setText("InnerBase");
                        wordsDay.getNameBase().setText("InnerBase");
                    } else {
                        allWords.getNameBase().setText(". . .");
                        wordsDay.getNameBase().setText(". . .");
                    }
                    break;
                case "DEFAULT" :
                    nameFileXML.setText("empty...");
                    nameFileJSON.setText("empty...");
                    allWords.getNameBase().setText(". . .");
                    wordsDay.getNameBase().setText(". . .");
            }
    }
    public Preferences getPrefs() {
        return prefs;
    }
    public Label getNameFileXML() {
        return nameFileXML;
    }
    public Label getNameFileJSON() {
        return nameFileJSON;
    }
    private void alertErrorLoadSaveDate(File file) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.setTitle("Error");
        alert.setHeaderText("Could not load/save data");
        alert.setContentText("Could not load/save data from file:" + file.getPath());
        alert.showAndWait();
    }
    private void alertErrorLoadSaveDateForClose(File file) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.setTitle("Error");
        alert.setHeaderText("There was a write / read error while closing the file");
        alert.setContentText("Could not close file :" + file.getPath());
        alert.showAndWait();
    }
    private void alertCountWords() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Invalid count day");
        alert.setHeaderText("Error input!!!");
        alert.setContentText("Please select count words between (1 - 999)");
        alert.showAndWait();
    }
    private void alertErrorParseJSON(File file) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Invalid file");
        alert.setHeaderText("Error parsing!!!");
        alert.setContentText("Error parsing file:" + file.getPath());
        alert.showAndWait();
    }
    private void alertNotBase(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add(css);
        alert.initOwner(AppRun.getMainStage());
        alert.setTitle("Empty base");
        alert.setHeaderText("Your base is empty.");
        alert.setContentText(content);
        alert.showAndWait();
    }
}

