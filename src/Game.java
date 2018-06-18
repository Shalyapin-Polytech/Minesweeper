import java.util.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import static java.lang.StrictMath.sqrt;

class Game {
    private List<List<Cell>> field = new ArrayList<>();
    private Group group = new Group();
    private Label remainingNOfMarksLabel = new Label();
    private boolean blocked;
    private int width, height;
    private int nOfMines, remainingNOfMarks, remainingNOfMines;

    Game(String mode, double windowWidth) {
        width = 36;
        height = (int) ((width / Main.ASPECT_RATIO) * (2.0 / sqrt(3)) * 0.9);
        int nOfCells = width * height;
        double proportionOfMines = 0;

        if (mode == null) {
            mode = "medium";
        }
        switch (mode) {
            case "easy":
                proportionOfMines = 0.1;
                break;
            case "medium":
                proportionOfMines = 0.15;
                break;
            case "hard":
                proportionOfMines = 0.18;
                break;
        }
        nOfMines = (int) (nOfCells * proportionOfMines);

        createField(windowWidth);
        setMines();
        findNeighbors();
    }

    Group getGroup() {
        return group;
    }

    private void setRemainingNOfMarksLabel(int remainingNOfMines) {
        remainingNOfMarksLabel.setText(String.valueOf(remainingNOfMines));
    }

    Label getRemainingNOfMarksLabel() {
        return remainingNOfMarksLabel;
    }

    private void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    void restart() {
        field.forEach(cells -> cells.forEach(Cell::clear));
        setMines();
        findNeighbors();
        setBlocked(false);
    }

    void openAll() {
        field.forEach(cells -> cells.forEach(Cell::setOpened));
        setBlocked(true);
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

    private void openWithNeighbors(Cell cell) {
        cell.setOpened();
        List<Cell> neighbors = getNeighbors(cell);
        if (!cell.isMined() && cell.getNOfNeighbors() == 0) {
            for (Cell neighbor : neighbors) {
                if (!neighbor.isOpened()) {
                    openWithNeighbors(neighbor);
                }
            }
        }
    }

    private void createGameResultStage(boolean win) {
        Stage resultStage = new Stage(StageStyle.TRANSPARENT);

        Text resultText = new Text();
        if (win) {
            resultText.setText("Вы выиграли");
        }
        else {
            resultText.setText("Вы проиграли");
        }

        HBox resultPane = new HBox();
        resultPane.setMinWidth(500);
        resultPane.setMinHeight(500 / Main.ASPECT_RATIO);
        resultPane.getChildren().add(resultText);
        resultPane.setAlignment(Pos.CENTER);
        resultPane.getStyleClass().add("result-pane");
        resultPane.setOnMouseClicked(t -> resultStage.close());

        Scene resultScene = new Scene(resultPane);
        resultScene.getStylesheets().add("styles.css");
        resultScene.setFill(Color.TRANSPARENT);

        resultStage.setScene(resultScene);
        resultStage.show();
    }

    private void addMouseListener(Cell cell) {
        cell.getActionListenerHexagon().setOnMousePressed(t -> {
            if (!blocked) {
                if (t.isPrimaryButtonDown()) {
                    if (cell.isMarked()) {
                        cell.setMarked(false);
                        remainingNOfMarks++;
                    }
                    if (cell.isMined()) {
                        openAll();
                        setBlocked(true);
                        createGameResultStage(false);
                    }
                    openWithNeighbors(cell);
                }
                if (t.isSecondaryButtonDown()) {
                    if (cell.isMarked()) {
                        cell.setMarked(false);
                        remainingNOfMarks++;
                        if (cell.isMined()) {
                            remainingNOfMines++;
                        }
                    }
                    else if (remainingNOfMarks > 0) {
                        cell.setMarked(true);
                        remainingNOfMarks--;
                        if (cell.isMined()) {
                            remainingNOfMines--;
                            if (remainingNOfMines == 0) {
                                setBlocked(true);
                                createGameResultStage(true);
                            }
                        }
                    }
                }
                setRemainingNOfMarksLabel(remainingNOfMarks);
            }
        });
    }

    private void findNeighbors() {
        field.forEach(cells -> cells.forEach(cell -> {
            List<Cell> neighbors = getNeighbors(cell);
            for (Cell neighbor : neighbors) {
                if (neighbor.isMined()) {
                    cell.setNOfNeighbors(cell.getNOfNeighbors() + 1);
                }
            }
        }));
    }

    private void setMines() {
        remainingNOfMarks = nOfMines;
        remainingNOfMines = nOfMines;
        setRemainingNOfMarksLabel(remainingNOfMarks);
        for (int i = nOfMines; i > 0;) {
            int randWidth = new Random().nextInt(width);
            int randHeight = new Random().nextInt(height);
            if (!field.get(randWidth).get(randHeight).isMined()) {
                field.get(randWidth).get(randHeight).setMined();
                i--;
            }
        }
    }

    private void createField(double windowWidth) {
        double sideLength = windowWidth / ( sqrt(3) * width );
        for (int i = 0; i < width; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell(
                    (i + 0.5 * (j % 2)) * sideLength * sqrt(3),
                    j * sideLength * 1.5,
                    sideLength
                );
                cell.setIndexX(i);
                cell.setIndexY(j);
                addMouseListener(cell);
                row.add(cell);

                group.getChildren().addAll(
                    cell.getHexagon(),
                    cell.getNOfNeighborsLabel(),
                    cell.getActionListenerHexagon()
                );
            }
            field.add(row);
        }
    }
}
