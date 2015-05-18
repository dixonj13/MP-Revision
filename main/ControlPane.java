/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.Status;

/**
 *
 * @author John
 */
public class ControlPane extends HBox {

    private MediaPlayer mp;

    public ControlPane(MediaPlayer mpRef) {
        mp = mpRef;
        
        Button play = new Button("play");
        play.setOnAction((e) -> {
            Status status = mp.getStatus();
            
            if(status == Status.DISPOSED || status == Status.HALTED) {
                return;
            }
            
            if(status == Status.PLAYING) { 
                mp.pause();
            } else {
                mp.play();
            }
        });
        
        mp.setOnPaused(() -> {
            play.setText("play");
        });
        
        mp.setOnPlaying(() -> {
            play.setText("pause");
        });
        
        Button stop = new Button("stop");
        stop.setOnAction((e) -> {
            mp.seek(mp.getStartTime());
            mp.pause();
        });
        
        this.getChildren().addAll(play, stop);
        this.setAlignment(Pos.CENTER);
    }
}
