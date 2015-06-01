/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;

/**
 *
 * @author John
 */
public class VolumePane extends HBox {

    private final double INIT_VOLUME = 0.2;

    private MediaPlayer mp;
    private final ProgressBar volume;
    private final Button icon;
    private final ImageView iconView;

    private final Image MUTE = new Image("image/mute.png");
    private final Image LOW = new Image("image/low-vol.png");
    private final Image MED = new Image("image/volume.png");
    private final Image HIGH = new Image("image/high-vol.png");

    private Boolean mediaRegistered;

    public VolumePane() {
        volume = new ProgressBar(INIT_VOLUME);
        iconView = new ImageView(LOW);
        iconView.setFitHeight(20);
        iconView.setFitWidth(20);
        icon = new Button("", iconView);
        icon.setStyle("-fx-background-color: transparent;");

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(icon, volume);

        this.setDisable(true);
        mediaRegistered = false;
    }

    public void registerMedia(MediaPlayer mpRef) {
        mp = mpRef;
        mp.setVolume(INIT_VOLUME);
        volume.setProgress(INIT_VOLUME);

        volume.setOnMouseClicked((MouseEvent event) -> {
            setVolume(event);
        });

        volume.setOnMouseDragged((MouseEvent event) -> {
            setVolume(event);
        });

        volume.progressProperty().addListener((o) -> {
            setVolumeIcon(volume.getProgress() * 100);
        });

        icon.setOnAction((e) -> {
            if (mp.isMute()) {
                mp.setMute(false);
                setVolumeIcon(volume.getProgress() * 100);
            } else {
                mp.setMute(true);
                setVolumeIcon(0);
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
            mp = null;
            mediaRegistered = false;
            this.setDisable(true);
        }
    }

    private void setVolume(MouseEvent event) {
        double mouseX = event.getX();
        volume.setProgress(mouseX / 100);
        if (volume.isIndeterminate()) {
            volume.setProgress(0);
        }

        mp.setMute(false);
        mp.setVolume(mouseX / 100);
    }

    private void setVolumeIcon(double newVolume) {
        if (newVolume > 75) {
            iconView.setImage(HIGH);
        } else if (newVolume > 25) {
            iconView.setImage(MED);
        } else if (newVolume > 0) {
            iconView.setImage(LOW);
        } else {
            iconView.setImage(MUTE);
        }
    }
}
