#!/bin/sh
export SINNORI_FRAMEWORK_LOC=/home/madang01/gitsinnori/sinnori_framework
export SINNORI_PROJECT_NAME=sample_simple_ftp
export JAVA_OPTS="-Xmx1024m -Xms1024m"
export APPCLIENT_BUILD_LOC=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/client_build/app_build
export SINNORI_PROJECT_LOG_PATH=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/log/client
export SINNORI_PROJECT_CONFIG_FILE=$SINNORI_FRAMEWORK_LOC/project/$SINNORI_PROJECT_NAME/config/project_config.properties

java -jar dist/JDBCTestUtil.jar
