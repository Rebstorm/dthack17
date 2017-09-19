from xlight.models import TrafficLight
from random import randint
from time import sleep
import threading


class XLightHandler ():

    def start_simulation(self):
        print(len(self.xlights))

    def __init__(self):
        self.xlights = {}
        self.on_startup()

    def on_startup(self):
        print('-- init demo data')
        self.read_demo_data()
        print('-- init light simulation')
        threading.Timer(5.0, self.start_simulation).start()

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
            while True:
                status = randint(0, 4)
                if status <= 2:
                    break
            light.current_status = status
            self.xlights[light.beaconid] = light

    def get_xlight_state(self, beaconid):
        print(self.xlights)
        xlight = self.xlights.get(beaconid, None)
        if not xlight:
            return None
        return xlight
