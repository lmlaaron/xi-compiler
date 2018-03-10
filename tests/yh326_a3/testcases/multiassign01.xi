// multiple asignment
test():int,bool{return 1,false}
foo() {
    a:int
    b:bool
    a, b = test()
    _, _ = test()
}
