import th
import sys

for xi_file in sys.argv[1:]:
    th.compile_and_run(xi_file)