import subprocess

def test():
    subprocess.run(['python3', 'th.py', 'silent', 'assmo'])

def rollback():
    subprocess.run(['git', 'checkout', 'HEAD^'])

def main():
    subprocess.run(['git', 'checkout', 'master'])
    while True:
        #print("Current hash:")
        #subprocess.run(['git', 'rev-parse', 'HEAD'])
        test()
        rollback()



if __name__ == '__main__':
    main()