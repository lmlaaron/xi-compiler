package yh326.util;

public class NumberGetter {
    private static long i = 0;

    public static String uniqueNumber() {
        i += 1;
        return Long.toString(i);
    }
}
