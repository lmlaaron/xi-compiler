use io
test():
int,int,int,int,int,int,int,int,int,int,
int,int,int,int,int,int,int,int,int,int,
int,int,int,int,int,int,int,int,int,int {return
1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30}

main(argv:int[][]) {
    _,_,_,_,_,_,_,_,_,_,_,_,_,_,wtf:int,_,_,_,_,_,_,_,_,_,_,_,_,_,_,_=test()
    println( { wtf / 10 + 48, wtf % 10 + 48 } )
}
