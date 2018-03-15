use io

sideEffect(arr:int[]) : bool {
    arr[0] = 5
    return true
}

main(argv:int[][]) {
    t:bool = true
    arr:int[] = {0,1,2,3}
    if (t | sideEffect(arr)) {
        print({arr[0]+48})
    }
}