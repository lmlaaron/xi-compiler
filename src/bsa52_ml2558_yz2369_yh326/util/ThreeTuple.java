package bsa52_ml2558_yz2369_yh326.util;

public class ThreeTuple<T1, T2, T3> {
    public T1 t1;
    public T2 t2;
    public T3 t3;

    public ThreeTuple(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    @Override
    public String toString() {
        return "(" + t1 + ", " + t2 +  ", " + t3 + ")";
    }
}
