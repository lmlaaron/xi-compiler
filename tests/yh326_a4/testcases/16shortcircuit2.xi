use io

sideEffect(arr:int[]) : bool {
    arr[0] = 5
    return true
}

main(argv:int[][]) {
    f:bool = false
    arr:int[] = {0,1,2,3}
    if (f & sideEffect(arr)) {
        print("false")
    }
    print({arr[0]+48})
}