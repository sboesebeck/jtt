package de.caluga.jtt.data;

import de.caluga.morphium.annotations.Embedded;

@Embedded
public class TypedKey {
    private String character;
    private long timestamp;
    private long atDuration;
    private boolean correct;
    private String finger;

    public TypedKey() {
        timestamp=System.currentTimeMillis();
    }

    public long getAtDuration() {
        return atDuration;
    }

    public void setAtDuration(long atDuration) {
        this.atDuration = atDuration;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }
}
