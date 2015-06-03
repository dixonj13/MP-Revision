/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import utilities.MarkerDataModel;

/**
 *
 * @author John
 */
public class ControlPane extends HBox {

    private MediaPlayer mp;
    private MarkerDataModel mdm;
    private Main application;

    private final Image PLAY = new Image("image/play.png");
    private final Image PAUSE = new Image("image/pause.png");
    private final Image STOP = new Image("image/stop.png");
    private final Image NEXT = new Image("image/forward.png");
    private final Image PREV = new Image("image/rewind.png");

    private final ImageView playIcon;
    private final ImageView stopIcon;
    private final ImageView nextIcon;
    private final ImageView prevIcon;

    private final Button play;
    private final Button stop;
    private final Button prev;
    private final Button next;

    private Boolean mediaRegistered;

    public ControlPane(Main application) {
        this.application = application;
        
        playIcon = new ImageView(PLAY);
        playIcon.setFitHeight(20);
        playIcon.setFitWidth(20);
        stopIcon = new ImageView(STOP);
        stopIcon.setFitHeight(20);
        stopIcon.setFitWidth(20);
        nextIcon = new ImageView(NEXT);
        nextIcon.setFitHeight(20);
        nextIcon.setFitWidth(20);
        prevIcon = new ImageView(PREV);
        prevIcon.setFitHeight(20);
        prevIcon.setFitWidth(20);

        play = new Button("", playIcon);
        stop = new Button("", stopIcon);
        prev = new Button("", prevIcon);
        next = new Button("", nextIcon);

        play.setOnAction((e) -> {
            if (mediaRegistered) {
                Status status = mp.getStatus();

                if (status == Status.DISPOSED || status == Status.HALTED) {
                    return;
                }

                if (status == Status.PLAYING) {
                    mp.pause();
                } else {
                    mp.play();
                }
            } else {
                if (!application.isMediaSelected()) {
                    application.selectMedia();
                }
                application.openMedia();
            }
        });

        HBox.setMargin(play, new Insets(0, 25, 0, 0));

        this.getChildren().addAll(play, prev, stop, next);
        this.setAlignment(Pos.CENTER);

        mediaRegistered = false;
        stop.setDisable(true);
        prev.setDisable(true);
        next.setDisable(true);
    }

    public void registerMedia(MediaPlayer mpRef, MarkerDataModel mdmRef) {
        mp = mpRef;
        mdm = mdmRef;

        mp.setOnPaused(() -> {
            playIcon.setImage(PLAY);
        });

        mp.setOnPlaying(() -> {
            playIcon.setImage(PAUSE);
        });

        stop.setOnAction((e) -> {
            playIcon.setImage(PLAY);
            mp.stop();
        });

        prev.setOnAction((e) -> {
            Duration offset = mp.getCurrentTime().subtract(mdm.getCurrMarker());
            if (offset.greaterThanOrEqualTo(new Duration(300))) {
                application.safeSeek(mdm.getCurrMarker());
            } else {
                application.safeSeek(mdm.getPrevMarker());
            }
        });

        next.setOnAction((e) -> {
            application.safeSeek(mdm.getNextMarker());
        });

        mediaRegistered = true;
        stop.setDisable(false);
        prev.setDisable(false);
        next.setDisable(false);
    }

    public Boolean isMediaRegistered() {
        return mediaRegistered;
    }

    public void deregisterMedia() {
        if (isMediaRegistered()) {
            mp = null;
            mediaRegistered = false;
            stop.setDisable(true);
            prev.setDisable(true);
            next.setDisable(true);
        }
    }
}
