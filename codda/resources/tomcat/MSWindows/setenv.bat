REM for %%f in (D:\sinnori_framework\core_build\lib\ex\*.jar) do set CLASSPATH=%CLASSPATH%;%%f
REM 이 동작하지 않았는데 이에 대한 해결 방법을 아래 사이트에서 얻음.
REM 해결 방법 제시 참고 사이트 : http://alvinalexander.com/blog/post/page-1/thu-mar-9-2006-dynamically-build-environment-variables-in-dos-c


SET CODDA_HOME=D:\gitmadang\codda
SET PROJECT_NAME=sample_base
SET WEB_BUILD_HOME=%CODDA_HOME%\project\%PROJECT_NAME%\client_build\web_build\
SET WEB_CORELIB_PATH=%WEB_BUILD_HOME%\corelib\ex
SET WEB_MAINLIB_PATH=%WEB_BUILD_HOME%\lib\main\ex
SET CLASSPATH=%CATALINA_HOME%\lib\servlet-api.jar;%CATALINA_HOME%\lib\jsp-api.jar;%WEB_BUILD_HOME%\dist\CoddaWebLib.jar

for %%f in (%WEB_CORELIB_PATH%\*.jar) do call CPAPPEND.BAT %%f
for %%f in (%WEB_MAINLIB_PATH%\*.jar) do call CPAPPEND.BAT %%f

echo classpath=%CLASSPATH%

set JAVA_OPTS=-Dcodda.projectName=%PROJECT_NAME% -Dcodda.installedPath=%CODDA_HOME% ^
-Dlogback.configurationFile=%CODDA_HOME%\project\%PROJECT_NAME%\resources\logback.xml ^
-Dcodda.logPath=%CODDA_HOME%\project\%PROJECT_NAME%\log\tomcat ^
-Dfile.encoding=UTF-8 -Dfile.client.encoding=UTF-8 -Dclient.encoding.override=UTF-8

echo JAVA_OPTS=%JAVA_OPTS%