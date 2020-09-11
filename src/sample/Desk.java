package sample;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Desk extends Pane {
    
    public static double deskWith;
    public static double deskHeight;
    
    Desk(int numOfColumns, int numOfRows) {
        setStyle("-fx-background-color: #cccccc;");

        deskWith = Puzzle.SIZE * numOfColumns * 2;
        deskHeight = Puzzle.SIZE * numOfRows;

        setPrefSize(deskWith,deskHeight);           // Creating desk with right sizes
        setMaxSize(deskWith, deskHeight);
        autosize();

        Path grid = new Path();
        grid.setStroke(Color.rgb(70, 20, 200));
        getChildren().add(grid);

        //   Create vertical lines
        for (int col = 0; col < numOfColumns - 1; col++) {
            grid.getElements().addAll(
                    new MoveTo(Puzzle.SIZE + Puzzle.SIZE * col, 5),
                    new LineTo(Puzzle.SIZE + Puzzle.SIZE * col, Puzzle.SIZE * numOfRows - 5)
            );
        }

        //   Create horizontal lines
        for (int row = 0; row < numOfRows - 1; row++) {
            grid.getElements().addAll(
                    new MoveTo(5, Puzzle.SIZE + Puzzle.SIZE * row),
                    new LineTo(Puzzle.SIZE * numOfColumns - 5, Puzzle.SIZE + Puzzle.SIZE * row)
            );
        }
    }
    @Override protected void layoutChildren() {}
}
