// check while statement return type
// the following should be type error according to the rule
// since while statement is unit type
foo(): int {
    while true {
       return 1
    }
}


