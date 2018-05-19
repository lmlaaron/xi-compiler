import th
import sys
import itertools


OPTIMIZATIONS = ['-O'] # off by default
if "o" in sys.argv:
    sys.argv.remove("o")
    OPTIMIZATIONS = ['-Oreg', '-Ocse', '-Ocf', '-Ocopy', '-Odce']

for xi_file in sys.argv[1:]:
    th.compile_and_run(xi_file, OPTIMIZATIONS)

# for xi_file in sys.argv[1:]:
#     opts = set(['-Oreg', '-Ocse', '-Ocf', '-Ocopy', '-Odce']) # copy and dce have to go with each other
#
#     for i in range(1, len(opts)):
#         for subset in itertools.combinations(opts, i):
#             print("Options : {}".format(list(subset)))
#             th.compile_and_run(xi_file, subset)
