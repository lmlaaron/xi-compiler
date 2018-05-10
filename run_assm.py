import th
import sys
import itertools


for xi_file in sys.argv[1:]:
    th.compile_and_run(xi_file, ['-Oreg', '-Ocse', '-Ocf', '-Ocopy -Odce'])

# for xi_file in sys.argv[1:]:
#     opts = set(['-Oreg', '-Ocse', '-Ocf', '-Ocopy -Odce']) # copy and dce have to go with each other
#
#     for i in range(1, len(opts)):
#         for subset in itertools.combinations(opts, i):
#             print("Options : {}".format(list(subset)))
#             th.compile_and_run(xi_file, subset)
