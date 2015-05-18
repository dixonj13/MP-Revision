/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.stage.Stage;

/**
 *
 * @author dixonj13
 */
public class Main extends Application {

    String mediaPath = "src/video/sample.mp4";
    private MediaPlayer mp;
    private MediaView mv;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Media Player R2");
        Scene scene = setScene();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu file = new Menu("File");
        Menu edit = new Menu("Edit");
        Menu about = new Menu("Help");
        
        menuBar.getMenus().addAll(file, edit, about);
        
       return menuBar;
    }

    public Scene setScene() {
        Media media = new Media(new File(mediaPath).toURI().toString());
        mp = new MediaPlayer(media);
        
        mp.setOnEndOfMedia(() -> {
            mp.seek(mp.getStartTime());
        });

        /* note: replace this */
        mediaDebugAutoruns();

        VideoPane vpane = new VideoPane(mp);
        BorderPane bpane = new BorderPane();
        bpane.setCenter(vpane);

        ProgressPane progress = new ProgressPane(mp);
        ControlPane control = new ControlPane(mp);
        VolumePane volume = new VolumePane(mp);
        HBox controlPane = new HBox();
        controlPane.getChildren().addAll(control, volume);
        controlPane.setAlignment(Pos.CENTER);
        VBox statusPane = new VBox();
        statusPane.getChildren().addAll(progress, controlPane);
        bpane.setBottom(statusPane);

        SplitPane spane = new SplitPane();
        spane.setDividerPositions(0.15f);

        ObservableList<String> numbers = FXCollections.observableArrayList(
                "10", "1", "13", "4", "9");
        ListView<String> numbersList = new ListView<>(numbers);
        spane.getItems().add(numbersList);

        spane.getItems().add(bpane);
        
        VBox housing = new VBox();
        housing.getChildren().addAll(createMenuBar(), spane);

        return new Scene(housing, 900, 600);
    }

    public void mediaDebugAutoruns() {
        mp.play();
        mp.setVolume(0.2);
    }
}
