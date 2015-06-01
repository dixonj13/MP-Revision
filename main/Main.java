/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import utilities.MarkerDataModel;

/**
 *
 * @author dixonj13
 */
public class Main extends Application {

    private File source;
    private MediaPlayer mp;
    private MarkerDataModel mdm;
    private FileChooser fc;
    private Boolean mediaRegistered;

    private VideoPane videoPane;
    private ProgressPane progressPane;
    private ControlPane controlPane;
    private VolumePane volumePane;
    private MarkerPane markerPane;

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        source = null;
        mediaRegistered = false;

        primaryStage.setTitle("Media Player R2");
        Scene scene = setScene();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public MenuBar createMenuBar() {
        fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP4, AAC", "*.mp4", "*.aac"));
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem open = new MenuItem("Open Media");
        open.setOnAction((e) -> {
            selectMedia();
            if (isMediaRegistered()) {
                deregisterMedia();
            }
            openMedia();
        });
        MenuItem close = new MenuItem("Close Media");
        close.setOnAction((e) -> {
            deregisterMedia();
        });
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem closeApp = new MenuItem("Close Application");
        closeApp.setOnAction((e) -> {
            Platform.exit();
        });
        file.getItems().addAll(open, close, separator, closeApp);

        Menu edit = new Menu("Edit");
        Menu help = new Menu("Help");

        menuBar.getMenus().addAll(file, edit, help);

        return menuBar;
    }

    public Scene setScene() throws IOException {
        videoPane = new VideoPane();
        controlPane = new ControlPane(this);
        progressPane = new ProgressPane(this);
        volumePane = new VolumePane();
        markerPane = new MarkerPane(this);

        BorderPane buttons = new BorderPane();
        buttons.setLeft(controlPane);
        buttons.setRight(volumePane);
        BorderPane.setMargin(controlPane, new Insets(10, 10, 10, 10));
        BorderPane.setMargin(volumePane, new Insets(10, 10, 10, 10));

        VBox controls = new VBox();
        controls.getChildren().addAll(progressPane, buttons);

        BorderPane rightHousing = new BorderPane();
        rightHousing.setCenter(videoPane);
        rightHousing.setBottom(controls);

        SplitPane bodyHousing = new SplitPane();
        bodyHousing.setDividerPositions(0.15f);
        bodyHousing.getItems().addAll(markerPane, rightHousing);
        SplitPane.setResizableWithParent(markerPane, false);

        BorderPane windowHousing = new BorderPane();
        windowHousing.setTop(createMenuBar());
        windowHousing.setCenter(bodyHousing);
        return new Scene(windowHousing, 900, 600);
    }

    public void registerMedia() throws IOException {
        if (source != null) {
            Media media = new Media(source.toURI().toString());
            mp = new MediaPlayer(media);
            mdm = new MarkerDataModel(mp, source);

            mp.setOnEndOfMedia(() -> {
                mp.seek(new Duration(0));
            });

            mp.setOnStopped(() -> {
                deregisterMedia();
            });

            // todo(john): replace this with something permenant
            mediaDebugAutoruns();

            videoPane.registerMedia(mp);
            progressPane.registerMedia(mp);
            controlPane.registerMedia(mp, mdm);
            volumePane.registerMedia(mp);
            markerPane.registerMedia(mp, mdm);
            mediaRegistered = true;
        }
    }

    public Boolean isMediaRegistered() {
        return mediaRegistered;
    }

    public void deregisterMedia() {
        if (isMediaRegistered()) {
            videoPane.deregisterMedia();
            progressPane.deregisterMedia();
            controlPane.deregisterMedia();
            volumePane.deregisterMedia();
            markerPane.deregisterMedia();

            mp.dispose();
            mp = null;
            mdm = null;
            mediaRegistered = false;
        }
    }
    
    public void seekAndUpdate(Duration seekTime) {
        mp.seek(seekTime);
    }

    private void mediaDebugAutoruns() {
        mp.setAutoPlay(true);
    }

    public void selectMedia() {
        source = fc.showOpenDialog(stage);
    }

    public void openMedia() {
        try {
            registerMedia();
        } catch (IOException ex) {
            // todo(john): prompt user with message about error opening file
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean isMediaSelected() {
        return source != null;
    }
}
