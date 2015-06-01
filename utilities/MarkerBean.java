/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author John
 */
public class MarkerBean {

    private ObjectProperty<Duration> time;
    private StringProperty title;
    private BooleanProperty playing;
    private BooleanProperty vamped;

    public MarkerBean() {
        setTime(new Duration(0));
        setTitle("Start");
        setPlaying(false);
        setVamped(false);
    }

    public MarkerBean(String title, Duration time) {
        setTime(time);
        setTitle(title);
        setPlaying(false);
        setVamped(false);
    }

    public MarkerBean(String title, double time) {
        setTime(new Duration(time));
        setTitle(title);
        setPlaying(false);
        setVamped(false);
    }

    public final Duration getTime() {
        return timeProperty().get();
    }

    public final String getTitle() {
        return titleProperty().get();
    }

    public final Boolean isPlaying() {
        return playingProperty().get();
    }
    
    public final Boolean isVamped() {
        return vampedProperty().get();
    }

    public final void setTime(final javafx.util.Duration time) {
        timeProperty().set(time);
    }

    public final void setTitle(final java.lang.String title) {
        titleProperty().set(title);
    }

    public final void setPlaying(final Boolean playing) {
        playingProperty().set(playing);
    }
    
    public final void setVamped(final Boolean vamped) {
        vampedProperty().set(vamped);
    }

    public ObjectProperty<Duration> timeProperty() {
        if (time == null) {
            time = new SimpleObjectProperty<>();
        }
        return time;
    }

    public StringProperty titleProperty() {
        if (title == null) {
            title = new SimpleStringProperty();
        }
        return title;
    }

    public BooleanProperty playingProperty() {
        if (playing == null) {
            playing = new SimpleBooleanProperty();
        }
        return playing;
    }

    public BooleanProperty vampedProperty() {
        if (vamped == null) {
            vamped = new SimpleBooleanProperty();
        }
        return vamped;
    }

    public String stringValue() {
        return String.format("%s %s", getTime().toSeconds(), getTitle());
    }

    public static Callback<MarkerBean, Observable[]> extractor() {
        return (MarkerBean m) -> new Observable[]{m.timeProperty(),
            m.titleProperty(), m.playingProperty(), m.vampedProperty()};
    }
}
