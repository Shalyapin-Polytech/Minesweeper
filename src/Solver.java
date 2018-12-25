import java.util.*;
import java.util.stream.Collectors;

class Solver {
    private Game game;
    private Set<CellsGroup> borderCellsGroups;
    private boolean changed;

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

        int getNOfMines() {
            return nOfMines;
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
                borderCellsGroups.forEach(anotherGroup -> {
                    if (thisGroup.equals(anotherGroup)) {
                        return;
                    }
                    if (thisGroup.includes(anotherGroup)) {
                        thisGroup.subtract(anotherGroup);
                    }
                    else if (anotherGroup.includes(thisGroup)) {
                        anotherGroup.subtract(thisGroup);
                    }
                });
                borderCellsGroups.add(thisGroup);
            }
        });
        return borderCellsGroups;
    }

    private void subtractionMethod() {
        borderCellsGroups.forEach(borderCellsGroup -> {
            if (borderCellsGroup.nOfMines == 0) {
                borderCellsGroup.forEach(cell -> game.open(cell));
                changed = true;
            }
            else if (borderCellsGroup.nOfMines == borderCellsGroup.size()) {
                borderCellsGroup.forEach(cell -> {
                    if (!cell.isMarked()) {
                        game.mark(cell);
                    }
                });
                changed = true;
            }
        });
        borderCellsGroups = getBorderCellsGroups();
    }

    private void probabilityMethod() {
        Map<Cell, Double> probabilities = new HashMap<>();
        borderCellsGroups.forEach(borderCellsGroup -> {
            if (borderCellsGroup.size() > 0) {
                borderCellsGroup.forEach(cell -> {
                    double indieProbability = borderCellsGroup.nOfMines / (double) borderCellsGroup.size();
                    double oldProbability = probabilities.get(cell) != null ? probabilities.get(cell) : 0;
                    double newProbability = 1 - (1 - indieProbability) * (1 - oldProbability);
                    probabilities.put(cell, newProbability);
                });
            }
        });

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

        int nOfIsolatedClosedCells = game.getIsolatedClosedCells().size();
        if (nOfIsolatedClosedCells > 0) {
            int nOfMinedBorderCells = borderCellsGroups.stream()
                    .map(CellsGroup::getNOfMines)
                    .reduce(0, Integer::sum);
            double randOpenProbability = ( game.getRemainingNOfMarks() - nOfMinedBorderCells ) / (double) nOfIsolatedClosedCells;
            if (randOpenProbability < minProbability && randOpenProbability < 1 - maxProbability) {
                randMove();
            }
        }
        if (!changed) {
            if (minProbability < 1 - maxProbability) {
                game.open(minProbabilityCell);
                changed = true;
            }
            else {
                game.mark(maxProbabilityCell);
                changed = true;
            }
            borderCellsGroups = getBorderCellsGroups();
        }
    }

    private void randMove() {
        Set<Cell> isolatedClosedCells = game.getIsolatedClosedCells();
        int nOfIsolatedClosedCells = isolatedClosedCells.size();
        if (borderCellsGroups.size() == 0 && nOfIsolatedClosedCells == game.getRemainingNOfMarks()) {
            isolatedClosedCells.forEach(cell -> game.mark(cell));
        }
        else {
            game.open(new ArrayList<>(isolatedClosedCells).get(new Random().nextInt(nOfIsolatedClosedCells)));
        }
        changed = nOfIsolatedClosedCells > 0;
        borderCellsGroups = getBorderCellsGroups();
    }

    void solve() {
        changed = false;
        borderCellsGroups = getBorderCellsGroups();

        if (game.isFirstMove()) {
            randMove();
        }
        subtractionMethod();
        if (!changed && borderCellsGroups.size() > 0 && !game.isBlocked()) {
            probabilityMethod();
        }
        if (!changed && borderCellsGroups.size() == 0 && !game.isBlocked()) {
            randMove();
        }

//        if (changed && !game.isBlocked()) {
//            solve();
//        }
    }
}
