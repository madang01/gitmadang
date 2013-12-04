set SINNORI_FRAMEWORK_LOC=D:\sinnori_framework
set PROJECT_NAME=sample_simple_chat
set JAVA_OPTS="-Xmx1024m -Xms1024m"

set APPCLIENT_BUILD_LOC=%SINNORI_FRAMEWORK_LOC%\%PROJECT_NAME%\client_build\app_build
set SINNORI_LOG_PATH=%SINNORI_FRAMEWORK_LOC%\%PROJECT_NAME%\log\server
set SINNORI_CONFIG_FILE=%SINNORI_FRAMEWORK_LOC%\%PROJECT_NAME%\config\sinnori_config.properties

java -jar dist/JDBCTestUtil.jar
