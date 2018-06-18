import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.*;

import static java.lang.Math.*;

class Cell {
    private int indexX, indexY;
    private boolean mined, opened, marked;
    private int nOfNeighbors = 0;
    private Label nOfNeighborsLabel = new Label();
    private Polygon hexagon = new Polygon();
    private Polygon actionListenerHexagon = new Polygon();
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

    Label getNOfNeighborsLabel() {
        return nOfNeighborsLabel;
    }

    Polygon getActionListenerHexagon() {
        return actionListenerHexagon;
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
            nOfNeighborsLabel.setText(String.valueOf(nOfNeighbors));
        }
        else {
            nOfNeighborsLabel.setText("");
        }
    }

    private void createCell(double coordX, double coordY, double sideLength) {
        double _sideLength = sideLength * 0.95;
        for (int i = 0; i < 6; i++) {
            hexagon.getPoints().add(coordX + _sideLength * cos((2 * i + 1) * PI / 6));
            hexagon.getPoints().add(coordY + _sideLength * sin((2 * i + 1) * PI / 6));
        }

        actionListenerHexagon.getPoints().addAll(hexagon.getPoints());
        actionListenerHexagon.setFill(Color.TRANSPARENT);

        setColor();
        setNOfNeighbors();

        nOfNeighborsLabel.setLayoutX(coordX - _sideLength * sqrt(3) / 2);
        nOfNeighborsLabel.setLayoutY(coordY - _sideLength);
        nOfNeighborsLabel.setMinWidth(_sideLength * sqrt(3));
        nOfNeighborsLabel.setMinHeight(_sideLength * 2);
        nOfNeighborsLabel.setFont(new Font(_sideLength));
        nOfNeighborsLabel.setAlignment(Pos.CENTER);
    }
}
