package model;


import java.io.Serializable;
import java.time.LocalDate;


public class WordSerializable implements Serializable {

    private final String word;
    private final String translate;
    private final LocalDate dateCreation;

    public WordSerializable (String word, String translate, LocalDate dateCreation) {
        this.word = word;
        this.translate = translate;
        this.dateCreation = dateCreation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslate() {
        return translate;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    @Override
    public String toString() {
        return "[" +
                "word='" + word + '\'' +
                ", translate='" + translate + '\'' +
                ", dateCreation=" + dateCreation +
                ']'+ "\n";
    }
}
