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

    void solve() {
        boolean changed = false;
        if (game.isFirstMove()) {
            game.open(game.getRandCell());
            changed = true;
        }

        Set<CellsGroup<Cell>> borderCellsGroups = new HashSet<>();
        game.forEachCell(cell -> {
            CellsGroup<Cell> thisGroup = createCellsGroup(cell);
            if (thisGroup != null) {
                for (CellsGroup<Cell> anotherGroup : borderCellsGroups) {
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

        for (CellsGroup<Cell> borderCellsGroup : borderCellsGroups) {
            if (borderCellsGroup.nOfMines == 0) {
                borderCellsGroup.forEach(cell -> game.open(cell));
                changed = true;
            }
            else if (borderCellsGroup.nOfMines == borderCellsGroup.size()) {
                ///
                borderCellsGroup.removeIf(Cell::isMarked);
                borderCellsGroup.forEach(cell -> game.mark(cell));
                changed = true;
            }
        }

        if (!changed && borderCellsGroups.size() > 0 && !game.isBlocked()) {
            Map<Cell, Double> probabilities = new HashMap<>();
            for (CellsGroup<Cell> borderCellsGroup : borderCellsGroups) {
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
            Cell minProbabilityCell = Collections.min(
                    probabilities.entrySet(),
                    Comparator.comparingDouble(Map.Entry::getValue)
            ).getKey();

            if (probabilities.get(minProbabilityCell)  < 1 - probabilities.get(maxProbabilityCell)) {
                game.open(minProbabilityCell);
                changed = true;
            }
            else {
                game.mark(maxProbabilityCell);
                changed = true;
            }
        }

        if (!changed && borderCellsGroups.size() == 0 && !game.isBlocked()) {
            List<Cell> closedCells = new ArrayList<>();
            game.forEachCell(cell -> {
                if (!cell.isOpened() && !cell.isMarked()) {
                    closedCells.add(cell);
                }
            });
            game.open(closedCells.get(new Random().nextInt(closedCells.size())));
        }

//        if (changed && !game.isBlocked()) {
//            solve();
//        }
    }
}
