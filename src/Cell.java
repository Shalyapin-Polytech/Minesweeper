import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.*;

import static java.lang.Math.*;

class Cell {
    private int indexX, indexY;
    private boolean mined, opened, marked;
    private int nOfNeighbors = 0;
    private Text nOfNeighborsText = new Text();
    private StackPane nOfNeighborsPane = new StackPane();
    private Polygon hexagon = new Polygon();
    private static final Color CLOSED_CELL = Color.rgb(128, 128, 128);
    private static final Color OPENED_EMPTY_CELL = Color.rgb(253, 234, 168);
    private static final Color OPENED_MINED_CELL = Color.rgb(220, 20, 60);
    private static final Color MARKED_CELL = Color.rgb(0, 0, 0);

    Cell(double coordX, double coordY, double sideLength) {
        this.opened = false;
        this.mined = false;

        createCell(coordX, coordY, sideLength);
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

    Polygon getHexagon() {
        return hexagon;
    }

    StackPane getNOfNeighborsPane() {
        return nOfNeighborsPane;
    }

    void incNOfNeighbors() {
        nOfNeighbors++;
    }

    int getNOfNeighbors() {
        return nOfNeighbors;
    }

    void setMined() {
        mined = true;
    }

    boolean isMined() {
        return mined;
    }

    void setOpened() {
        opened = true;
        marked = false;
        setColor();
        setNOfNeighbors();
    }

    boolean isOpened() {
        return opened;
    }

    void setMarked(boolean marked) {
        this.marked = marked;
        setColor();
        setNOfNeighbors();
    }

    boolean isMarked() {
        return marked;
    }

    void clear() {
        opened = false;
        mined = false;
        marked = false;
        setColor();
        nOfNeighbors = 0;
        setNOfNeighbors();
    }

    private void setColor() {
        if (!marked) {
            if (opened) {
                if (mined) {
                    hexagon.setFill(OPENED_MINED_CELL);
                } else {
                    hexagon.setFill(OPENED_EMPTY_CELL);
                }
            }
            else {
                hexagon.setFill(CLOSED_CELL);
            }
        }
        else {
            hexagon.setFill(MARKED_CELL);
        }
    }

    private void setNOfNeighbors() {
        if (opened && !mined && !marked && nOfNeighbors > 0) {
            nOfNeighborsText.setText(String.valueOf(nOfNeighbors));
        }
        else {
            nOfNeighborsText.setText("");
        }
    }

    private void createCell(double coordX, double coordY, double sideLength) {
        double _sideLength = sideLength * 0.95;
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().add(coordX + _sideLength * cos((2 * i + 1) * PI / 6));
            hexagon.getPoints().add(coordY + _sideLength * sin((2 * i + 1) * PI / 6));
        }
        setColor();
        setNOfNeighbors();

        nOfNeighborsText.setFont(new Font(sideLength));
        nOfNeighborsText.setBoundsType(TextBoundsType.VISUAL);
        nOfNeighborsText.setLayoutX(coordX);
        nOfNeighborsText.setLayoutY(coordY);

        nOfNeighborsPane.setLayoutX(coordX);
        nOfNeighborsPane.setLayoutY(coordY);
        nOfNeighborsPane.getChildren().add(nOfNeighborsText);
    }
}
