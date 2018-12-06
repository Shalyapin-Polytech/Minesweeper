import java.util.*;
import javafx.scene.*;
import static java.lang.StrictMath.sqrt;

class Game {
    private List<List<Cell>> field = new ArrayList<>();
    private Group group = new Group();
    private double windowWidth, windowHeight;
    private int width, height;
    private GameMode gameMode;
    private int remainingNOfMarks, remainingNOfMines = 0;
    private boolean blocked;

    Game(double windowWidth, double windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    List<List<Cell>> getField() {
        return field;
    }

    Group getGroup() {
        return group;
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

    private void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    void restart() {
        clearField();
        createField();
        setMines();
        findMinedNeighbors();
        setBlocked(false);
    }

    void openAll() {
        field.forEach(cells -> cells.forEach(Cell::open));
        setBlocked(true);
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

    void openWithNeighbors(Cell cell) {
        cell.open();
        Set<Cell> neighbors = getNeighbors(cell);
        if (!cell.isMined()) {
            if (cell.getNOfMinedNeighbors() == 0) {
                for (Cell neighbor : neighbors) {
                    if (!neighbor.isOpened()) {
                        openWithNeighbors(neighbor);
                    }
                }
            }
        }
    }

    void mark(Cell cell, boolean marked) {
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

    private void addMouseListener(Cell cell) {
        cell.getActionListenerHexagon().setOnMousePressed(t -> {
            if (!blocked) {
                if (t.isPrimaryButtonDown()) {
                    if (cell.isMarked()) {
                        mark(cell, false);
                    }
                    if (cell.isMined()) {
                        openAll();
                        setBlocked(true);
                        Main.createGameResultStage(false);
                    }
                    openWithNeighbors(cell);
                }
                if (t.isSecondaryButtonDown()) {
                    if (cell.isMarked()) {
                        mark(cell, false);
                    }
                    else if (remainingNOfMarks > 0) {
                        mark(cell, true);
                        if (remainingNOfMines == 0) {
                            setBlocked(true);
                            Main.createGameResultStage(true);
                        }
                    }
                }
                Main.setRemainingNOfMarksLabel(remainingNOfMarks);
            }
        });
    }

    private void findMinedNeighbors() {
        field.forEach(cells -> cells.forEach(cell -> {
            Set<Cell> neighbors = getNeighbors(cell);
            for (Cell neighbor : neighbors) {
                if (neighbor.isMined()) {
                    cell.setNOfMinedNeighbors(cell.getNOfMinedNeighbors() + 1);
                }
            }
        }));
    }

    private void setMines() {
        int nOfCells = width * height;
        if (gameMode == null) {
            gameMode = GameMode.MEDIUM;
        }
        int nOfMines = (int) (nOfCells * gameMode.getProportionOfMines());
        remainingNOfMarks = nOfMines;
        remainingNOfMines = nOfMines;
        Main.setRemainingNOfMarksLabel(remainingNOfMarks);

        for (int i = nOfMines; i > 0;) {
            int randWidth = new Random().nextInt(width);
            int randHeight = new Random().nextInt(height);
            Cell randCell = field.get(randWidth).get(randHeight);
            if (!randCell.isMined()) {
                randCell.mine();
                i--;
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

                group.getChildren().addAll(
                    cell.getHexagon(),
                    cell.getNOfMinedNeighborsLabel(),
                    cell.getActionListenerHexagon()
                );
            }
            field.add(row);
        }
    }

    private void clearField() {
        field.clear();
        group.getChildren().clear();
    }
}
