/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.scene.layout.Pane;
import javafx.scene.media.*;

/**
 *
 * @author dixonj13
 */
public class VideoPane extends Pane {

    private final MediaView mv;
    private final MediaPlayer mp;

    public VideoPane(MediaPlayer mpRef) {
        this.setId("VideoPane");
        this.setStyle("-fx-background-color: black;");
        this.mp = mpRef;
        mv = new MediaView(mp);
        mv.setPreserveRatio(true);
        this.getChildren().add(mv);
    }

    @Override
    protected double computeMinWidth(double height) {
        return 200;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 150;
    }

    @Override
    protected double computePrefWidth(double height) {
        return mp.getMedia().getWidth();
    }

    @Override
    protected double computePrefHeight(double width) {
        return mp.getMedia().getHeight();
    }

    @Override
    protected double computeMaxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return Double.MAX_VALUE;
    }

    @Override
    protected void layoutChildren() {

        if (mv != null) {
            mv.setFitWidth(getWidth());
            mv.setFitHeight(getHeight());
        }
        super.layoutChildren();
        if (mv != null) {
            mv.setTranslateX((getWidth() - mv.prefWidth(-1)) / 2);
            mv.setTranslateY((getHeight() - mv.prefHeight(-1)) / 2);
        }
    }
}
