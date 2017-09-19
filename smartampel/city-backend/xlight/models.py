"""Django default module for model definition"""

from django.db import models


class TrafficLight(models.Model):
    """Traffic light object"""

    beaconid = models.CharField(max_length=100)
    """iBeacon ID corresponding to traffic light"""
    location_street = models.CharField(max_length=100)
    """Location of beacon: Street name"""
    location_streetno = models.CharField(max_length=10)
    """Location of beacon: Street number"""
    current_status = models.IntegerField()
    """Status of current traffic light.
        0 - Idle/disabled 
        1 - Green 
        2 - Yellow 
        3 - Red  
    """
    beacon_light_distance = models.IntegerField()
    """Distance between beacon and traffic light"""


class ApiError(models.Model):

    http_code = models.IntegerField()
    error_message = models.CharField(max_length=1000)
