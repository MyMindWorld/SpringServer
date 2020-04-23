import string
from random import *
import sys

print(sys.argv[1:])

print(f"##ScriptServer[BooleanCustom'Do you want super secure password?/Yes i want!/No i don't!']")
new_needed = input()
if new_needed == "1":
    print("Here will be just text info modal")
else:
    print(f"##ScriptServer[InputText'Please tell me why?!']")
    reason = input()
    print(f"##ScriptServer[BooleanCustom'Oh, because {reason}?!/No,sry../Ofc, u dumb?']")
    is_changed_mind = input()
    if is_changed_mind != "1":
        print(f"##ScriptServer[ShowInfo'Bye Bye then!']")
        exit(1)
    else:
        print(f"##ScriptServer[TextArea'Tell me why u asking me to believe you']")
        input()

answer = ""
iternum = 0
while answer != "1":
    iternum += 1
    characters = string.ascii_letters + string.punctuation + string.digits
    password = "".join(choice(characters) for x in range(randint(8, 16)))
    print(f"Your password is {password!r}")
    print(f"##ScriptServer[Boolean'Are you satisfied with password {password!r} ? ({iternum})']")
    answer = input()
    print(f"received input {answer}")
print("Have a good and secure life with your password!")
