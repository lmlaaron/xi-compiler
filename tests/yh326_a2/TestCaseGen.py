import os

ans_dir = "./answers/"
case_dir = "./testcases/"

precedence_tiers = [
    ['*', '*>>', '/', '%'],
    ['+', '-'],
    ['<', '<=', '>', '>='],
    ['==', '!='],
    ['&'],
    ['|']
]

def clean():
    to_rm = []
    for root, _, files in os.walk('.'):
        for name in files:
            if name.find('AUTO') != -1:
                to_rm.append(os.path.join(root, name))
    for f in to_rm:
        os.remove(f)

def generate():
    #TODO

if __name__ == '__main__':
    clean()