"""WSGI-hook for XLight to combine Django with a WSGI-compatible web
server like Apache HTTP or Unicorn."""

# The following two lines MUST stick to the top in that order
# right on top. Otherwise WSGI-usage will not work.
from os import environ
environ.setdefault('DJANGO_SETTINGS_MODULE', 'xlight_project.settings')

from django.core.wsgi import get_wsgi_application
from xlight.xlight_core import XLightHandler

# run XLight's system initialization procedure
xlight_handler = XLightHandler()
xlight_handler.on_startup()

application = get_wsgi_application()
