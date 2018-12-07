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

        void setNOfMines(int nOfMines) {
            this.nOfMines = nOfMines;
        }

        int getNOfMines() {
            return nOfMines;
        }

        boolean includes(CellsGroup<E> another) {
            return containsAll(another) && nOfMines >= another.nOfMines;
        }

        void subtract(CellsGroup<E> another) {
            removeAll(another);
            nOfMines -= another.nOfMines;
        }

//        CellsGroup<E> intersect(CellsGroup<E> another) {
//            CellsGroup<E> larger, smaller;
//            if (nOfMines > another.nOfMines || nOfMines == another.nOfMines && size() > another.size()) {
//                larger = this;
//                smaller = another;
//            }
//            else {
//                larger = another;
//                smaller = this;
//            }
//            CellsGroup<E> result = new CellsGroup<>();
//            result.addAll(larger);
//            result.retainAll(smaller);
//
//            int resultNOfMines = larger.nOfMines - ( smaller.size() - result.size() );
//            result.setNOfMines(resultNOfMines);
//
//            if (resultNOfMines != smaller.nOfMines) {
//                return new CellsGroup<>();
//            }
//            return result;
//        }

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
        Set<CellsGroup<Cell>> borderCellsGroups = new HashSet<>();
        game.getField().forEach(row -> row.forEach(cell -> {
            CellsGroup<Cell> thisGroup = createCellsGroup(cell);
            if (thisGroup != null) {
//                Set<CellsGroup<Cell>> intersections = new HashSet<>();
                for (CellsGroup<Cell> anotherGroup : borderCellsGroups) {
//                    CellsGroup<Cell> intersection = anotherGroup.intersect(thisGroup);
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
        }));

        for (CellsGroup<Cell> borderCellsGroup : borderCellsGroups) {
            if (borderCellsGroup.getNOfMines() == 0) {
                borderCellsGroup.forEach(cell -> game.openWithNeighbors(cell));
            }
            else if (borderCellsGroup.getNOfMines() == borderCellsGroup.size()) {
                borderCellsGroup.removeIf(Cell::isMarked);
                borderCellsGroup.forEach(cell -> game.mark(cell, true));
                Main.setRemainingNOfMarksLabel(game.getRemainingNOfMarks());
            }
        }
    }
}
