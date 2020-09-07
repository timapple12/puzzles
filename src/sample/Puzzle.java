package sample;

import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Puzzle extends Parent {

    public static final int SIZE = 100;

    private final double x;
    private final double y;

    static int count = 0;

    private double deskWidth;
    private double deskHeight;

    private double startDragX;
    private double startDragY;

    private Shape pieceShape;
    private Shape pieceClip;
    private ImageView imageView = new ImageView();
    private Point2D dragAnchor;


    public Puzzle(Image image, double x, double y, double width, double height) {
        this.x = x;
        this.y = y;

        this.deskWidth = width;
        this.deskHeight = height;
        configureClip();
        configureShape();
        configImage(image);
    }

    public void configureClip() {                   // It inserts the single puzzle into a canvas
        pieceClip = createPiece();
        pieceClip.setFill(Color.WHITE);
        pieceClip.setStroke(null);
    }

    public void configureShape() {                   //It gives stroke to the puzzles
        pieceShape = createPiece();
        pieceShape.setFill(null);
        pieceShape.setStroke(Color.BLACK);
    }

    public void configImage(Image image) {
        imageView.setImage(image);                  // Here we connecting all together
        imageView.setClip(pieceClip);
        setFocusTraversable(true);
        getChildren().addAll(imageView, pieceShape);

        //setCache(true);

        setInactive();

        setOnMousePressed(me -> {
            toFront();
            startDragX = getTranslateX();
            startDragY = getTranslateY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
        });

        setOnMouseReleased(me -> {
            if (getTranslateX() < 10 && getTranslateX() > -10 &&
                    getTranslateY() < 10 && getTranslateY() > -10 && getRotate() == 0) {
                setTranslateX(0);
                setTranslateY(0);
                setInactive();
                count++;
                if(Main.numberOfPuzzles == count){
                    count = 0;
                    Main.text.setText("You won!");
                }
            }
        });

        setOnScroll(m -> {
            setRotate(getRotate() + 90);    // Rotate puzzle and if degree above 360 set it to 0
            if (getRotate() >= 360) {
                setRotate(0);
            }
        });

        setOnMouseDragged(me -> {
            double newTranslateX = startDragX
                    + me.getSceneX() - dragAnchor.getX();
            double newTranslateY = startDragY
                    + me.getSceneY() - dragAnchor.getY();
            double minTranslateX = -45f - x;
            double maxTranslateX = (deskWidth - Puzzle.SIZE + 50f) - x;
            double minTranslateY = -30f - y;
            double maxTranslateY = (deskHeight - Puzzle.SIZE + 70f) - y;
            if ((newTranslateX > minTranslateX) &&
                    (newTranslateX < maxTranslateX) &&
                    (newTranslateY > minTranslateY) &&
                    (newTranslateY < maxTranslateY)) {
                setTranslateX(newTranslateX);
                setTranslateY(newTranslateY);
            }
        });
    }


    private Shape createPiece() {
        Shape shape = createPieceRectangle();

        shape.setTranslateX(x);
        shape.setTranslateY(y);
        return shape;
    }

    private Rectangle createPieceRectangle() {
        Rectangle rec = new Rectangle();
        rec.setWidth(SIZE);
        rec.setHeight(SIZE);
        return rec;
    }

    public void setActive() {
        setDisable(false);
        setEffect(new DropShadow());
        toFront();
    }

    public void setInactive() {         // If you put the right puzzle to right place It disable any actions with puzzle
        setEffect(null);
        setDisable(true);
        toBack();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
