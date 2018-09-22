#!/bin/sh
export CODDA_HOME=/home/madang01/gitmadang/codda
export PROJECT_NAME=sample_base
export WEB_BUILD_PATH=$CODDA_HOME/project/$PROJECT_NAME/client_build/web_build
export WEB_CORELIB_PATH=$WEB_BUILD_PATH/corelib/ex
export WEB_MAINLIB_PATH=$WEB_BUILD_PATH/lib/main/ex

for jarfile in $WEB_CORELIB_PATH/*.jar
do
        export CLASSPATH=$CLASSPATH:${jarfile}
done

for jarfile in $WEB_MAINLIB_PATH/*.jar
do
        export CLASSPATH=$CLASSPATH:${jarfile}
done

echo $CLASSPATH

export JAVA_OPTS="$JAVA_OPTS \
-Dfile.encoding=UTF-8 \
-Dlogback.configurationFile=$CODDA_HOME/project/$PROJECT_NAME/config/logback.xml \
-Dcodda.logPath=$CODDA_HOME/project/$PROJECT_NAME/log/tomcat \
-Dcodda.installedPath=$CODDA_HOME \
-Dcodda.projectName=$PROJECT_NAME"