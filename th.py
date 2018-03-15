# th stands for "test harness"

import subprocess
import os

# Parent directories for each test category
LEXER_TESTS = "./tests/yh326_a1"
PARSER_TESTS = "./tests/yh326_a2"
TYPECHECKER_TESTS = "./tests/yh326_a3"
IRRUN_TESTS = "./tests/yh326_a4"

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





# returns contents of file matching regex, or None if no matches are found
def file_contents_in_dir(fname, dir='.', split_lines=False):
    file = find_file_in_dir(fname, dir)
    if file == None:
        return None
    if split_lines:
        return list(open(file))
    else:
        return ''.join(open(file))

# os utilities
def parent_dir(path):
    return os.path.dirname(os.path.normpath(path))
def find_file_in_dir(fname, dir='.'):
    for file in filter(os.path.isfile, os.listdir(dir)):
        file = str(file)
        if file == fname:
            return os.path.join(dir, file)

# Runs a shell command and prints stdout, stderr #
def run_shell(cmd, print_results = True, end_on_error = False):
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

def rm_extension(path):
    return path[:path.index(".")] if "." in path else path

def rm_path(path):
    return os.path.split(path)[1]


# misc. utilities
def remove_whitespace(s):
    return ''.join(s.split())
def last_line(str):
    return str.split('\n')[-1]


# utility function for loading up test cases
#
# returns the parent directories of the testcase and answer files.
# For each, a mapping of filename (without extension) to that file's
# path is also returned. These maps are used to match corresponding
# testcases and solutions
def load_testcases(test_dir):
    testcase_dir = os.path.join(test_dir, "testcases")
    answer_dir = os.path.join(test_dir, "answers")

    testcases = {}
    answers = {}
    for f in os.listdir(testcase_dir):
        if f.endswith(".xi"):
            fullpath = os.path.join(testcase_dir, f)
            testcases[rm_extension(f)] = fullpath
    for f in os.listdir(answer_dir):
        answers[rm_extension(f)] = os.path.join(answer_dir, f)

    bad_keys = []
    for k in testcases.keys():
        if k not in answers:
            print_stderr("Testcase {} has no corresponding solution!".format(k))
            bad_keys.append(k)
    for k in bad_keys:
        del testcases[k]

    return testcase_dir, answer_dir, testcases, answers




# abstract test delegator. given a parent directory and a grader function,
# runs the grader function on each testcase-solution pair and prints the results
def run_test_set(test_dir, grader_function):
    testcase_dir, answer_dir, testcases, answers = load_testcases(test_dir)
    results = []
    for k in testcases.keys():
        # grader function returns (success, reason) tuple
        results.append( (k,) + grader_function(testcases[k], answers[k]) )
    correct = 0
    for case, passed, reason in results:
        if passed:
            print_log("Case {} : {} : {}".format(case, "PASS", reason))
            correct += 1
        else:
            print_stderr("Case {} : {} : {}".format(case, "FAIL", reason))

    print_log("{} out of {} tests passed".format(correct, len(results)))


# GRADER FUNCTIONS
# all grader functions return (success:bool, reason:str) tuple

# parse tests pass only if the output file's contents (besides whitespace)
# completely match the given solution
def parse_grader(testcase_f, answer_f):
    run_shell(['./xic', '--parse', testcase_f], print_results=False)
    result_file = rm_extension(rm_path(testcase_f)) + ".parsed"
    try:
        result_contents = ''.join(open(result_file))
    except FileNotFoundError:
        return (False, "couldn't find generated .parsed file")
    run_shell(['rm', result_file], print_results=False)
    answer_contents = ''.join(open(answer_f))

    result_contents = remove_whitespace(result_contents)
    answer_contents = remove_whitespace(answer_contents)
    if result_contents == answer_contents:
        return (True, "")
    else:
        return (False, "\nresults -> {}\ncorrect -> {}".format(result_contents, answer_contents))

# lex tests pass if both the output and the solution found errors,
# or if neither did. More specific analysis isn't attempted
def lex_grader(testcase_f, answer_f):
    run_shell(['./xic', '--lex', testcase_f], print_results=False)
    result_file = rm_extension(rm_path(testcase_f)) + ".lexed"
    try:
        result_contents = ''.join(open(result_file))
    except FileNotFoundError:
        return (False, "couldn't find generated .lexed file")

    run_shell(['rm', result_file], print_results=False)

    answer_contents = ''.join(open(answer_f))

    result_error = 'error' in last_line(result_contents)
    answer_error = 'error' in last_line(answer_contents)
    if result_error == answer_error:
        return (True, '')
    else:
        if (answer_error):
            message = "Result should have been an error, but wasn't"
        else:
            message = "Result shouldn't have been an error, but was"
        return (False, message)

# similar to lex_grader, this grader just tests for the presence of
# 'valid xi program'
def typecheck_grader(testcase_f, answer_f):
    run_shell(['./xic', '--typecheck', testcase_f], print_results=False)
    result_file = rm_extension(rm_path(testcase_f)) + ".typed"
    try:
        result_contents = ''.join(open(result_file))
    except FileNotFoundError:
        return (False, "couldn't find generated .typed file")

    run_shell(['rm', result_file], print_results=False)

    answer_contents = ''.join(open(answer_f))

    answer_valid = 'valid xi program' in answer_contents.lower()
    result_valid = 'valid xi program' in answer_contents.lower()

    if answer_valid == result_valid:
        return (True, '')
    else:
        if answer_valid:
            message = "Result should have been valid, but wasn't"
        else:
            message = "Result shouldn't hae been valid, but was"
        return (False, message)

def irrun_grader(testcase_f, answer_f):
    # TODO: libpath flag must be used so that io works, but that may not be true in the future
    _, stdout, _ = run_shell(['./xic', '-libpath', 'lib/xi', '--irrun', testcase_f], print_results=False)

    answer_contents = ''.join(open(answer_f))

    if answer_contents == stdout:
        return (True, '')
    else:
        return (False, "stdout doesnt match answer file:\n\tExpected:'{}'\n\t   Found:'{}'".format(answer_contents, stdout))



def build():
    print("===CLEANING===")
    run_shell(['./clean'])
    print("===BUILDING===")
    run_shell(['./xic-build'], print_results=False)


if __name__ == "__main__":
    print("Test Harness Begin")

    build()

    # print("===RUNNING LEX TESTS===")
    # run_test_set(LEXER_TESTS, lex_grader)
    # print("===RUNNING PARSE TESTS===")
    # run_test_set(PARSER_TESTS, parse_grader)
    # print("====RUNNING TYPECHECK TESTS===")
    # run_test_set(TYPECHECKER_TESTS, typecheck_grader)
    print("====RUNNING IRRUN TESTS====")
    run_test_set(IRRUN_TESTS, irrun_grader)

    print("Test Harness End!")

