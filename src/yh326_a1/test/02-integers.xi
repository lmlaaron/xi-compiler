a:int;
a=1
a=2147483647
a=2147483648
a=-2147483648
a=-2147483649
a=9223372036854775807
a=-9223372036854775808
a=123+456
a=123-456
a=123*456
a=123/456
a=123%456
a=123*>>123123123123
a=123+456*789/12
a=(123+456)*789
a=3/0 // Not a compile time error
a==3
3==a
31!=a
a<3
a<=3
a>10
a>=10
a='a'
a='啊'
//a=\x61 // Currently don't support hex or oct. It's ok.

// TODO !!!!!!!!!!!!!!!!!!!! The following cases FAIL!!!
a='\x62'+1
a='\x1f' // Non-printable character
a='\x554a' // The '啊' character
a='\xffff'

