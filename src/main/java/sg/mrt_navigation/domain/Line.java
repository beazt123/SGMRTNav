package sg.mrt_navigation.domain;

public enum Line {

    EXIT(-1) {
        @Override
        public String toString() {
            return "EXIT";
        }
    },
    BP(4) {
        @Override
        public String toString() {
            return "BP";
        }
    },
    CC(13) {
        @Override
        public String toString() {
            return "CC";
        }
    },
    CE(13) {
        @Override
        public String toString() {
            return "CE";
        }
    },
    CG(25) {
        @Override
        public String toString() {
            return "CG";
        }
    },
    DT(13) {
        @Override
        public String toString() {
            return "DT";
        }
    },
    EW(25) {
        @Override
        public String toString() {
            return "EW";
        }
    },
    NE(25) {
        @Override
        public String toString() {
            return "NE";
        }
    },
    NS(25) {
        @Override
        public String toString() {
            return "NS";
        }
    },
    PE(4) {
        @Override
        public String toString() {
            return "PE";
        }
    },
    PW(4) {
        @Override
        public String toString() {
            return "PW";
        }
    },
    SE(4) {
        @Override
        public String toString() {
            return "SE";
        }
    },
    SW(4) {
        @Override
        public String toString() {
            return "SW";
        }
    },
    TE(21) {
        @Override
        public String toString() {
            return "TE";
        }
    };

    private int numDoors;
    private double averageTimeBetweenStns;

    private Line(int numDoors, double averageTimeBetweenStns) {
        this.numDoors = numDoors;
        this.averageTimeBetweenStns = averageTimeBetweenStns;
    }

    private Line(int numDoors) {
        this(numDoors, 2);
    }

    public int getNumDoors() {
        return numDoors;
    }

    public double getAverageTimeBetweenStns() {
        return averageTimeBetweenStns;
    }

    @Override
    public abstract String toString();

}
