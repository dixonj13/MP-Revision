/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.*;
import javafx.util.Duration;
import utilities.Conversions;

/**
 *
 * @author John
 */
public class ProgressPane extends BorderPane {

    private static final int SLIDE_FACTOR = 10000;

    private final Main application;
    private MediaPlayer mp;
    private final YieldingSlider slider;
    private Duration duration = null;
    private Label current = null;
    private Label end = null;
    private Boolean initialized = false;
    private Boolean mediaRegistered;

    public ProgressPane(Main application) {
        this.application = application;
        
        slider = new YieldingSlider(0, SLIDE_FACTOR, 0);
        current = new Label("00:00");
        end = new Label("00:00");

        this.setLeft(current);
        this.setRight(end);
        this.setCenter(slider);
        BorderPane.setMargin(slider, new Insets(10, 0, 10, 0));
        BorderPane.setMargin(current, new Insets(10, 10, 10, 10));
        BorderPane.setMargin(end, new Insets(10, 10, 10, 10));

        mediaRegistered = false;
        this.setDisable(true);
    }

    public void registerMedia(MediaPlayer mpRef) {
        mp = mpRef;

        mp.currentTimeProperty().addListener((o) -> {
            if (!initialized) {
                duration = mp.getMedia().getDuration();
                end.setText(Conversions.durationToDynamicHrMnSc(duration));
                initialized = true;
            }

            int value = (int) (mp.getCurrentTime().divide(
                    duration.toMillis()).toMillis() * SLIDE_FACTOR);
            slider.setValue(value);

            current.setText(
                    Conversions.durationToDynamicHrMnSc(mp.getCurrentTime()));
        });

        slider.valueProperty().addListener((o) -> {
            if (slider.isValueChanging() || slider.mouseDown()) {
                Duration seekTo = new Duration(1.0 * slider.getValue()
                        / SLIDE_FACTOR * duration.toMillis());
                application.seekAndUpdate(seekTo);
            }
        });

        mediaRegistered = true;
        this.setDisable(false);
    }

    public Boolean isMediaRegistered() {
        return mediaRegistered;
    }

    public void deregisterMedia() {
        if (isMediaRegistered()) {
            slider.setValue(0);
            current.setText("00:00");
            end.setText("00:00");

            mp = null;
            mediaRegistered = false;
            this.setDisable(true);
        }
    }

    class YieldingSlider extends Slider {

        private Boolean mousePressed = false;

        public YieldingSlider(int start, int end, int position) {
            super(start, end, position);

            addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
                mousePressed = true;
            });

            addEventFilter(MouseEvent.MOUSE_RELEASED, (e) -> {
                mousePressed = false;
            });
        }

        public boolean mouseDown() {
            return mousePressed;
        }
    }
}
