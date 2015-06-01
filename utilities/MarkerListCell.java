/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author John
 */
public class MarkerListCell extends ListCell<MarkerBean> {

    private static final Image PLAY = new Image("image/marker-play.png");
    private static final ImageView playIcon = new ImageView(PLAY);

    public MarkerListCell() {
        playIcon.setFitHeight(11);
        playIcon.setFitWidth(11);
    }

    @Override
    public void updateItem(MarkerBean marker, boolean empty) {
        super.updateItem(marker, empty);
        this.setText(null);

        if (empty) {
            this.setGraphic(null);
        } else {
            Label time = new Label(Conversions.durationToDynamicHrMnSc(marker.getTime()));
            Label title = new Label(marker.getTitle());
            title.setFont(Font.font("", FontWeight.BOLD, 12));

            GridPane grid = new GridPane();
            grid.setHgap(6);
            
            grid.add(title, 1, 0);
            grid.add(time, 1, 1);

            if (marker.isPlaying()) {
                grid.add(playIcon, 0, 0, 1, 2);
            }

            this.setGraphic(grid);
        }
    }
}
