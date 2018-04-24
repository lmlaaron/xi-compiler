package bsa52_ml2558_yz2369_yh326.util;

public class NumberGetter {
    private static long i = 0;

    public static long uniqueNumber() {
        i+=1;
        return i;
    }

    public static String uniqueNumberStr() {
        i += 1;
        return Long.toString(i);
    }
}
