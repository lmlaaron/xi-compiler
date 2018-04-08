use io
use conv

main(args: int[][]) {
    x: int[] = {1, 2, 3}
    a: int[] = {f(x, 1), f(x, 2), f(x, 3), f(x, 4), f(x, 5)}
    i: int = 0
    while (i < length(a)) {
        println(unparseInt(a[i]))
        i = i + 1
    }
}

f(x: int[], y: int): int {
    print("f() called: ")
    println(unparseInt(y))
    x[0] = x[0] + 1
    return x[0]
}
