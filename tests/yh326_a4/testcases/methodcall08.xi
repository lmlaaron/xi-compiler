// Call procedure, recursion
foo(a:int) {
if a <= 0 {return} else
foo(a-1)
}
main(argv:int[][]) {foo(20)}
