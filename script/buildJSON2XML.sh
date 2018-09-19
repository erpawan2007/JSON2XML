#!/bin/sh

#cd ../ \
#  && mvn clean \
#  && mvn install \
#  && ant create_run_jar

#upload to s3 
## Binaries
aws s3api put-object --bucket public.robomq --key packages/InfoGenesisPOS/JSON2XML/dist/json2xml-1.0.0.jar --body ./dist/json2xml-1.0.0.jar
#aws s3api put-object --bucket public.robomq --key packages/InfoGenesisPOS/JSON2XML/dist/json2xml_lib.zip --body ./dist/json2xml_lib.zip
## Config and run files
aws s3api put-object --bucket public.robomq --key packages/InfoGenesisPOS/JSON2XML/runJSON2XML.sh --body ./script/runJSON2XML.sh
aws s3api put-object --bucket public.robomq --key packages/InfoGenesisPOS/JSON2XML/config/quboid.json --body ./config/quboid.json
aws s3api put-object --bucket public.robomq --key packages/InfoGenesisPOS/JSON2XML/config/log4j.properties --body ./config/log4j.properties
