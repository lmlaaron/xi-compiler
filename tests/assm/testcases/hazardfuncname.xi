//use register name as values
use io
rax():int {return '0'}
rbx():int {return '1'} 
rcx():int {return '2'}
rdx():int {return '3'}
rsp():int {return '4'}
rbp():int {return '5'}
rdi():int {return '6'}
rsi():int {return '7'}
r8() :int {return '8'}
r9() :int {return '9'}
r10():int {return 'a'}
r11():int {return 'b'}
r12():int {return 'c'}
r13():int {return 'd'}
r14():int {return 'e'}
r15():int {return 'f'}

main(argv:int[][]) {
  str:int[16]
  str[0]=rax()  
  str[1]=rbx()
  str[2]=rcx()
  str[3]=rdx()
  str[4]=rsp()
  str[5]=rbp()
  str[6]=rdi()
  str[7]=rsi()
  str[8]=r8()
  str[9]=r9()
  str[10]=r10()
  str[11]=r11()
  str[12]=r12()
  str[13]=r13()
  str[14]=r14()
  str[15]=r15()
  println(str)
}
