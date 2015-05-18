/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;

/**
 *
 * @author John
 */
public class VolumePane extends HBox {

    private MediaPlayer mp;
    private ProgressBar volume;

    public VolumePane(MediaPlayer mpRef) {
        mp = mpRef;
        volume = new ProgressBar(0.2);

        volume.setOnMouseClicked((MouseEvent event) -> {
            double mouseX = event.getX();
            volume.setProgress(mouseX / 100);

            mp.setMute(false);
            mp.setVolume(mouseX / 100);
        });

        volume.setOnMouseDragged((MouseEvent event) -> {
            double mouseX = event.getX();
            volume.setProgress(mouseX / 100);

            mp.setMute(false);
            mp.setVolume(mouseX / 100);
        });

        this.getChildren().add(volume);
    }
}
