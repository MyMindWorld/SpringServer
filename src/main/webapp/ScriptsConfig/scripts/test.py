import string
from random import *
import os

characters = string.ascii_letters + string.punctuation + string.digits
password = "".join(choice(characters) for x in range(randint(8, 16)))
print(os.getcwd())
print(f"Your password is {password!r}")
