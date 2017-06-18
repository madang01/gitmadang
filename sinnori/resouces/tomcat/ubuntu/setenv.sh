#!/bin/sh
export PROJECT_NAME=sample_base
export SINNORI_INSTALL_PATH=/home/madang01/gitsinnori/sinnori
export SINNORI_WEBLIB_PATH=$SINNORI_INSTALL_PATH/project/$PROJECT_NAME/client_build/web_build/corelib/ex

for jarfile in $SINNORI_WEBLIB_PATH/*.jar
do
        export CLASSPATH=$CLASSPATH:${jarfile}
done

echo $CLASSPATH

export JAVA_OPTS="$JAVA_OPTS \
-Dfile.encoding=UTF-8 \
-Dlogback.configurationFile=$SINNORI_INSTALL_PATH/project/$PROJECT_NAME/config/logback.xml \
-Dsinnori.logPath=$SINNORI_INSTALL_PATH/project/$PROJECT_NAME/log/servlet \
-Dsinnori.installedPath=$SINNORI_INSTALL_PATH \
-Dsinnori.projectName=$PROJECT_NAME"
