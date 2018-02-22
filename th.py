# th stands for "test harness"

import subprocess
import os

LEXER_TESTS = "./tests/yh326_a1"
PARSER_TESTS = "./tests/yh326_a2"

# https://stackoverflow.com/questions/287871/print-in-terminal-with-colors
bcolors = {
    'HEADER': '\033[95m',
    'OKBLUE': '\033[94m',
    'OKGREEN': '\033[92m',
    'WARNING': '\033[93m',
    'FAIL': '\033[91m',
    'ENDC': '\033[0m',
    'BOLD': '\033[1m',
    'UNDERLINE': '\033[4m',
}

# Print functionality #
old_print = print
def print_log(stdout):
    if stdout != None and len(stdout) > 0:
        old_print(bcolors['OKGREEN'] + stdout + bcolors['ENDC'])
def print_stdout(stdout):
    if stdout != None and len(stdout) > 0:
        old_print(bcolors['OKBLUE'] + stdout + bcolors['ENDC'])
def print_stderr(stderr):
    if stderr != None and len(stderr) > 0:
        old_print(bcolors['FAIL'] + stderr + bcolors['ENDC'])
print = print_log

# os utilities
def parent_dir(path):
    return os.path.dirname(os.path.normpath(path))

# Runs a shell command and prints stdout, stderr #
def run(cmd, print_results = True, end_on_error = False):
    result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out = result.stdout.decode()
    err = result.stderr.decode()
    if print_results:
        print_stdout(out)
        print_stderr(err)
    fail = (err != None and len(err) > 0) or (result.returncode !=0)
    if (fail and end_on_error):
        print("There was an error:")
        print_stderr(err)
        print("Exiting...")
        exit(1)
    return fail, out, err

# abstract test delegator
def run_test_set(test_dir, grader_function):
    testcase_dir, answer_dir, testcases, answers = load_testcases(test_dir)
    results = []
    for k in testcases.keys():
        # grader function returns (success, reason) tuple
        results.append( (k,) + grader_function(testcases[k], answers[k]) )
    for case, passed, reason in results:
        if passed:
            print_log("Case {} : {} : {}".format(case, "PASS", reason))
        else:
            print_stderr("Case {} : {} : {}".format(case, "FAIL", reason))

# utility function for loading up test cases
def load_testcases(test_dir):
    testcase_dir = os.path.join(test_dir, "testcases")
    answer_dir = os.path.join(test_dir, "answers")

    testcases = {}
    answers = {}
    for f in os.listdir(testcase_dir):
        testcases[f[:2]] = os.path.join(testcase_dir, f)
    for f in os.listdir(answer_dir):
        answers[f[:2]] = os.path.join(answer_dir, f)

    bad_keys = []
    for k in testcases.keys():
        if k not in answers:
            print_stderr("Testcase {} has no corresponding solution!".format(k))
            bad_keys.append(k)
    # TODO: uncomment below!
    for k in bad_keys:
        del testcases[k]

    return testcase_dir, answer_dir, testcases, answers

# grader function returns (success, reason) tuple
def lex_grader(testcase_f, answer_f):
    ret = None
    # run the lexer
    run(['./xic', '--lex', testcase_f], print_results=False)
    # find the .lexed file
    # TODO: assumes .lexed files have been cleaned. this relies on the clean script which doesn't exist yet
    _, lexfn, _ = run(['find', '.', '-name', '*.lexed'], print_results=False)
    lexfn = lexfn.strip()
    if len(lexfn) == 0:
        ret = (False, "couldn't find generated .lexed file")
    else:
        # examine results:
        _, lexed_contents, _ = run(['cat', lexfn], print_results=False)
        _, testcase_contents, _ = run(['cat', answer_f])
        if testcase_contents.find('error') != -1:
            # correct answer is for lexer to detect error
            lexed_lastline = lexed_contents.split['\n'][-1]
            if lexed_lastline.find('error') == -1:
                # fail
                ret = (False, "Last line should have been an error. Instead, found '{}'".format(lexed_lastline))
            else:
                # pass
                ret = (True, '')
        else: # testcase should pass
            if lexed_contents.find('error') >= 0:
                ret = (False, "Lexer detected an error, but shouldn't have!")
            else:
                ret = (True, '')

    run(['rm', lexfn], print_results=False)
    return ret

def remove_whitespace(s):
    return ''.join(s.split())

# grader function returns (success, reason) tuple
def parse_grader(testcase_f, answer_f):
    ret = None
    # run the lexer
    run(['./xic', '--parse', testcase_f], print_results=False)
    # find the .parsed file
    # TODO: assumes .parsed files have been cleaned. this relies on the clean script which doesn't exist yet
    _, lexfn, _ = run(['find', '.', '-maxdepth', '1' ,'-name', '*.parsed'], print_results=False)
    lexfn = lexfn.strip()
    if len(lexfn) == 0:
        ret = (False, "couldn't find generated .parsed file")
    else:
        # examine results:
        _, result_contents, _ = run(['cat', lexfn], print_results=False)
        _, answer_contents, _ = run(['cat', answer_f], print_results=False)
        result_contents = remove_whitespace(result_contents)
        answer_contents = remove_whitespace(answer_contents)
        if result_contents != answer_contents:
            ret = (False, "results {} don't match testcase {} {}".format(result_contents, answer_f, answer_contents))
        else:
            ret = (True, "")

    run(['rm', lexfn], print_results=False)
    return ret





# # runs PA1 tests
# def lex_tests():
#     testcase_dir, answer_dir, testcases, answers = load_testcases(LEXER_TESTS)
#
#     results = [] # elements of type (testcode, result[bool], error[str]) tuple
#
#     for k in testcases.keys():
#         # run the lexer
#         run(['./xic', '--lex', testcases[k]])
#
#         # find the .lexed file
#         _, lexfn, _ = run(['find', testcase_dir, '{}*.lexed'.format(k)], print_results=False)
#         lexfn = lexfn.strip()
#         if len(lexfn) == 0:
#             results.append( (k, False, "couldn't find generated .lexed file") )
#         else:
#             # examine results:
#             _, lexed_contents, _ = run(['cat', lexfn], print_results=False)
#             _, testcase_contents, _ = run(['cat', answers[k]])
#             if testcase_contents.find('error') != -1:
#                 # correct answer is for lexer to detect error
#                 lexed_lastline = lexed_contents.split['\n'][-1]
#                 if lexed_lastline.find('error') == -1:
#                     # fail
#                     results[k] = (k, False, "Last line should have been an error. Instead, found '{}'".format(lexed_lastline))
#                 else:
#                     # pass
#                     results[k] = (k, True, '')
#         run(['rm', lexfn], print_results=False)
#
#     for case, passed, reason in results:
#         if passed:
#             print_log("Case {} : {} : {}".format(case, "PASS", reason))
#         else:
#             print_stderr("Case {} : {} : {}".format(case, "FAIL", reason))





def build():
    print("===CLEANING===")
    run(['./clean'])
    print("===BUILDING===")
    run(['./xic-build'], print_results=False)

    print("===RUNNING LEX TESTS===")
    run_test_set(LEXER_TESTS, lex_grader)
    print("===RUNNING PARSE TESTS===")
    run_test_set(PARSER_TESTS, parse_grader)


if __name__ == "__main__":
    print("Test Harness Begin")
    build()
    print("Test Harness End!")

