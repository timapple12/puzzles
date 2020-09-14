package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Andrew Valiukh
 * Can work with images with resolution above 300*200!
 */
public class Main extends Application {

    private Timeline timeline;
    protected static Text text;
    protected static int numberOfPuzzles;
    private Group root;
    private ImageView imageViewAutoSolving;
    private List<ImageView> tiles = new ArrayList();
    private int numOfColumns;
    private int numOfRows;
    private String format = ".jpeg";
    private String name = "download";
    private int horizontalError=1000;
    private int verticalError = 450;
    private int numbsOfTurnsH;
    private int numbsOfTurnsV;

    private void init(Stage primaryStage){
        imageViewAutoSolving = new ImageView();
        root = new Group();
        primaryStage.setScene(new Scene(root));

        text = new Text();
        Image image = new Image(getClass().getResourceAsStream(
                name + ".jpg"));

        numOfColumns = (int) (image.getWidth() / Puzzle.SIZE);
        numOfRows = (int) (image.getHeight() / Puzzle.SIZE);
        numberOfPuzzles = numOfColumns * numOfRows;

        if (numOfColumns > 3 || numOfRows > 2) {
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

        /**
         * This method split image into tiles, uncomment if needed
         * */
        BufferedImage image1 = SwingFXUtils.fromFXImage(image, null);
        splitImage(image1);

        Button mixButton = new Button("Mix");
        onClickMixButtonAction(desk, Puzzles, mixButton);

        Button solveButton = new Button("Solve");
        onClickSolveButtonAction(Puzzles, solveButton);
        solveButton.setTranslateX(Desk.deskWith - solveButton.getWidth() - 110);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(mixButton, solveButton, text);

        Text text = new Text("Original image:");
        text.setTranslateX((Desk.deskWith / 2) - 50);

        ImageView imageView = new ImageView(image);
        imageView.setTranslateX(Desk.deskWith / 2 - image.getWidth() / 2);

        VBox vb = new VBox(20);
        vb.getChildren().addAll(desk, buttonBox, text, imageView);
        root.getChildren().addAll(vb);

    }

    private void onClickSolveButtonAction(List<Puzzle> puzzles, Button solveButton) {

        timeline = new Timeline();

        solveButton.setOnAction(actionEvent -> {

            int column = 0;
            int savedPhotoNumber = 0;
            int counter;
            BufferedImage bufferedImage;

            text.setText("AutoSolving");
            text.setTranslateX(170);
            if (timeline != null) {
                timeline.stop();
            }

            for (Puzzle Puzzle : puzzles) {

                Puzzle.setInactive();
                Puzzle.setRotate(0);
                Puzzle.setVisible(false);
            }

            Map<Integer, BufferedImage> map = new HashMap<>();
            List<Integer> rightPixels = new ArrayList<>();
            List<Integer> leftPixels = new ArrayList<>();
            List<Integer> topPixels = new ArrayList<>();
            List<Integer> bottomPixels = new ArrayList<>();

            for (Integer i : getRandomSet(numberOfPuzzles)) {
                savedPhotoNumber++;
                try {
                    bufferedImage = ImageIO.read(new File("/home/andrew/puzzles/src/tiles/" + i + format));
                    map.put(savedPhotoNumber, bufferedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            while (true) {
                counter = 0;
                numbsOfTurnsH++;
                numbsOfTurnsV++;
                for (Integer i : getRandomSet(numberOfPuzzles)) {

                    imageViewAutoSolving = new ImageView();
                    imageViewAutoSolving.setImage(SwingFXUtils.toFXImage(map.get(i), null));

                    if (counter < 2) {
                        imageViewAutoSolving.setTranslateX(0);
                        imageViewAutoSolving.setTranslateY(100 * column);
                    }
                    if (counter >= 2 && counter < 4) {
                        imageViewAutoSolving.setTranslateX(100);
                        imageViewAutoSolving.setTranslateY(100 * column);
                    }
                    if (counter >= 4 && counter < 6) {
                        imageViewAutoSolving.setTranslateX(200);
                        imageViewAutoSolving.setTranslateY(100 * column);
                    }

                    counter++;
                    column++;

                    if (column >= 2) {
                        column = 0;
                    }

                    for (int k = 0; k < 100; k++) {
                        int rgb = map.get(i).getRGB(99, k);
                        putRgbToList(rightPixels, rgb);
                    }

                    for (int k = 0; k < 100; k++) {
                        int rgb = map.get(i).getRGB(0, k);
                        putRgbToList(leftPixels, rgb);
                    }
                    for (int k = 0; k < 100; k++) {
                        int rgb = map.get(i).getRGB(k, 99);
                        putRgbToList(topPixels, rgb);
                    }
                    for (int k = 0; k < 100; k++) {
                        int rgb = map.get(i).getRGB(k, 0);
                        putRgbToList(bottomPixels, rgb);
                    }
                    tiles.add(imageViewAutoSolving);
                }
                if (horizontalSort(rightPixels, leftPixels) && verticalSort(topPixels, bottomPixels)) {
                    rightPixels.clear();
                    leftPixels.clear();
                    topPixels.clear();
                    bottomPixels.clear();
                    root.getChildren().addAll(tiles);
                    break;
                }
                imageViewAutoSolving = null;
                tiles.clear();
                rightPixels.clear();
                leftPixels.clear();
                topPixels.clear();
                bottomPixels.clear();
            }
        });
    }

    private void putRgbToList(List<Integer> list, int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        list.add(r);
        list.add(g);
        list.add(b);
    }

    private boolean horizontalSort(List<Integer> rightPixels, List<Integer> leftPixels) {
        int matchedPixels = 0;

        for (int j = 0; j < 1800; j++) {

            if (j <= 300) {
                if (Math.abs(rightPixels.get(j) - leftPixels.get(j + Puzzle.SIZE * 6)) < 18) {
                    matchedPixels++;
                }
            }

            if (j > 300 && j <= 600) {
                if (Math.abs(rightPixels.get(j) - leftPixels.get(j + Puzzle.SIZE * 6)) < 18) {
                    matchedPixels++;
                }
            }

            if (j > 600 && j <= 900) {
                if (Math.abs(rightPixels.get(j) - leftPixels.get(j + Puzzle.SIZE * 6)) < 18) {
                    matchedPixels++;
                }
            }

            if (j > 900 && j < 1200) {
                if (Math.abs(rightPixels.get(j) - leftPixels.get(j + Puzzle.SIZE * 6)) < 18) {
                    matchedPixels++;

                }
            }
        }
        if(numbsOfTurnsH>400){
            horizontalError -=10;
            numbsOfTurnsH=0;
        }

        if (matchedPixels > horizontalError) {      /** Change the number to increase/decrease errors */
            return true;
        }
        return false;
    }
    private boolean verticalSort(List<Integer> topPixels, List<Integer> bottomPixels){
        int matchedPixels = 0;
        for (int j = 0; j < 900; j++) {
            if(j<=300){
                if (Math.abs(topPixels.get(j) - bottomPixels.get(j + Puzzle.SIZE * 3)) < 10) {
                    matchedPixels++;
                }
            }
            if(j>300&&j<=600){
                if (Math.abs(topPixels.get(j) - bottomPixels.get(j + Puzzle.SIZE * 3)) < 10) {
                    matchedPixels++;
                }
            }
            if(j>600&&j<900){
                if (Math.abs(topPixels.get(j) - bottomPixels.get(j + Puzzle.SIZE * 3)) < 10) {
                    matchedPixels++;
                }
            }
        }
        if(numbsOfTurnsV>200){
            verticalError-=5;
            numbsOfTurnsV=0;
        }
        if(matchedPixels > verticalError){
            return true;
        }
        return false;
    }
    private Set<Integer> getRandomSet(int bound) {
        int numbersNeeded = 6;
        if (bound < numbersNeeded) {
            throw new IllegalArgumentException("Can't ask for more numbers than are available");
        }
        Random rng = new Random();
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < numbersNeeded) {
            Integer next = rng.nextInt(bound) + 1;
            generated.add(next);
        }
        return generated;
    }

    private List<Long> splitImage(BufferedImage image) {
        List<Long> colours = new ArrayList<>();
        int tWidth = image.getWidth();
        int tHeight = image.getHeight();
        int row = 2;
        int col = 3;
        int eWidth = tWidth / col;
        int eHeight = tHeight / row;
        int x = 0;
        int y = 0;
        int count = 0;

        for (int i = 0; i < row; i++) {
            y = 0;
            for (int j = 0; j < col; j++) {
                try {
                    System.out.println("creating piece: " + i + " " + j);
                    count++;
                    BufferedImage SubImgage = image.getSubimage(y, x, eWidth, eHeight);
                    y += eWidth;
                    ImageIO.write(SubImgage, "jpeg", new File("/home/andrew/puzzles/src/tiles/" + count + format));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            x += eHeight;
        }

        return colours;
    }

    private void onClickMixButtonAction(Desk desk, List<Puzzle> puzzles, Button shuffleButton) {

        shuffleButton.setOnAction(actionEvent -> {
            root.getChildren().removeAll(tiles);
            imageViewAutoSolving.setVisible(false);
            text.setText("Mixed!");
            text.setTranslateX(170);
            if (timeline != null) {
                timeline.stop();
            }

            timeline = new Timeline();
            for (Puzzle Puzzle : puzzles) {
                Puzzle.setActive();
                Puzzle.setVisible(true);

                double mixX = Math.random() *
                        (desk.getWidth() - Puzzle.SIZE + 30f) - Puzzle.getX();
                double mixY = Math.random() *
                        (desk.getHeight() - Puzzle.SIZE + 30f) - Puzzle.getY();

                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(0.4),
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
    public void start(Stage stage) throws IOException {
        init(stage);
        stage.show();
    }
}
