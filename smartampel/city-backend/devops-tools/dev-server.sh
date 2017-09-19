#!/bin/bash

cd $( dirname $( readlink -f $0 ) )
cd dthack17
git pull 
cd smartampel/city-backend 
nohup python3 manage.py runserver 63434 &
ps -ef | grep 63434 | awk '{print $2}' | xargs kill -9 

