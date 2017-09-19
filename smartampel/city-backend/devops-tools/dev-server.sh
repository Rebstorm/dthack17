#!/bin/bash
cd "$( dirname "$( readlink -f "$0" )" )"
[ ! -z $( command -v python3 ) ] && PYCOM="python3" || PYCOM="python"
$PYCOM manage.py test && $PYCOM manage.py runserver localhost:8000
