(COMPUNIT
 test
 (FUNC
  a
  (SEQ
   (MOVE
    (TEMP i)
    (MUL
     (ESEQ (MOVE (TEMP i)(CONST 4))(TEMP i))
     (ESEQ (MOVE (TEMP i)(CONST 5))(TEMP j))
    )
   )
  )
 )
)
