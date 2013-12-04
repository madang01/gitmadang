set SINNORI_FRAMEWORK_LOC=D:\sinnori_framework
set SINNORI_PROJECT_NAME=sample_simple_ftp
set JAVA_OPTS="-d64 -server -Xmx1024m -Xms1024m"

set SERVER_BUILD_LOC=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\server_build
set SINNORI_PROJECT_LOG_PATH=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\log\server
set SINNORI_PROJECT_CONFIG_FILE=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\config\project_config.properties

java -jar %SERVER_BUILD_LOC%\dist\SinnoriServerMain.jar
