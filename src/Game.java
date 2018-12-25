import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.*;
import static java.lang.StrictMath.sqrt;

class Game {
    private List<List<Cell>> field = new ArrayList<>();
    private Group group = new Group();
    private Set<Cell> isolatedClosedCells = new HashSet<>();
    private Set<Cell> openedBorderCells = new HashSet<>();
    private double windowWidth, windowHeight;
    private int width, height;
    private GameMode gameMode;
    private int remainingNOfMarks, remainingNOfMines = 0;
    private boolean finished, firstMove;

    Game(double windowWidth, double windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    Group getGroup() {
        return group;
    }

    Set<Cell> getIsolatedClosedCells() {
        return isolatedClosedCells;
    }

    Set<Cell> getOpenedBorderCells() {
        return openedBorderCells;
    }

    void setWidth(int width) {
        this.width = width;
    }

    void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    int getRemainingNOfMarks() {
        return remainingNOfMarks;
    }

    boolean isFinished() {
        return finished;
    }

    boolean isFirstMove() {
        return firstMove;
    }

    void restart() {
        clearField();
        createField();
        finished = false;
    }

    void demonstrateMines() {
        field.forEach(row -> row.forEach(cell -> {
            if (cell.isMined() && !cell.isOpened()) {
                cell.demonstrate();
            }
        }));
        finished = true;
    }

    Set<Cell> getNeighbors(Cell cell) {
        Set<Cell> neighbors = new HashSet<>();
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

    private void finish(boolean result) {
        demonstrateMines();
        Main.createGameResultStage(result);
    }

    private void openWithNeighbors(Cell cell) {
        cell.open();
        Set<Cell> neighbors = getNeighbors(cell);
        isolatedClosedCells.remove(cell);
        isolatedClosedCells.removeAll(neighbors);
        neighbors.forEach(neighbor -> {
            Set<Cell> closedGrandNeighbors = getNeighbors(neighbor).stream()
                    .filter(grandNeighbor -> !grandNeighbor.isOpened() && !grandNeighbor.isMarked())
                    .collect(Collectors.toSet());
            if (closedGrandNeighbors.size() == 0) {
                openedBorderCells.remove(neighbor);
            }
            if (!cell.isMined() && cell.getNOfMinedNeighbors() == 0 && !neighbor.isOpened() && !neighbor.isMarked()) {
                openWithNeighbors(neighbor);
            }
        });

        if (!cell.isMined() && cell.getNOfMinedNeighbors() > 0) {
            openedBorderCells.add(cell);
        }
    }

    private void mark(Cell cell, boolean marked) {
        cell.mark(marked);
        if (marked) {
            remainingNOfMarks--;
            if (cell.isMined()) {
                remainingNOfMines--;
            }
        }
        else {
            remainingNOfMarks++;
            if (cell.isMined()) {
                remainingNOfMines++;
            }
        }
    }

    private enum Action {
        OPEN, MARK
    }

    private void action(Cell cell, Action action) {
        if (!finished) {
            if (firstMove) {
                setMines(cell);
                firstMove = false;
            }
            if (action == Action.OPEN) {
                openWithNeighbors(cell);
                if (cell.isMarked()) {
                    mark(cell, false);
                }
                if (cell.isMined()) {
                    finish(false);
                }
            }
            if (action == Action.MARK) {
                if (cell.isMarked()) {
                    mark(cell, false);
                }
                else if (remainingNOfMarks > 0) {
                    mark(cell, true);
                    if (remainingNOfMines == 0) {
                        finish(true);
                    }
                }
            }
            Main.setRemainingNOfMarksLabel(remainingNOfMarks);
        }
    }

    void open(Cell cell) {
        action(cell, Action.OPEN);
    }

    void mark(Cell cell) {
        action(cell, Action.MARK);
    }

    private void addMouseListener(Cell cell) {
        cell.getActionListenerHexagon().setOnMousePressed(t -> {
            if (t.isPrimaryButtonDown()) {
                open(cell);
            }
            if (t.isSecondaryButtonDown()) {
                mark(cell);
            }
        });
    }

    private void setMines(Cell forbiddenCell) {
        for (int i = remainingNOfMines; i > 0;) {
            Random rand = new Random();
            Cell randCell = field.get(rand.nextInt(width)).get(rand.nextInt(height));
            if (!randCell.equals(forbiddenCell) && !randCell.isMined()) {
                randCell.mine();
                i--;

                getNeighbors(randCell).forEach(neighbor -> neighbor.setNOfMinedNeighbors(neighbor.getNOfMinedNeighbors() + 1));
            }
        }
    }

    private void createField() {
        double aspectRatio = windowWidth / windowHeight;
        height = (int) ((width / aspectRatio) * (2.0 / sqrt(3)));

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
                isolatedClosedCells.add(cell);

                group.getChildren().addAll(
                    cell.getHexagon(),
                    cell.getNOfMinedNeighborsLabel(),
                    cell.getActionListenerHexagon()
                );
            }
            field.add(row);
        }

        int nOfCells = width * height;
        if (gameMode == null) {
            gameMode = GameMode.MEDIUM;
        }
        int nOfMines = (int) (nOfCells * gameMode.getProportionOfMines());
        remainingNOfMarks = nOfMines;
        remainingNOfMines = nOfMines;
        Main.setRemainingNOfMarksLabel(remainingNOfMarks);
    }

    private void clearField() {
        firstMove = true;
        field.clear();
        isolatedClosedCells.clear();
        openedBorderCells.clear();
        group.getChildren().clear();
    }
}
