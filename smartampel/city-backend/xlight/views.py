"""Django default module for view generation"""

from django.shortcuts import redirect, render

from django.http import HttpResponse
import json
from xlight.xlight_core import XLightHandler
from xlight.models import TrafficLight, ApiStatus
from re import sub
from django.core import serializers


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

    xlight_handler = None
    """Handler to create the webpage context for incoming GET requests"""

    def __init__(self):
        """Constructor"""
        self.xlight_handler = XLightHandler()

    def get(self, request):
        """This method serves the GET requests to the web photo albums"""

        if not request:
            raise TypeError

    
        clean_path = cleanup_url_path(request.path)
        if not request.path == clean_path:
            return redirect(clean_path)
        
        if not request.path.endswith('/'):
            request.path = request.path + '/'

            
        beaconid = request.GET.get('beaconid')
        print('-- beacon id {} requested'.format(beaconid))
        
        xlight = self.xlight_handler.get_xlight_state(beaconid)
        error = ApiStatus(http_code=200, error_message="")
        if not xlight:
            error = ApiStatus(
                http_code=404, error_message="xlight not registered")
            xlight = TrafficLight()

        serialized_data = serializers.serialize("json", [xlight, error])
        return HttpResponse(serialized_data, content_type="application/json")
