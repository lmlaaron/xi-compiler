// Function returns wrong array
b():bool {return true}
foo():bool[], bool, int[][] {return {true, b()}, !b(), {"lol", {1,2}}}