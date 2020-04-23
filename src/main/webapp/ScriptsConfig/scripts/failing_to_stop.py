import string
from random import *
import sys
import time
import signal

print(sys.argv[1:])


def signal_handler(sig, frame):
    print('Why are you trying to kill me?! I WANT TO LIVE!!!')
    time.sleep(20)
    print("ok, il DIE!")
    sys.exit(666)


signal.signal(signal.SIGINT, signal_handler)
signal.signal(signal.SIGTERM, signal_handler)

while True:
    print("Im still alive!")
    time.sleep(1)
