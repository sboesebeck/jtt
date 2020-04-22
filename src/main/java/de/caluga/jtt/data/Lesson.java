package de.caluga.jtt.data;

import de.caluga.morphium.annotations.CreationTime;
import de.caluga.morphium.annotations.Entity;
import de.caluga.morphium.annotations.Id;
import de.caluga.morphium.driver.MorphiumId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class Lesson {
    @Id
    private MorphiumId id;
    private long timestamp;
    private long duration;

    private String lessonName;
    private List<String> linesOfLesson;
    private int typedTotal;
    private int typedWords;
    private double wpm;
    private double cpm;
    private int errorsTotal;
    private int backspaces;
    private Map<String,Integer> errorsByFinger;
    private Map<String,Integer> errorsByCharacter;

    private List<LineTyped> typedLines=new ArrayList<>();

    public Lesson(){
        timestamp=System.currentTimeMillis();
    }
    public MorphiumId getId() {
        return id;
    }

    public void setId(MorphiumId id) {
        this.id = id;
    }

    public LineTyped getLastLineTyped(){
        return getTypedLines().get(getTypedLines().size()-1);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public List<String> getLinesOfLesson() {
        return linesOfLesson;
    }

    public void setLinesOfLesson(List<String> linesOfLesson) {
        this.linesOfLesson = linesOfLesson;
    }

    public int getTypedTotal() {
        return typedTotal;
    }

    public void setTypedTotal(int typedTotal) {
        this.typedTotal = typedTotal;
    }

    public int getErrorsTotal() {
        return errorsTotal;
    }

    public void setErrorsTotal(int errorsTotal) {
        this.errorsTotal = errorsTotal;
    }

    public int getBackspaces() {
        return backspaces;
    }

    public void setBackspaces(int backspaces) {
        this.backspaces = backspaces;
    }

    public Map<String, Integer> getErrorsByFinger() {
        return errorsByFinger;
    }

    public void setErrorsByFinger(Map<String, Integer> errorsByFinger) {
        this.errorsByFinger = errorsByFinger;
    }

    public Map<String, Integer> getErrorsByCharacter() {
        return errorsByCharacter;
    }

    public void setErrorsByCharacter(Map<String, Integer> errorsByCharacter) {
        this.errorsByCharacter = errorsByCharacter;
    }

    public List<LineTyped> getTypedLines() {
        return typedLines;
    }

    public void setTypedLines(List<LineTyped> typedLines) {
        this.typedLines = typedLines;
    }

    public int getTypedWords() {
        return typedWords;
    }

    public void setTypedWords(int typedWords) {
        this.typedWords = typedWords;
    }

    public double getWpm() {
        return wpm;
    }

    public void setWpm(double wpm) {
        this.wpm = wpm;
    }

    public double getCpm() {
        return cpm;
    }

    public void setCpm(double cpm) {
        this.cpm = cpm;
    }
}
