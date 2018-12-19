import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

class Solver {
    private Game game;

    Solver(Game game) {
        this.game = game;
    }

    private class CellsGroup extends HashSet<Cell> {
        private int nOfMines;

        CellsGroup(Cell baseCell) {
            if (baseCell.isOpened()) {
                Set<Cell> neighbors = game.getNeighbors(baseCell);
                Set<Cell> closedNeighbors = new HashSet<>(neighbors).stream()
                        .filter(n -> !n.isOpened() && !n.isMarked()).collect(Collectors.toSet());
                Set<Cell> markedNeighbors = new HashSet<>(neighbors).stream()
                        .filter(Cell::isMarked).collect(Collectors.toSet());

                addAll(closedNeighbors);
                this.nOfMines = baseCell.getNOfMinedNeighbors() - markedNeighbors.size();
            }
        }

        boolean includes(CellsGroup another) {
            return containsAll(another) && nOfMines >= another.nOfMines;
        }

        void subtract(CellsGroup another) {
            removeAll(another);
            nOfMines -= another.nOfMines;
        }

        @Override
        public String toString() {
            return "(" + super.toString() + ": " + nOfMines + ")";
        }
    }

    private Set<CellsGroup> getBorderCellsGroups() {
        Set<CellsGroup> borderCellsGroups = new HashSet<>();
        game.getOpenedBorderCells().forEach(cell -> {
            CellsGroup thisGroup = new CellsGroup(cell);
            if (thisGroup.size() != 0) {
                for (CellsGroup anotherGroup : borderCellsGroups) {
                    if (thisGroup.equals(anotherGroup)) {
                        continue;
                    }
                    if (thisGroup.includes(anotherGroup)) {
                        thisGroup.subtract(anotherGroup);
                    }
                    else if (anotherGroup.includes(thisGroup)) {
                        anotherGroup.subtract(thisGroup);
                    }
                }
                borderCellsGroups.add(thisGroup);
            }
        });
        return borderCellsGroups;
    }

    private void subtractionMethod(Set<CellsGroup> borderCellsGroups, AtomicBoolean changed) {
        for (CellsGroup borderCellsGroup : borderCellsGroups) {
            if (borderCellsGroup.nOfMines == 0) {
                borderCellsGroup.forEach(cell -> game.open(cell));
                changed.set(true);
            }
            else if (borderCellsGroup.nOfMines == borderCellsGroup.size()) {
                borderCellsGroup.forEach(cell -> {
                    if (!cell.isMarked()) {
                        game.mark(cell);
                    }
                });
                changed.set(true);
            }
        }
    }

    private void probabilityMethod(Set<CellsGroup> borderCellsGroups, AtomicBoolean changed) {
        Map<Cell, Double> probabilities = new HashMap<>();
        for (CellsGroup borderCellsGroup : borderCellsGroups) {
            if (borderCellsGroup.size() > 0) {
                borderCellsGroup.forEach(cell -> {
                    double indieProbability = borderCellsGroup.nOfMines / (double) borderCellsGroup.size();
                    double oldProbability = probabilities.get(cell) != null ? probabilities.get(cell) : 0;
                    double newProbability = 1 - (1 - indieProbability) * (1 - oldProbability);
                    probabilities.put(cell, newProbability);
                });
            }
        }

        Cell maxProbabilityCell = Collections.max(
                probabilities.entrySet(),
                Comparator.comparingDouble(Map.Entry::getValue)
        ).getKey();
        double maxProbability = probabilities.get(maxProbabilityCell);
        Cell minProbabilityCell = Collections.min(
                probabilities.entrySet(),
                Comparator.comparingDouble(Map.Entry::getValue)
        ).getKey();
        double minProbability = probabilities.get(minProbabilityCell);

        Set<Cell> isolatedClosedCells = game.getIsolatedClosedCells();
        int nOfIsolatedClosedCells = isolatedClosedCells.size();
        if (nOfIsolatedClosedCells > 0) {
            int nOfMinedBorderCells = 0;
            for (CellsGroup borderCellsGroup : borderCellsGroups) {
                nOfMinedBorderCells += borderCellsGroup.nOfMines;
            }
            double randOpenProbability = ( game.getRemainingNOfMarks() - nOfMinedBorderCells ) / (double) nOfIsolatedClosedCells;
            if (randOpenProbability < minProbability && randOpenProbability < 1 - maxProbability) {
                randMove(isolatedClosedCells, changed);
            }
        }
        if (!changed.get()) {
            if (minProbability < 1 - maxProbability) {
                game.open(minProbabilityCell);
                changed.set(true);
            }
            else {
                game.mark(maxProbabilityCell);
                changed.set(true);
            }
        }
    }

    private void randMove(Set<Cell> isolatedClosedCells, AtomicBoolean changed) {
        int nOfIsolatedClosedCells = isolatedClosedCells.size();
        if (nOfIsolatedClosedCells == game.getRemainingNOfMarks()) {
            isolatedClosedCells.forEach(cell -> game.mark(cell));
        }
        else {
            game.open(new ArrayList<>(isolatedClosedCells).get(new Random().nextInt(nOfIsolatedClosedCells)));
        }
        changed.set(nOfIsolatedClosedCells > 0);
    }

    void solve() {
        AtomicBoolean changed = new AtomicBoolean(false);
        if (game.isFirstMove()) {
            randMove(game.getIsolatedClosedCells(), changed);
        }

        Set<CellsGroup> borderCellsGroups = getBorderCellsGroups();
        subtractionMethod(borderCellsGroups, changed);
        if (!changed.get() && borderCellsGroups.size() > 0 && !game.isBlocked()) {
            probabilityMethod(borderCellsGroups, changed);
        }
        if (!changed.get() && borderCellsGroups.size() == 0 && !game.isBlocked()) {
            randMove(game.getIsolatedClosedCells(), changed);
        }

//        if (changed && !game.isBlocked()) {
//            solve();
//        }
    }
}
