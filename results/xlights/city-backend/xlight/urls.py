"""Django default module for url pattern definition"""

from django.conf.urls import patterns, url
from xlight.views import ViewHandler

urlpatterns = patterns('',
                       url(r'^', ViewHandler().get, name='get'),
                       )
