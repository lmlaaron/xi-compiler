foo() {
    b : bool
    i : int

    i = 1 * 2 * 3
    i = 1 *>> 2 *>> 3
    i = 1 / 2 / 3
    i = 1 % 2 % 3
    i = 1 + 2 + 3
    i = 1 - 2 - 3

    // this would fail due to type rules
    b = 1 < 2 < 3
    b = 1 <= 2 <= 3
    b = 1 > 2 > 3
    b = 1 >= 2 >= 3
    b = 1 == 2 == 3
    b = 1 != 2 != 3

    b = true & false & true
    b = true | false | true
}