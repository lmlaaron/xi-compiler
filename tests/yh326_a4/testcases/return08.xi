// Function returns wrong array
b():bool {return true}
main(argv:int[][]):bool[], bool, int[][] {return {true, b()}, !b(), {"lol", {1,2}}}