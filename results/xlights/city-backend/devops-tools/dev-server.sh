#!/bin/bash

cd $( dirname $( readlink -f $0 ) )/../
echo "-- killing old server" 
ps -ef | grep 63434 | awk '{print $2}' | xargs kill -9 
echo "-- firing up"
nohup python3 manage.py runserver 63434 &
echo "-- done"
