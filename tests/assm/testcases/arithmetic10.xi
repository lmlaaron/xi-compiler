use io
main(argv:int[][]) {
  two_to_62nd : int = 4611686018427387904
  two_to_4th : int = 16

  // two to 66th power with the bottom 64 bits (2^64) removed is just 2^2

  if (two_to_62nd *>> two_to_4th == 4) {
    println("success!")
  }
}