import java.util.*;
import javafx.scene.*;
import static java.lang.StrictMath.sqrt;

class Game {
    private List<List<Cell>> field = new ArrayList<>();
    private List<Cell> borderCells = new ArrayList<>();
    private List<CellsGroup<Cell>> borderCellGroups = new ArrayList<>();
    private Group group = new Group();
    private double windowWidth, windowHeight;
    private int width, height;
    private GameMode gameMode;
    private int remainingNOfMarks, remainingNOfMines = 0;
    private boolean blocked;

    //
    private HashMap<String, String> ways = new HashMap<>();
    //

    Game(double windowWidth, double windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
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

    void subtractionMethod() {
        for (Cell cell : borderCells) {
            CellsGroup<Cell> thisGroup = new CellsGroup<>();
            List<Cell> neighbors = getNeighbors(cell);
            for (Cell neighbor : neighbors) {
                if (!neighbor.isOpened() && !neighbor.isMarked()) {
                    thisGroup.add(neighbor);
                }
            }
            thisGroup.setNOfMines(cell.getNOfMinedNeighbors());
            ways.put(thisGroup.toString(), "");

            List<CellsGroup<Cell>> intersections = new ArrayList<>();
            for (CellsGroup<Cell> anotherGroup : borderCellGroups) {
                String thisGroupOld = thisGroup.toString();
                String anotherGroupOld = anotherGroup.toString();
                String anotherGroupOldWay = ways.get(anotherGroup.toString());
                CellsGroup<Cell> intersection = anotherGroup.intersect(thisGroup);
                if (thisGroup.includes(anotherGroup)) {
                    thisGroup.subtract(anotherGroup);
                    ways.put(thisGroup.toString(), thisGroupOld + " - " + anotherGroupOld + " = " + thisGroup.toString());
                }
                else if (anotherGroup.includes(thisGroup)) {
                    anotherGroup.subtract(thisGroup);
                    ways.put(anotherGroup.toString(), anotherGroupOldWay + ";;; " + anotherGroupOld + " - " + thisGroupOld + " = " + anotherGroup.toString());
                }
                else if (intersection.size() > 0) {
                    intersections.add(intersection);
                    anotherGroup.subtract(intersection);
                    thisGroup.subtract(intersection);
                    String res = thisGroupOld + " ⋂ " + anotherGroupOld + " = " + anotherGroup.toString() + ", " + thisGroup.toString() + ", " + intersection.toString();
                    ways.put(anotherGroup.toString(), anotherGroupOldWay + ";;; " + res);
                    ways.put(thisGroup.toString(), res);
                    ways.put(intersection.toString(), res);
                }
            }
            borderCellGroups.add(thisGroup);
            borderCellGroups.addAll(intersections);
        }

        for (CellsGroup<Cell> borderCellGroup : borderCellGroups) {
            if (borderCellGroup.getNOfMines() == 0) {
                borderCellGroup.forEach(cell -> {
                    openWithNeighbors(cell);
                    if (cell.isMined()) {
                        System.out.println(ways.get(borderCellGroup.toString()));
                    }
                });
            }
            else if (borderCellGroup.getNOfMines() == borderCellGroup.size()) {
                borderCellGroup.forEach(cell -> {
                    cell.mark(true);
//                    borderCells.add(cell);
//                    for (Cell neighbor : getNeighbors(cell)) {
//                        neighbor.setNOfMinedNeighbors(cell.getNOfMinedNeighbors() - 1);
//                    }
                    if (!cell.isMined()) {
                        System.out.println(ways.get(borderCellGroup.toString()));
                    }
                });
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

    private void openWithNeighbors(Cell cell) {
        cell.open();
        List<Cell> neighbors = getNeighbors(cell);
        if (!cell.isMined()) {
            if (cell.getNOfMinedNeighbors() == 0) {
                for (Cell neighbor : neighbors) {
                    if (!neighbor.isOpened()) {
                        openWithNeighbors(neighbor);
                    }
                }
            }
            else {
                borderCells.add(cell);
            }
        }
//        borderCells.forEach(borderCell -> borderCell.mark(true));
    }

    private void addMouseListener(Cell cell) {
        cell.getActionListenerHexagon().setOnMousePressed(t -> {
            if (!blocked) {
                if (t.isPrimaryButtonDown()) {
                    if (cell.isMarked()) {
                        cell.mark(false);
                        remainingNOfMarks++;
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
                        cell.mark(false);
                        remainingNOfMarks++;
                        if (cell.isMined()) {
                            remainingNOfMines++;
                        }
                    }
                    else if (remainingNOfMarks > 0) {
                        cell.mark(true);
                        remainingNOfMarks--;
                        if (cell.isMined()) {
                            remainingNOfMines--;
                            if (remainingNOfMines == 0) {
                                setBlocked(true);
                                Main.createGameResultStage(true);
                            }
                        }
                    }
                }
                Main.setRemainingNOfMarksLabel(remainingNOfMarks);
            }
        });
    }

    private void findMinedNeighbors() {
        field.forEach(cells -> cells.forEach(cell -> {
            List<Cell> neighbors = getNeighbors(cell);
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
        borderCells.clear();
        borderCellGroups.clear();
        ways.clear();
        group.getChildren().clear();
    }
}
