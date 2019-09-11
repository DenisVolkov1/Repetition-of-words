package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utillity.DateUtil;
import utillity.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Word {

    private final StringProperty word;
    private final StringProperty translate;
    private final ObjectProperty<LocalDate> dateCreation;

    public Word (String word, String translate, LocalDate dateCreation) {
        this.word = new SimpleStringProperty(word);
        this.translate = new SimpleStringProperty(translate);
        this.dateCreation = new SimpleObjectProperty<>(dateCreation);
    }
    public Word () {
        this(null,null,null);
    }

    public String getWord() {
        return word.get();
    }

    public StringProperty wordProperty() {
        return word;
    }

    public void setWord(String word) {
        this.word.set(word);
    }

    public String getTranslate() {
        return translate.get();
    }

    public StringProperty translateProperty() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate.set(translate);
    }
    @XmlJavaTypeAdapter (LocalDateAdapter.class)
    public LocalDate getDateCreation() {
        return dateCreation.get();
    }

    public ObjectProperty<LocalDate> dateCreationProperty() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation.set(dateCreation);
    }

    //property String date
    public StringProperty getDateCreationProperty() {
        SimpleStringProperty result = new SimpleStringProperty(DateUtil.format(getDateCreation()));
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return this.getWord().equals(word1.getWord());
    }
    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "[" +
                "word=" + getWord() +
                ", translate=" + getTranslate() +
                ", dateCreation=" + getDateCreation() +
                ']' + "\n";

    }
}
