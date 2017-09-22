#!/usr/bin/env python

from os import environ, path, chdir
import sys
import shutil

# change to manage.py's folder
abspath = path.abspath(__file__)
dname = path.dirname(abspath)
chdir(dname)

if __name__ == "__main__":

    environ.setdefault("DJANGO_SETTINGS_MODULE", "xlight_project.settings")
    from django.core.management import execute_from_command_line
    execute_from_command_line(sys.argv)
