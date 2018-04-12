// procedure multiple args
// to test the calling convention
use io
use conv

printInts(a1:int, a2:int, a3:int, a4:int, a5:int) {
print(unparseInt(a1))
print(unparseInt(a2))
print(unparseInt(a3))
print(unparseInt(a4))
println(unparseInt(a5))
}

g0(){}
g1():int {return 1}
g2():int, int {return 1, 2}
g3():int, int, int {return 1, 2, 3}
g4():int, int, int, int {return 1,2,3,4}
g5():int, int, int, int, int {return 1, 2, 3, 4, 5}
g51(a1:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g52(a1:int, a2: int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g53(a1:int, a2: int, a3:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g54(a1:int, a2: int, a3:int, a4:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g55(a1:int, a2: int, a3:int, a4:int, a5:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g56(a1:int, a2: int, a3:int, a4:int, a5:int, a6:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g57(a1:int, a2: int, a3:int, a4:int, a5:int, a6:int, a7:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
g58(a1:int, a2: int, a3:int, a4:int, a5:int, a6:int, a7:int, a8:int): int, int, int, int, int {return 1, 2, 3, 4, 5}
main(argv:int[][]) {
   g0()
   g1r1:int=g1()
   printInts(g1r1,0,0,0,0) 
   g2r1:int ,g2r2:int = g2()
   printInts(g2r1, g2r2,0,0,0) 
   g3r1:int,g3r2:int,g3r3:int=g3()
   printInts(g3r1, g3r2, g3r3,0,0) 
   g4r1:int,g4r2:int,g4r3:int,g4r4:int=g4()
   printInts(g4r1, g4r2, g4r3, g4r4, 0) 
   g5r1:int,g5r2:int,g5r3:int,g5r4:int,g5r5:int=g5()
   printInts(g5r1, g5r2, g5r3, g5r4, g5r5) 
   g51r1:int,g51r2:int,g51r3:int,g51r4:int,g51r5:int=g51(1)
   printInts(g51r1, g51r2, g51r3, g51r4, g51r5) 
   g52r1:int,g52r2:int,g52r3:int,g52r4:int,g52r5:int=g52(1,1)
   printInts(g52r1, g52r2, g52r3, g52r4, g52r5) 
   g53r1:int,g53r2:int,g53r3:int,g53r4:int,g53r5:int=g53(1,1,1)
   printInts(g53r1, g53r2, g53r3, g53r4, g53r5) 
   g54r1:int,g54r2:int,g54r3:int,g54r4:int,g54r5:int=g54(1,1,1,1)
   printInts(g54r1, g54r2, g54r3, g54r4, g54r5) 
   g55r1:int,g55r2:int,g55r3:int,g55r4:int,g55r5:int=g55(1,1,1,1,1)
   printInts(g55r1, g55r2, g55r3, g55r4, g55r5) 
   g56r1:int,g56r2:int,g56r3:int,g56r4:int,g56r5:int=g56(1,1,1,1,1,1)
   printInts(g56r1, g56r2, g56r3, g56r4, g56r5)

   g57r1:int,g57r2:int,g57r3:int,g57r4:int,g57r5:int=g57(1,1,1,1,1,1,1)
   printInts(g57r1, g57r2, g57r3, g57r4, g57r5) 
   g58r1:int,g58r2:int,g58r3:int,g58r4:int,g58r5:int=g58(1,1,1,1,1,1,1,1)
   printInts(g58r1, g58r2, g58r3, g58r4, g58r5) 
}
