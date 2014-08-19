set SINNORI_FRAMEWORK_LOC=D:\gitsinnori\sinnori_framework
set SINNORI_PROJECT_NAME=sample_fileupdown
set JAVA_OPTS="-d64 -server -Xmx1024m -Xms1024m"
set LOGBACK_CONFIG_FILE=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\config\logback.xml
set SERVER_BUILD_LOC=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\server_build
set SINNORI_PROJECT_LOG_PATH=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\log\server
set SINNORI_PROJECT_CONFIG_FILE=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\config\sinnori.properties
java -Dlogback.configurationFile=%LOGBACK_CONFIG_FILE%  -jar %SERVER_BUILD_LOC%\dist\SinnoriServerMain.jar