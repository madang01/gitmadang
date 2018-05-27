REM 'Dynamically build a Java classpath in a Windows batch file' 참고 사이트 : http://alvinalexander.com/blog/post/page-1/thu-mar-9-2006-dynamically-build-environment-variables-in-dos-c

SET CODDA_HOME=D:\gitmadang\codda
SET PROJECT_NAME=sample_base
SET WEB_BUILD_HOME=%CODDA_HOME%\project\%PROJECT_NAME%\client_build\web_build
SET WEB_CORELIB_PATH=%WEB_BUILD_HOME%\corelib\ex
SET WEB_MAINLIB_PATH=%WEB_BUILD_HOME%\lib\main\ex
SET CLASSPATH=

for %%f in (%WEB_CORELIB_PATH%\*.jar) do call CPAPPEND.BAT %%f
for %%f in (%WEB_MAINLIB_PATH%\*.jar) do call CPAPPEND.BAT %%f

echo classpath=%CLASSPATH%

set JAVA_OPTS=-Dcodda.projectName=%PROJECT_NAME% -Dcodda.installedPath=%CODDA_HOME% ^
-Dlogback.configurationFile=%CODDA_HOME%\project\%PROJECT_NAME%\resources\logback.xml ^
-Dcodda.logPath=%CODDA_HOME%\project\%PROJECT_NAME%\log\servlet ^
-Dfile.encoding=UTF-8 -Dfile.client.encoding=UTF-8 -Dclient.encoding.override=UTF-8

echo JAVA_OPTS=%JAVA_OPTS%