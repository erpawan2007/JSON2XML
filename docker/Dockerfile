############################################################
# Dockerfile for building image for JSON2XML Microservice
# Based on Ubuntu 16.04
############################################################


FROM us.gcr.io/robomq-demo/quboid_u16.04_v1.0

# Config version
ENV CONFIG_DEPENDENCIES 1.00

# Installng packages
RUN apt-get update \
 && apt-get install -y jq \
 && mkdir -p /opt/thingsConnect/config/json2xml \
 && mkdir -p /opt/thingsConnect/lib/json2xml \
 && mkdir -p /opt/thingsConnect/lib/common \

#Copy config files
 && wget https://s3.amazonaws.com/public.robomq/packages/InfoGenesisPOS/JSON2XML/config/quboid.json -P /opt/thingsConnect/config/json2xml/ \
 && wget https://s3.amazonaws.com/public.robomq/packages/InfoGenesisPOS/JSON2XML/config/log4j.properties -P /opt/thingsConnect/config/json2xml/ \

#Copy the jar file
 && wget https://s3.amazonaws.com/public.robomq/packages/InfoGenesisPOS/JSON2XML/dist/json2xml-1.0.0.jar -P /opt/thingsConnect/lib/json2xml \

#Copy the dependency jars
# && wget https://s3.amazonaws.com/public.robomq/packages/InfoGenesisPOS/JSON2XML/dist/json2xml_lib.zip \
# && unzip -n json2xml_lib.zip -d /opt/thingsConnect/lib/common/ \
# && rm json2xml_lib.zip \
 

#COPY the script
 && wget https://s3.amazonaws.com/public.robomq/packages/InfoGenesisPOS/JSON2XML/runJSON2XML.sh -P /usr/local/bin/ \
 && chmod 755 /usr/local/bin/runJSON2XML.sh 

#Run command
CMD runJSON2XML.sh

#Example run command
# docker run -d --name ig-json2xml -v /<path>/<to>/<config>/<dir>/:/opt/thingsConnect/config/json2xml/ us.gcr.io/topgolf01-robomq/json2xml
