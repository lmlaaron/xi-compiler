# th stands for "test harness"

import subprocess
import os
import sys

# Parent directories for each test category
LEXER_TESTS = "./tests/lex"
PARSER_TESTS = "./tests/parse"
TYPECHECKER_TESTS = "./tests/typecheck"
IRRUN_TESTS = "./tests/irrun"
ASSM_TESTS = "./tests/assm"
PA7_TESTS = "./tests/pa7"
PA7_NEGATIVE_TESTS = "./tests/pa7_shouldfail"

LIB_PATH = "./runtime/include"

silent = False

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
#print = print_log





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
    fail = (err != None and len(err) > 0) or (result.returncode != 0)
    if (fail and end_on_error):
        print("There was an error:")
        print_stderr(err)
        print("Exiting...")
        exit(1)
    return fail, out, err

def rm_extension(path):
    while (path.rfind('/') < path.rfind('.')):
        path = path[0:path.rfind('.')]
    return path
    # return path[:path.rindex(".")] if "." in path else path

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
            if not silent:
                print_stderr("Testcase {} has no corresponding solution!".format(k))
            bad_keys.append(k)
    for k in bad_keys:
        del testcases[k]

    return testcase_dir, answer_dir, testcases, answers




# abstract test delegator. given a parent directory and a grader function,
# runs the grader function on each testcase-solution pair and prints the results
def run_test_set(test_dir, grader_function):
    testcase_dir, answer_dir, testcases, answers = load_testcases(test_dir)

    # testcases = dict(list(testcases.items())[:5]) # TODO: REMOVE

    results = []
    for k in testcases.keys():
        # grader function returns (success, reason) tuple
        # print("Running {}".format(k)) # TODO: REMOVE
        results.append( (k,) + grader_function(testcases[k], answers[k]) )
    results = sorted(results)
    correct = 0
    for case, passed, reason in results:
        if passed:
            # UNCOMMENT TO PRINT OUT PASSED TESTCASES
            # print_log("Case {} : {} : {}".format(case, "PASS", reason))
            correct += 1
        else:
            if not silent:
                print_stderr("Case {} : {} : {}".format(case, "FAIL", reason))

    print_log("{} out of {} tests passed".format(correct, len(results)))


# GRADER FUNCTIONS
# all grader functions return (success:bool, reason:str) tuple

# parse tests pass only if the output file's contents (besides whitespace)
# completely match the given solution
def parse_grader(testcase_f, answer_f):
    run_shell(['./xic', '-O','--parse', testcase_f], print_results=False)
    result_file = rm_extension(testcase_f) + ".parsed"
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
    result_file = rm_extension(testcase_f) + ".lexed"
    try:
        result_contents = ''.join(open(result_file))
    except FileNotFoundError:
        print("couldnt find resulting file " + result_file + " " + testcase_f)
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
    result_file = rm_extension(testcase_f) + ".typed"
    try:
        result_contents = ''.join(open(result_file))
    except FileNotFoundError:
        return (False, "couldn't find generated .typed file" + " " + result_file)

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
            message = "Result shouldn't have been valid, but was"
        return (False, message)

def irrun_grader(testcase_f, answer_f):
    # TODO: libpath flag must be used so that io works, but that may not be true in the future
    _, stdout, _ = run_shell(['./xic', '-libpath', LIB_PATH, '--irrun', testcase_f], print_results=False)

    answer_contents = ''.join(open(answer_f))

    return grade_by_matching_output(stdout, answer_contents)

def grade_by_matching_output(output, correct_output):
    if output == correct_output:
        return (True, '')
    else:
        return (False, "stdout doesnt match answer file:\n\tExpected:'{}'\n\t   Found:'{}'".format(correct_output, output))

def assm_grader(testcase_f, answer_f):
    assembly_f = testcase_f.rsplit('.', maxsplit=1)[0] + '.s'

    run_shell(['./xic', '-O', '-libpath', LIB_PATH, testcase_f], print_results=False)
    if not os.path.isfile(assembly_f):
        return (False, "Couldnt find generated assembly file")
    run_shell(['runtime/linkxi.sh', assembly_f, '-o', 'xi_executable'], print_results=False)
    if not os.path.isfile('xi_executable'):
        return (False, "Couldn't find generated executable")
    _, stdout, _ = run_shell(['./xi_executable'], print_results=False)

    os.remove('./xi_executable')
    os.remove(assembly_f)

    answer_contents = ''.join(open(answer_f))
    return grade_by_matching_output(stdout, answer_contents)

def negative_assm_grader(testcase_f, answer_f):
    return negate(assm_grader(testcase_f, answer_f))

def optimization_grader(testcase_f, answer_f):
    assembly_f = testcase_f.rsplit('.', maxsplit=1)[0] + '.s'

    run_shell(['./xic', '-libpath', LIB_PATH, testcase_f], print_results=False)
    if not os.path.isfile(assembly_f):
        return (False, "Couldnt find generated assembly file")
    run_shell(['runtime/linkxi.sh', assembly_f, '-o', 'xi_executable'], print_results=False)
    if not os.path.isfile('xi_executable'):
        return (False, "Couldn't find generated executable")
    _, stdout, _ = run_shell(['./xi_executable'], print_results=False)

    os.remove('./xi_executable')
    os.remove(assembly_f)

    answer_contents = ''.join(open(answer_f))
    return grade_by_matching_output(stdout, answer_contents)

def negate(result):
    return (not result[0], result[1],)


def negative_optimization_grader(testcase_f, answer_f):
    result = optimization_grader(testcase_f, answer_f)
    return negate(result)

def compile_and_run(xi_f, extra_options = None):
    if os.path.isfile('./xi_executable'):
        os.remove('./xi_executable')

    assembly_f = xi_f.rsplit('.', maxsplit=1)[0] + '.s'
    print_log("Generating Assembly")


    cmd = ['./xic', '--abstract','--comment','-libpath', LIB_PATH, xi_f]
    if extra_options != None:
        for option in extra_options:
            cmd.append(option)

    print(' '.join(cmd))

    run_shell(cmd)
    print_log("Linking Assembly")
    run_shell(['runtime/linkxi.sh', assembly_f, '-o', 'xi_executable'], end_on_error=True)
    print_log("Running Executable")
    run_shell(['./xi_executable'], end_on_error=False)



def build():
    if not silent:
        print("===CLEANING===")
    run_shell(['./clean'])
    if not silent:
        print("===BUILDING PROJECT===")
    run_shell(['./xic-build'], print_results=False)
    if not silent:
        print("====BUILDING BINARY RUNTIME====")
    run_shell(['make', '-C', 'runtime'], print_results=False)

def help():
    print(
"""===== Options ==================================
nobuild   -> don't rebuild project
all       -> run ALL tests
lex       -> run lex tests
parse     -> run parse tests
typecheck -> run typecheck tests
ir        -> run ir simulation tests
assm      -> run unoptimized assembly tests
assmo     -> run optimized assembly tests
pa7       -> run pa7 tests
pa7o      -> run pa7 tests with all optimizations on
silent    -> run with minimal printing
================================================
"""
    )

if __name__ == "__main__":

    if "silent" in sys.argv:
        silent = True

    if "all" in sys.argv:
        sys.argv.extend(['lex', 'parse', 'typecheck', 'ir', 'assm', 'assmo'])

    if "help" in sys.argv or len(sys.argv) == 1:
        help()
        sys.exit(0)

    if "-libpath" in sys.argv:
        LIB_PATH = sys.argv[sys.argv.index("-libpath") + 1]
        print(LIB_PATH)

    if not silent:
        print("Test Harness Begin")

    if "nobuild" not in sys.argv:
        build() # <-- TODO: uncomment!

    if "lex" in sys.argv:
        if not silent:
            print("===RUNNING LEX TESTS===")
        run_test_set(LEXER_TESTS, lex_grader)

    if "parse" in sys.argv:
        if not silent:
            print("===RUNNING PARSE TESTS===")
        run_test_set(PARSER_TESTS, parse_grader)

    if "typecheck" in sys.argv:
        if not silent:
            print("====RUNNING TYPECHECK TESTS===")
        run_test_set(TYPECHECKER_TESTS, typecheck_grader)

    if "ir" in sys.argv:
        if not silent:
            print("====RUNNING IRRUN TESTS====")
        run_test_set(IRRUN_TESTS, irrun_grader)

    if "assm" in sys.argv:
        if not silent:
            print("====RUNNING ASSM TESTS====")
        # print("Note: if this process is killed, we still have a critical issue in assm generation... test with run_assm.py")
        run_test_set(ASSM_TESTS, assm_grader) # reuse old tests because they test by output

    if "assmo" in sys.argv:
        if not silent:
            print("==== RUNNING OPTIMIZED ASSM TESTS ====")
        run_test_set(ASSM_TESTS, optimization_grader)

    if "pa7o" in sys.argv:
        if not silent:
            print("==== RUNNING OPTIMIZED PA7 TESTS ====")
        run_test_set(PA7_TESTS, optimization_grader)
        if not silent:
            print("==== RUNNING NEGATIVE OPTIMIZED PA7 TESTS ====")
        run_test_set(PA7_NEGATIVE_TESTS, negative_optimization_grader)
    if "pa7" in sys.argv:
        if not silent:
            print("==== RUNNING PA7 TESTS ====")
        run_test_set(PA7_TESTS, assm_grader)
        if not silent:
            print("==== RUNNING NEGATIVE PA7 TESTS ====")
        run_test_set(PA7_NEGATIVE_TESTS, negative_assm_grader)
    

    if not silent:
        print("Test Harness End!")

