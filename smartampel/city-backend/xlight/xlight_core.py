from xlight.models import TrafficLight
from random import randint
import sched
import time


class XLightHandler ():

    SIMULATION_INTERVAL_S = 2

    def simulate_light_change(self, sc):
        print(len(self.xlights))
        self.s.enter(self.SIMULATION_INTERVAL_S, 1,
                     self.simulate_light_change, (sc,))

    def __init__(self):
        self.xlights = {}
        self.s = sched.scheduler(time.time, time.sleep)
        self.on_startup()

    def on_startup(self):
        print('-- init demo data')
        self.read_demo_data()
        print('-- init light simulation')
        self.s.enter(self.SIMULATION_INTERVAL_S, 1,
                     self.simulate_light_change, (self.s,))
        self.s.run()

    def read_demo_data(self):
        from csv import reader, DictReader

        csv_in = DictReader(
            open('xlight/demodata.csv', 'r', encoding='utf-8'), delimiter=',',
            quotechar='\"')

        lights = []
        for row in csv_in:
            print(row)
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
        print(self.xlights)
        xlight = self.xlights.get(beaconid, None)
        if not xlight:
            return None
        return xlight
