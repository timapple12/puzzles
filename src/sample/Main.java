package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew Valiukh
 * */
public class Main extends Application {

    private Timeline timeline;
    protected static Text text;
    protected static int numberOfPuzzles;

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setScene(new Scene(root));

        text = new Text("");
        text.setTranslateX(-45);

        Image image = new Image(getClass().getResourceAsStream(
                "download.jpeg"));

        int numOfColumns = (int) (image.getWidth() / Puzzle.SIZE);
        int numOfRows = (int) (image.getHeight() / Puzzle.SIZE);
        numberOfPuzzles = numOfColumns*numOfRows;

        if (numOfColumns > 1000 || numOfRows > 1000) {
            throw new IllegalArgumentException("Too large photo!");
        }

        final Desk desk = new Desk(numOfColumns, numOfRows);

        final List<Puzzle> Puzzles = new ArrayList();
        for (int col = 0; col < numOfColumns; col++) {
            for (int row = 0; row < numOfRows; row++) {
                int x = col * Puzzle.SIZE;
                int y = row * Puzzle.SIZE;
                final Puzzle Puzzle = new Puzzle(image, x, y, desk.getWidth(), desk.getHeight());
                Puzzles.add(Puzzle);
            }
        }
        desk.getChildren().addAll(Puzzles);


        Button mixButton = new Button("Mix");
        onClickMixButtonAction(desk, Puzzles, mixButton);

        Button solveButton = new Button("Solve");
        onClickSolveButtonAction(Puzzles, solveButton);
        solveButton.setTranslateX(100);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(mixButton, solveButton, text);

        VBox vb = new VBox(10);
        vb.getChildren().addAll(desk, buttonBox);
        root.getChildren().addAll(vb);
    }

    private void onClickSolveButtonAction(List<Puzzle> puzzles, Button solveButton) {

        solveButton.setOnAction(actionEvent -> {
            text.setText("AutoSolving");
            if (timeline != null){
                timeline.stop();
            }

            timeline = new Timeline();
            for (Puzzle Puzzle : puzzles) {
                Puzzle.setInactive();
                Puzzle.setRotate(0);
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(0.3),
                                new KeyValue(Puzzle.translateXProperty(), 0),
                                new KeyValue(Puzzle.translateYProperty(), 0)));
            }
            timeline.playFromStart();
        });
    }

    private void onClickMixButtonAction(Desk desk, List<Puzzle> puzzles, Button shuffleButton) {
        shuffleButton.setOnAction(actionEvent -> {
            text.setText("Mixed!");
            if (timeline != null) {
                timeline.stop();
            }

            timeline = new Timeline();
            for (Puzzle Puzzle : puzzles) {
                Puzzle.setActive();
                double mixX = Math.random() *
                        (desk.getWidth() - Puzzle.SIZE + 30f) - Puzzle.getX();
                double mixY = Math.random() *
                        (desk.getHeight() - Puzzle.SIZE + 30f) - Puzzle.getY();
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(0.3),
                                new KeyValue(Puzzle.translateXProperty(), mixX),
                                new KeyValue(Puzzle.translateYProperty(), mixY)));
            }
            timeline.playFromStart();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        init(stage);
        stage.show();
    }
}
