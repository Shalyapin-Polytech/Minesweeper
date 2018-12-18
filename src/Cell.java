import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import static java.lang.Math.*;

class Cell {
    private Polygon hexagon = new Polygon();
    private Polygon actionListenerHexagon = new Polygon();
    private Label label = new Label();
    private int indexX, indexY;
    private int nOfMinedNeighbors = 0;
    private boolean mined, opened, marked, demonstrated;

    private static final Color CLOSED_CELL = Color.rgb(128, 128, 128);
    private static final Color OPENED_EMPTY_CELL = Color.rgb(253, 234, 168);
    private static final Color OPENED_MINED_CELL = Color.rgb(220, 20, 60);
    private static final Color MARKED_CELL = Color.rgb(0, 0, 0);
    private static final Color DEMONSTRATED_NOT_MARKED_CELL = Color.rgb(128, 0, 0);
    private static final Color DEMONSTRATED_MARKED_CELL = Color.GREEN;

    Cell(double coordX, double coordY, double sideLength) {
        this.opened = false;
        this.mined = false;

        createCell(coordX, coordY, sideLength);
    }

    Polygon getHexagon() {
        return hexagon;
    }

    Polygon getActionListenerHexagon() {
        return actionListenerHexagon;
    }

    Label getNOfMinedNeighborsLabel() {
        return label;
    }

    void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    int getIndexX() {
        return indexX;
    }

    void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    int getIndexY() {
        return indexY;
    }

    void setNOfMinedNeighbors(int nOfMinedNeighbors) {
        this.nOfMinedNeighbors = nOfMinedNeighbors;
        rewriteLabel();
    }

    int getNOfMinedNeighbors() {
        return nOfMinedNeighbors;
    }

    void mine() {
        mined = true;
    }

    boolean isMined() {
        return mined;
    }

    void open() {
        opened = true;
        marked = false;
        recolor();
        rewriteLabel();
    }

    boolean isOpened() {
        return opened;
    }

    void mark(boolean marked) {
        this.marked = marked;
        recolor();
        rewriteLabel();
    }

    boolean isMarked() {
        return marked;
    }

    void demonstrate() {
        demonstrated = true;
        recolor();
        rewriteLabel();
    }

    private void recolor() {
        if (demonstrated && mined) {
            if (marked) {
                hexagon.setFill(DEMONSTRATED_MARKED_CELL);
            }
            else {
                hexagon.setFill(DEMONSTRATED_NOT_MARKED_CELL);
            }
        }
        else if (marked) {
            hexagon.setFill(MARKED_CELL);
        }
        else if (opened) {
            if (mined) {
                hexagon.setFill(OPENED_MINED_CELL);
            }
            else {
                hexagon.setFill(OPENED_EMPTY_CELL);
            }
        }
        else {
            hexagon.setFill(CLOSED_CELL);
        }
    }

    private void rewriteLabel() {
        String text = "";
        if (opened && !mined && !marked && nOfMinedNeighbors > 0) {
            text = String.valueOf(nOfMinedNeighbors);
        }
        label.setText(text);
    }

    private void createCell(double coordX, double coordY, double sideLength) {
        sideLength = sideLength * 0.95;
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().add(coordX + sideLength * cos((2 * i + 1) * PI / 6));
            hexagon.getPoints().add(coordY + sideLength * sin((2 * i + 1) * PI / 6));
        }

        actionListenerHexagon.getPoints().addAll(hexagon.getPoints());
        actionListenerHexagon.setFill(Color.TRANSPARENT);

        recolor();
        rewriteLabel();

        label.setLayoutX(coordX - sideLength * sqrt(3) / 2);
        label.setLayoutY(coordY - sideLength);
        label.setMinWidth(sideLength * sqrt(3));
        label.setMinHeight(sideLength * 2);
        label.setFont(new Font(sideLength));
        label.setAlignment(Pos.CENTER);
    }

    @Override
    public String toString() {
        return "[" + indexX + ", " + indexY + "]";
    }
}
