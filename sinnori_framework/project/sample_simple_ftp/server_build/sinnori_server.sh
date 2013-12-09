#!/bin/sh
export SINNORI_FRAMEWORK_LOC=/home/madang01/gitsinnori/sinnori_framework
export SINNORI_PROJECT_NAME=sample_simple_ftp
export JAVA_OPTS="-d64 -server -Xmx1024m -Xms1024m"
export SERVER_BUILD_LOC=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/server_build
export SINNORI_PROJECT_LOG_PATH=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/log/server
export SINNORI_PROJECT_CONFIG_FILE=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/config/project_config.properties
java -jar $SERVER_BUILD_LOC/dist/SinnoriServerMain.jar