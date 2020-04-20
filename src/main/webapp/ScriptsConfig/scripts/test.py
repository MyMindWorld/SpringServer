import string
from random import *
import os
import sys

print(sys.argv[1:])
answer = ""

while answer != "1":
    print("In while")
    characters = string.ascii_letters + string.punctuation + string.digits
    password = "".join(choice(characters) for x in range(randint(8, 16)))
    print(os.getcwd())
    print(f"Your password is {password!r}")
    print("##ScriptServer[InputText'Are you satisfied with your password?(1/0)']")
    answer = input()
    print("received input")
print("Have a good and secure life with your password!")