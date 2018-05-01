use io
test():int,bool{return 1,false}
main(argv:int[][]) {
    a:int,b:bool=test()
    if !b println({a+48})
}
