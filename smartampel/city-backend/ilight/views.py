"""Django default module for view generation"""

from django.shortcuts import redirect, render

from django.http import HttpResponse
import json
from ilight.ilight_core import ILightHandler
from ilight.models import TrafficLight, ApiError
from re import sub


def cleanup_url_path(url_path):
    """Removes redundant or unwanted symbols from the provided URL path"""

    if not url_path:
        url_path = '/'

    clean_path = sub('[/]+', '/', url_path)
    clean_path = sub('index.html$', '', clean_path)
    return clean_path


class ViewHandler ():
    """Instances of this class handle incoming GET requests and serve
    the appropriate HTTP responses"""

    ilight_handler = None
    """Handler to create the webpage context for incoming GET requests"""

    def __init__(self):
        """Constructor"""
        self.ilight_handler = ILightHandler()

    def get(self, request):
        """This method serves the GET requests to the web photo albums"""

        if not request:
            raise TypeError

        beaconid = request.GET.get('beaconid')
        clean_path = cleanup_url_path(request.path)
        if not request.path == clean_path:
            return redirect(clean_path)

        if not request.path.endswith('/'):
            request.path = request.path + '/'

        from django.core import serializers

        tlight = TrafficLight(
            beaconid=beaconid,
            location_street="Musterstraße",
            location_streetno="100",
            current_status=0,
            beacon_light_distance=0
        )
        error = ApiError(http_code=200, error_message="-")
        serialized_data = serializers.serialize("json", [tlight, error])
        return HttpResponse(serialized_data, content_type="application/json")
