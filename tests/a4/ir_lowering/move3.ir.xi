(COMPUNIT
 test
 (FUNC
  a
  (MOVE
   (MEM (ADD (TEMP _array_3_69) (CONST 0)))
   (ESEQ
    (SEQ
     (MOVE (TEMP _array_3_70)
      (ADD (CALL (NAME _xi_alloc) (CONST 32)) (CONST 8)))
     (MOVE (MEM (ADD (TEMP _array_3_70) (CONST -8))) (CONST 3))
     (MOVE (MEM (ADD (TEMP _array_3_70) (CONST 0))) (CONST 108))
     (MOVE (MEM (ADD (TEMP _array_3_70) (CONST 8))) (CONST 111))
     (MOVE (MEM (ADD (TEMP _array_3_70) (CONST 16))) (CONST 108)))
    (TEMP _array_3_70)))
 )
)
