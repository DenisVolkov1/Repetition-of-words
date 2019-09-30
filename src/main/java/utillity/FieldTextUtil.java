package utillity;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import model.Word;

public class FieldTextUtil {
    /**
     * Проверяет заполнены ли поля.
     * @param textFields  поля.
     * @return возвращает TRUE не заполнены ,FALSE если заполнены.
     */
    public static boolean isEmptyFields(TextField ...textFields) {
        for (TextField field : textFields) {
            if (field.getText().length() == 0) return true;
        }
        return false;
    }

    /**
     * Находит повторяющиеся слова.
     * @param list - список слов (объектов)
     * @param textFields - поля
     * @return возвращает TRUE если есть совпадения ,FALSE если нет.
     */

    public static boolean isCloneExist(ObservableList<Word> list, TextField ...textFields) {
        for (TextField field : textFields) {
            for (Word word : list) {
                if (word.getWord().compareTo(field.getText()) == 0) return true;
            }

        }
        return false;
    }

}
