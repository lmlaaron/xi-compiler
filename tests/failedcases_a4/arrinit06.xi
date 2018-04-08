use io
use conv

main(args: int[][]) {
    x: int[] = {1, 2, 3}
    a: int[][] = {{f(x, 1)}, {f(x, 2), f(x, 3)}, {f(x, 4), f(x, 5), f(x, 6)},
        {f(x, 7), f(x, 8), f(x, 9), f(x, 10)}}
    {
        i: int = 0
        while (i < length(a)) {
            j: int = 0
            while (j < length(a[i])) {
                println(unparseInt(a[i][j]))
                j = j + 1
            }
            i = i + 1
        }
    }
}

f(x: int[], y: int): int {
    print("f() called: ")
    println(unparseInt(y))
    x[0] = x[0] + 1
    return x[0]
}
