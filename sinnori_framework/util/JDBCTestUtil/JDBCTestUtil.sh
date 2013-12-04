#!/bin/sh
export SINNORI_LOG_PATH=/home/madang01/sinnori_framework/sample_simple_chat/log/client
export SINNORI_CONFIG_FILE=/home/madang01/sinnori_framework/sample_simple_chat/config/sinnori_config.properties

java -jar dist/JDBCTestUtil.jar
