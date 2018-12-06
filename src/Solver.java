import java.util.*;

class Solver {
    private Game game;

    Solver(Game game) {
        this.game = game;
    }

    private class CellsGroup<E> extends HashSet<E> {
        private int nOfMines = 0;

        void setNOfMines(int nOfMines) {
            this.nOfMines = nOfMines;
        }

        int getNOfMines() {
            return nOfMines;
        }

        @Override
        public boolean add(E e) {
            return !this.contains(e) && super.add(e);
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

    private Set<Cell> getBorderCells() {
        Set<Cell> borderCells = new HashSet<>();
        game.getField().forEach(row -> row.forEach(cell -> {
            if (cell.isOpened() || cell.isMarked()) {
                for (Cell neighbor : game.getNeighbors(cell)) {
                    if (!neighbor.isOpened() && !neighbor.isMarked()) {
                        borderCells.add(cell);
                        break;
                    }
                }
            }
        }));
        return borderCells;
    }

    void subtractionMethod() {
        Set<CellsGroup<Cell>> borderCellGroups = new HashSet<>();
//        HashMap<String, String> ways = new HashMap<>();

        for (Cell cell : getBorderCells()) {
            CellsGroup<Cell> thisGroup = new CellsGroup<>();
            int nOfMarkedNeighbors = 0;
            Set<Cell> neighbors = game.getNeighbors(cell);
            for (Cell neighbor : neighbors) {
                if (!neighbor.isOpened() && !neighbor.isMarked()) {
                    thisGroup.add(neighbor);
                }
                else if (neighbor.isMarked()) {
                    nOfMarkedNeighbors++;
                }
            }
            thisGroup.setNOfMines(cell.getNOfMinedNeighbors() - nOfMarkedNeighbors);
//            ways.put(thisGroup.toString(), "");

//            Set<CellsGroup<Cell>> intersections = new HashSet<>();
            for (CellsGroup<Cell> anotherGroup : borderCellGroups) {
                if (anotherGroup.size() != 0) {
//                    String thisGroupOld = thisGroup.toString();
//                    String anotherGroupOld = anotherGroup.toString();
//                    String anotherGroupOldWay = ways.get(anotherGroup.toString());
//                    CellsGroup<Cell> intersection = anotherGroup.intersect(thisGroup);

                    if (thisGroup.includes(anotherGroup)) {
                        thisGroup.subtract(anotherGroup);
//                        ways.put(thisGroup.toString(), thisGroupOld + " - " + anotherGroupOld + " = " + thisGroup.toString());
                    }
                    else if (anotherGroup.includes(thisGroup)) {
                        anotherGroup.subtract(thisGroup);
//                        ways.put(anotherGroup.toString(), anotherGroupOldWay + ";;;\n" + anotherGroupOld + " - " + thisGroupOld + " = " + anotherGroup.toString());
                    }
//                    else if (intersection.size() > 0) {
//                        intersections.add(intersection);
//                        anotherGroup.subtract(intersection);
//                        thisGroup.subtract(intersection);
//                        String res = thisGroupOld + " ⋂ " + anotherGroupOld + " = " + anotherGroup.toString() + ", " + thisGroup.toString() + ", " + intersection.toString();
//                        ways.put(anotherGroup.toString(), anotherGroupOldWay + ";;;\n" + res);
//                        ways.put(thisGroup.toString(), res);
//                        ways.put(intersection.toString(), res);
//                    }
                }
            }
            borderCellGroups.add(thisGroup);
//            borderCellGroups.addAll(intersections);
        }

        for (CellsGroup<Cell> borderCellGroup : borderCellGroups) {
            if (borderCellGroup.getNOfMines() == 0) {
                borderCellGroup.forEach(cell -> game.openWithNeighbors(cell));
            }
            else if (borderCellGroup.getNOfMines() == borderCellGroup.size()) {
                borderCellGroup.removeIf(Cell::isMarked);
                borderCellGroup.forEach(cell -> game.mark(cell, true));
                Main.setRemainingNOfMarksLabel(game.getRemainingNOfMarks());
            }
        }
    }
}
