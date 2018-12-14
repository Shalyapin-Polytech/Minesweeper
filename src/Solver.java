import java.util.*;
import java.util.stream.Collectors;

class Solver {
    private Game game;

    Solver(Game game) {
        this.game = game;
    }

    private class CellsGroup<E> extends HashSet<E> {
        private int nOfMines;

        CellsGroup(Set<E> cells, int nOfMines) {
            addAll(cells);
            this.nOfMines = nOfMines;
        }

        boolean includes(CellsGroup<E> another) {
            return containsAll(another) && nOfMines >= another.nOfMines;
        }

        boolean intersects(CellsGroup<E> another) {
            return new HashSet<>(another).stream()
                    .filter(this::contains).collect(Collectors.toSet()).size() > 0;
        }

        void subtract(CellsGroup<E> another) {
            removeAll(another);
            nOfMines -= another.nOfMines;
        }

        @Override
        public String toString() {
            return "(" + super.toString() + ": " + nOfMines + ")";
        }
    }

    private CellsGroup<Cell> createCellsGroup(Cell cell) {
        if (cell.isOpened()) {
            Set<Cell> neighbors = game.getNeighbors(cell);
            Set<Cell> closedNeighbors = new HashSet<>(neighbors).stream()
                    .filter(n -> !n.isOpened() && !n.isMarked()).collect(Collectors.toSet());
            Set<Cell> markedNeighbors = new HashSet<>(neighbors).stream()
                    .filter(Cell::isMarked).collect(Collectors.toSet());
            if (closedNeighbors.size() != 0) {
                return new CellsGroup<>(closedNeighbors, cell.getNOfMinedNeighbors() - markedNeighbors.size());
            }
        }
        return null;
    }

    void subtractionMethod() {
        if (game.isFirstMove()) {
            game.open(game.getRandCell());
        }

        Set<CellsGroup<Cell>> borderCellsGroups = new HashSet<>();
        game.forEachCell(cell -> {
            CellsGroup<Cell> thisGroup = createCellsGroup(cell);
            if (thisGroup != null) {
//                Set<CellsGroup<Cell>> intersections = new HashSet<>();
                for (CellsGroup<Cell> anotherGroup : borderCellsGroups) {
//                    CellsGroup<Cell> intersection = anotherGroup.intersects(thisGroup);
                    if (thisGroup.equals(anotherGroup)) {
                        continue;
                    }
                    if (thisGroup.includes(anotherGroup)) {
                        thisGroup.subtract(anotherGroup);
                    }
                    else if (anotherGroup.includes(thisGroup)) {
                        anotherGroup.subtract(thisGroup);
                    }
//                    else if (intersection.size() > 0) {
//                        intersections.add(intersection);
//                        anotherGroup.subtract(intersection);
//                        thisGroup.subtract(intersection);
//                    }
                }
                borderCellsGroups.add(thisGroup);
//                borderCellGroups.addAll(intersections);
            }
        });

        for (CellsGroup<Cell> borderCellsGroup : borderCellsGroups) {
            if (borderCellsGroup.nOfMines == 0) {
                borderCellsGroup.forEach(cell -> game.open(cell));
            }
            else if (borderCellsGroup.nOfMines == borderCellsGroup.size()) {
                borderCellsGroup.removeIf(Cell::isMarked);
                borderCellsGroup.forEach(cell -> game.mark(cell));
                Main.setRemainingNOfMarksLabel(game.getRemainingNOfMarks());
            }
        }
    }
}
