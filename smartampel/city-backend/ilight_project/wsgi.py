"""WSGI-hook for ILight to combine Django with a WSGI-compatible web
server like Apache HTTP or Unicorn."""

# The following two lines MUST stick to the top in that order
# right on top. Otherwise WSGI-usage will not work.
from os import environ
environ.setdefault('DJANGO_SETTINGS_MODULE', 'ilight_project.settings')

from django.core.wsgi import get_wsgi_application
from ilight.ilight_core import ILightHandler

# run ILight's system initialization procedure
ilight_handler = ILightHandler()
ilight_handler.on_startup()

application = get_wsgi_application()
