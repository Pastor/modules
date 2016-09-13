#!/bin/sh

docker run -p 80:80 -p 8080:8080 -p 433:433 -p 2200:22 -d --name al1 pastorfreak/alacarte:v1
