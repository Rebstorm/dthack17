import multiprocessing
from random import randrange
from threading import Thread
from time import sleep
from queue import Queue


def simulate_traffic_lights(xlights):
    print(len(xlights))
    sleep(5)


class Worker(Thread):
    """Thread executing tasks from a given tasks queue"""

    def __init__(self, tasks):
        Thread.__init__(self)
        self.tasks = tasks
        self.daemon = True
        self.start()

    def run(self):
        while True:
            print('>>>>')
            func, args, kargs = self.tasks.get()
            try:
                func(*args, **kargs)
            except Exception as e:
                print(e)
            print('<<<<')
