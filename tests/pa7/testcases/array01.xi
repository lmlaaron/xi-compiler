use io

// comparing arrays to null should work
main(args: int[][]) {
    a1: int[] = {0, 1, 2, 3, 4}
    b1: bool[] = {true, false, true}
    a2: int[6]
    if (a1 == null) {
      println("a1 is null")
    }
    if (a1 != null) {
      println("a1 is not null")
    }
    if (b1 == null) {
      println("b1 is null")
    }
    if (b1 != null) {
      println("b1 is not null")
    }
    if (a2 == null) {
      println("a2 is null")
    }
    if (a2 != null) {
      println("a2 is not null")
    }
    if (a2[0] == null) {
      println("a2[0] is null")
    }
    if (a2[0] != null) {
      println("a2[0] is not null")
    }
} 
