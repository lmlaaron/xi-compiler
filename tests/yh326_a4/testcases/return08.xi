// Function returns wrong array
b():bool {return true}
foo():bool[], bool, int[][] {return {true, b()}, !b(), {"lol", {1,2}}}
main(argv:int[][]) {
    _, _, _ = foo()
}
