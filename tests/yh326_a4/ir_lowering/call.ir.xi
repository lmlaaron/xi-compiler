(COMPUNIT
 test
 (FUNC
  a
  (SEQ
   (MOVE (TEMP i) (TEMP _ARG0))
   (MOVE (TEMP j) (TEMP _ARG1))
   (MOVE (TEMP k) (TEMP _ARG2))
   (RETURN (MUL (MUL (TEMP i) (TEMP j))(TEMP k)))))
 (FUNC
  b
  (SEQ
   (MOVE 
    (TEMP x) 
    (CALL 
     (ESEQ (MOVE (TEMP j)(TEMP i)) (TEMP j)) 
     (ESEQ (MOVE (TEMP k)(TEMP j)) (TEMP k)) 
     (ESEQ (MOVE (TEMP l)(TEMP k)) (TEMP l)) 
    )
   )
  )
 )
)
