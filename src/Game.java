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
        height = (int) (width * 0.9 / Main.ASPECT_RATIO * (2.0 / sqrt(3)));

        if (mode == null) {
            mode = "medium";
        }
        switch (mode) {
            case "easy":
                nOfMines = 70;
                break;
            case "medium":
                nOfMines = 100;
                break;
            case "hard":
                nOfMines = 130;
                break;
        }

        createField(windowWidth);
        setMines();
        findNeighbors();
        addMouseListener();
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
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field.get(i).get(j).clear();
            }
        }
        setMines();
        findNeighbors();
        setBlocked(false);
    }

    void openAll() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                field.get(i).get(j).setOpened();
            }
        }
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

    private void addMouseListener() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                Cell cell = field.get(i).get(j);
                cell.getNOfNeighborsPane().setOnMousePressed(t -> {

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

                            openWithNeighbours(cell);
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
        remainingNOfMarks = nOfMines;
        remainingNOfMines = nOfMines;
        setRemainingNOfMarksLabel(remainingNOfMarks);
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

    private void createField(double windowWidth) {
        double sideLength = windowWidth / ( sqrt(3) * width );
        for (int i = 0; i < width; i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < height; j++) {
                Cell cell = new Cell((i + 0.5 * (j % 2)) * sideLength * sqrt(3), j * sideLength * 1.5, sideLength);
                cell.setIndexX(i);
                cell.setIndexY(j);
                row.add(cell);

                group.getChildren().add(cell.getHexagon());
                group.getChildren().add(cell.getNOfNeighborsPane());
            }
            field.add(row);
        }
    }
}
