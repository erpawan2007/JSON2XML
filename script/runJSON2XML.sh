#!/bin/sh

java -Dlog4j.configuration=file:/opt/thingsConnect/config/json2xml/log4j.properties -DquboidConfig=/opt/thingsConnect/config/json2xml/quboid.json -jar /opt/thingsConnect/lib/json2xml/json2xml-1.0.0.jar
