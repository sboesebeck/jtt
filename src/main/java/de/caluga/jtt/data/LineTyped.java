package de.caluga.jtt.data;

import de.caluga.morphium.annotations.Embedded;
import de.caluga.morphium.annotations.Entity;
import de.caluga.morphium.annotations.Id;
import de.caluga.morphium.driver.MorphiumId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Embedded
public class LineTyped {

    private long timestamp;
    private long atDuration;
    private int typedInLine;
    private int errorsInLine;
    private int lineInLesson;

    private String lineToType;
    private String typedString;
    private Map<String,Integer> errorsByFingerInLine;
    private Map<String,Integer> errorsByCharInLine;

    private List<TypedKey> keyStrokes;

    public LineTyped(){
        timestamp=System.currentTimeMillis();
    }
    public long getAtDuration() {
        return atDuration;
    }

    public void setAtDuration(long atDuration) {
        this.atDuration = atDuration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTypedInLine() {
        return typedInLine;
    }

    public void setTypedInLine(int typedInLine) {
        this.typedInLine = typedInLine;
    }

    public int getErrorsInLine() {
        return errorsInLine;
    }

    public void setErrorsInLine(int errorsInLine) {
        this.errorsInLine = errorsInLine;
    }

    public int getLineInLesson() {
        return lineInLesson;
    }

    public void setLineInLesson(int lineInLesson) {
        this.lineInLesson = lineInLesson;
    }

    public String getLineToType() {
        return lineToType;
    }

    public void setLineToType(String lineToType) {
        this.lineToType = lineToType;
    }

    public String getTypedString() {
        return typedString;
    }

    public void setTypedString(String typedString) {
        this.typedString = typedString;
    }

    public Map<String, Integer> getErrorsByFingerInLine() {
        return errorsByFingerInLine;
    }

    public void setErrorsByFingerInLine(Map<String, Integer> errorsByFingerInLine) {
        this.errorsByFingerInLine = errorsByFingerInLine;
    }

    public Map<String, Integer> getErrorsByCharInLine() {
        return errorsByCharInLine;
    }

    public void setErrorsByCharInLine(Map<String, Integer> errorsByCharInLine) {
        this.errorsByCharInLine = errorsByCharInLine;
    }

    public List<TypedKey> getKeyStrokes() {
        if (keyStrokes==null){
            keyStrokes=new ArrayList<>();
        }
        return keyStrokes;
    }

    public void setKeyStrokes(List<TypedKey> keyStrokes) {
        this.keyStrokes = keyStrokes;
    }
}
