enum GameMode {
    EASY {
        @Override
        public double getProportionOfMines() {
            return 0.1;
        }
    },
    MEDIUM {
        @Override
        public double getProportionOfMines() {
            return 0.15;
        }
    },
    HARD {
        @Override
        public double getProportionOfMines() {
            return 0.18;
        }
    },
    IMPOSSIBLE {
        @Override
        public double getProportionOfMines() {
            return 0.3;
        }
    };

    public abstract double getProportionOfMines();
}