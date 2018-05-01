use io
main(argv:int[][]) {
   a:int[3][2][];
   a[0] = {"new", "array"}
   a[1][1] = "new string"
   println(a[0][0])
   println(a[1][1])
}
