package utillity;

import controllers.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Word;
import model.WordSerializable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Utillity {

    public static ObservableList<Word> serializableListInObservableList(List<WordSerializable> list) {
        ObservableList<Word> result = FXCollections.observableArrayList();
        for (WordSerializable wordS : list) {
            result.add(new Word(wordS.getWord(), wordS.getTranslate(), wordS.getDateCreation()));
        }
        return result;
    }
    public static List<WordSerializable> observableListInSerializableList(ObservableList<Word> list) {
        List<WordSerializable> result = new ArrayList<>();
        for (Word wordS : list) {
            result.add(new WordSerializable(wordS.getWord(), wordS.getTranslate(), wordS.getDateCreation()));
        }
        return result;
    }
    public static ObservableList<Word> jsonObjInObservableList(JSONObject jsonObject) {
        ObservableList<Word> result = FXCollections.observableArrayList();
        JSONArray jsonArray = (JSONArray) jsonObject.get("words");

        for (Object jsonObjArrayElement : jsonArray) {
            JSONObject wordJson = (JSONObject) jsonObjArrayElement;
                String word = (String) wordJson.get("word");
                String translate = (String) wordJson.get("translate");
                String dateCreation = (String) wordJson.get("dateCreation");
                LocalDate localDateCreation = DateUtil.parse(dateCreation);

            result.add(new Word(word, translate, localDateCreation));
        }
        return result;
    }
    public static JSONObject observableListInJsonList(ObservableList<Word> observableList) {
        JSONObject result = new JSONObject();
        JSONArray words = new JSONArray();

        for (Word word : observableList) {
            JSONObject temper = new JSONObject();
            temper.put("word",word.getWord());
            temper.put("translate",word.getTranslate());
            temper.put("dateCreation",DateUtil.format(word.getDateCreation()));
            words.add(temper);
        }
        result.put("words", words);
        return result;
    }
    public static void printAllPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(Settings.class);
        System.out.println("Preferences---------------");
        System.out.println("key WordsForDay ="+prefs.get("WordsForDay",null));
        System.out.println("key NewWordsForDays ="+prefs.get("NewWordsForDays",null));
        System.out.println("key SaveListDayWordsExit ="+prefs.getBoolean("SaveListDayWordsExit",false));
        System.out.println(" -countLastWords ="+prefs.getInt("CountLastWords", 0));
        System.out.println("key XMLToggle ="+prefs.getBoolean("XMLToggle",false));
        System.out.println("key JSONToggle ="+prefs.getBoolean("JSONToggle",false));
        System.out.println("key Serializable ="+prefs.getBoolean("Serializable", false));
        System.out.println("key filePathXML ="+prefs.get("filePathXML", null));
        System.out.println("key filePathJSON ="+prefs.get("filePathJSON", null));
        System.out.println("fileSerializable exists ="+ new File("dictonary.base").exists());
        System.out.println("---------------------------");
    }

}
