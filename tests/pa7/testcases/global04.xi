use io

a: bool[5]

main(args: int[][]) {
    for x in a {
      if (x == true) {
        println("true")
      }
      else {
        println("false")
      }
    }
}