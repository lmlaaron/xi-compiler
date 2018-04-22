package bsa52_ml2558_yz2369_yh326.util;

public class Tuple<T1, T2> {
    public final T1 t1;
    public final T2 t2;

    public Tuple(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "(" + t1 + ", " + t2 + ")";
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Tuple) {
            Tuple<?, ?> otherTuple = (Tuple<?, ?>) other;
            if (otherTuple.t1.equals(t1) && otherTuple.t2.equals(t2)) {
                return true;
            } else return false;
        } else return false;
    }
}
