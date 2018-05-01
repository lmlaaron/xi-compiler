//use register name as values
use io

main(argv:int[][]) {
  rax:int ='0' 
  rbx:int ='1' 
  rcx:int ='2' 
  rdx:int ='3' 
  rsp:int ='4'
  rbp:int ='5'
  rdi:int ='6'
  rsi:int ='7'

  r8:int = '8' 
  r9:int = '9'
  r10:int ='a'
  r11:int ='b'
  r12:int ='c'
  r13:int ='d'
  r14:int ='e'
  r15:int ='f'
  str:int[16]
  str[0]=(rax)  
  str[1]=(rbx)
  str[2]=(rcx)
  str[3]=(rdx)
  str[4]=(rsp)
  str[5]=(rbp)
  str[6]=(rdi)
  str[7]=(rsi)
  str[8]=(r8)
  str[9]=(r9)
  str[10]=(r10)
  str[11]=(r11)
  str[12]=(r12)
  str[13]=(r13)
  str[14]=(r14)
  str[15]=(r15)
  println(str)
}
