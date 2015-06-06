/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import utilities.MarkerBean;
import utilities.MarkerDataModel;
import utilities.MarkerListCell;

/**
 *
 * @author John
 */
public class MarkerPane extends ListView<MarkerBean> {

    private Main application;
    private MediaPlayer mp;
    private MarkerDataModel mdm;
    private Boolean mediaRegistered;

    public MarkerPane(Main application) {
        super();
        this.application = application;
        this.setDisable(true);
        mediaRegistered = false;
    }

    public void registerMedia(MediaPlayer mpRef, MarkerDataModel mdmRef) throws IOException {
        mp = mpRef;
        mdm = mdmRef;

        this.setItems(mdm.getItems());
        this.setCellFactory((ListView<MarkerBean> param) -> new MarkerListCell());
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.setOnMouseClicked((MouseEvent click) -> {
            if (click.getClickCount() == 2) {
                MarkerBean selected = getSelectionModel().getSelectedItem();
                application.safeSeek(selected.getTime());
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
            this.setItems(null);
            mp = null;
            mdm = null;
            mediaRegistered = false;
            this.setDisable(true);
        }
    }
}
