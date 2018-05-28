package sample;

import javafx.scene.Group;
import javafx.scene.control.Label;
import java.util.*;
import static java.lang.StrictMath.sqrt;
import static sample.Main.ASPECT_RATIO;

class Field {
    private List<List<Cell>> field = new ArrayList<>();
    private Group group = new Group();
    private Label remainingNOfMinesLabel = new Label();
    private int width, height;
    private int nOfMines, remainingNOfMines;
    private double windowWidth;
    private static final String DEFAULT_MODE = "medium";

    Field(String mode, int nOfMines, double windowWidth) {
        this.nOfMines = nOfMines;
        this.windowWidth = windowWidth;

        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        switch (mode) {
            case "easy":
                width = 24;
                break;
            case "medium":
                width = 36;
                break;
            case "hard":
                width = 48;
                break;
        }
        height = (int) (width / ASPECT_RATIO * (2.0 / sqrt(3)));

        createField();
        setMines();
        findNeighbors();
        addMouseListener();
    }

    Group getGroup() {
        return group;
    }

    private void setRemainingNOfMinesLabel(int remainingNOfMines) {
        remainingNOfMinesLabel.setText(String.valueOf(remainingNOfMines));
    }

    Label getRemainingNOfMinesLabel() {
        return remainingNOfMinesLabel;
    }

    void restart() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field.get(i).get(j).clear();
            }
        }
        setMines();
        findNeighbors();
    }

    void openAll() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field.get(i).get(j).setOpened();
            }
        }
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int indexX = cell.getIndexX();
        int indexY = cell.getIndexY();
        int[] listX;
        if (indexY % 2 == 0) {
            listX = new int[]{-1, 0, 1, 0, - 1, -1};
        }
        else {
            listX = new int[]{0, 1, 1, 1, 0 , -1};
        }
        int[] listY = {-1, -1, 0, 1, 1, 0};

        for (int n = 0; n < 6; n++) {
            int addX = listX[n];
            int addY = listY[n];
            if (
                !(
                    addX > 0 && indexX + addX >= width ||
                    addX < 0 && indexX + addX < 0 ||
                    addY > 0 && indexY + addY >= height ||
                    addY < 0 && indexY + addY < 0

                )
            ) {
                neighbors.add(field.get(indexX + addX).get(indexY + addY));
            }
        }
        return neighbors;
    }

    private void openWithNeighbours(Cell cell) {
        cell.setOpened();
        List<Cell> neighbors = getNeighbors(cell);
        if (!cell.isMined() && cell.getNOfNeighbors() == 0) {
            for (Cell neighbor : neighbors) {
                if (!neighbor.isOpened()) {
                    openWithNeighbours(neighbor);
                }
            }
        }
    }

    private void addMouseListener() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = field.get(i).get(j);
                cell.getHexagon().setOnMousePressed(t -> {
                    if (t.isPrimaryButtonDown()) {
                        openWithNeighbours(cell);
                    }
                    if (t.isSecondaryButtonDown()) {
                        cell.setMarked();
                        if (cell.isMarked()) {
                            remainingNOfMines--;
                        }
                        else {
                            remainingNOfMines++;
                        }
                        setRemainingNOfMinesLabel(remainingNOfMines);
                    }
                });
            }
        }
    }

    private void findNeighbors() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Cell cell = field.get(i).get(j);
                List<Cell> neighbors = getNeighbors(cell);
                for (Cell neighbor : neighbors) {
                    if (neighbor.isMined()) {
                        cell.incNOfNeighbors();
                    }
                }
            }
        }
    }

    private void setMines() {
        remainingNOfMines = nOfMines;
        setRemainingNOfMinesLabel(remainingNOfMines);
        int _nOfMines = nOfMines;
        while (_nOfMines > 0) {
            int randWidth = new Random().nextInt(width);
            int randHeight = new Random().nextInt(height);
            if (!field.get(randWidth).get(randHeight).isMined()) {
                field.get(randWidth).get(randHeight).setMined();
                _nOfMines--;
            }
        }
    }

    private void createField() {
        double sideLength = windowWidth / ( sqrt(3) * width );
        for (int i = 0; i < width; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell((i + 0.5 * (j % 2)) * sideLength * sqrt(3), j * sideLength * 1.5, sideLength);
                row.add(cell);
                cell.setIndexX(i);
                cell.setIndexY(j);
                group.getChildren().add(cell.getHexagon());
                group.getChildren().add(cell.getNOfNeighborsPane());
            }
            field.add(row);
        }
    }
}
