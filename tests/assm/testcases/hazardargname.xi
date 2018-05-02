//use register name as function arguments
use io
frax(rax:int):int {return rax}
frbx(rbx:int):int {return rbx} 
frcx(rcx:int):int {return rcx}
frdx(rdx:int):int {return rdx}
frsp(rsp:int):int {return rsp}
frbp(rbp:int):int {return rbp}
frdi(rdi:int):int {return rdi}
frsi(rsi:int):int {return rsi}
fr8( r8 :int):int {return r8 }
fr9( r9 :int):int {return r9 }
fr10(r10:int):int {return r10}
fr11(r11:int):int {return r11}
fr12(r12:int):int {return r12}
fr13(r13:int):int {return r13}
fr14(r14:int):int {return r14}
fr15(r15:int):int {return r15}

main(argv:int[][]) {
  str:int[16]
  str[0]=frax('0')  
  str[1]=frbx('1')
  str[2]=frcx('2')
  str[3]=frdx('3')
  str[4]=frsp('4')
  str[5]=frbp('5')
  str[6]=frdi('6')
  str[7]=frsi('7')
  str[8]=fr8( '8')
  str[9]=fr9( '9')
  str[10]=fr10('a')
  str[11]=fr11('b')
  str[12]=fr12('c')
  str[13]=fr13('d')
  str[14]=fr14('e')
  str[15]=fr15('f')
  println(str)
}
