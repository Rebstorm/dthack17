from xlight.models import TrafficLight
from random import randint
import sched
import time
import threading


class XLightHandler ():

    SIMULATION_INTERVAL_S = 10

    def run_simulator(self):
        while True:
            # select a random traffic light
            len_x = len(self.xlights)
            rand = randint(0, len_x - 1)
            xlight_key = list(self.xlights.keys())[rand]
            xlight = self.xlights[xlight_key]

            # if light is red turn to green and vice versa
            if xlight.current_status == 2:
                new_state = 1
            else:
                new_state = 2

            # set state to yellow first
            xlight.current_status = 3
            time.sleep(2)
            xlight.current_status = new_state
            # ------------------------
            time.sleep(self.SIMULATION_INTERVAL_S)

    def __init__(self):
        self.xlights = {}
        self.read_demo_data()
        self.sim_thread = threading.Thread(target=self.run_simulator)
        self.sim_thread.start()

    def read_demo_data(self):
        from csv import reader, DictReader

        csv_in = DictReader(
            open('xlight/demodata.csv', 'r', encoding='utf-8'), delimiter=',',
            quotechar='\"')

        lights = []
        for row in csv_in:
            light = TrafficLight()
            light.beaconid = row['beaconid']
            light.location_city = row['city']
            light.location_street = row['street']
            light.location_postcode = row['postcode']
            light.location_streetno = row['no']
            light.beacon_light_distance = row['bld']
            light.current_status = randint(1, 2)
            self.xlights[light.beaconid] = light

    def get_xlight_state(self, beaconid):
        xlight = self.xlights.get(beaconid, None)
        if not xlight:
            return None
        return xlight
