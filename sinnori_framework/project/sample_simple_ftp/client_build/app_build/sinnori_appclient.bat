set SINNORI_FRAMEWORK_LOC=D:\sinnori_framework
set SINNORI_PROJECT_NAME=sample_simple_ftp
set JAVA_OPTS="-Xmx1024m -Xms1024m"


set APPCLIENT_BUILD_LOC=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\client_build\app_build
set SINNORI_PROJECT_LOG_PATH=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\log\client
set SINNORI_PROJECT_CONFIG_FILE=%SINNORI_FRAMEWORK_LOC%\project\%SINNORI_PROJECT_NAME%\config\project_config.properties


java %JAVA_OPTS% -jar %APPCLIENT_BUILD_LOC%\dist\SinnoriAppClientMain.jar
