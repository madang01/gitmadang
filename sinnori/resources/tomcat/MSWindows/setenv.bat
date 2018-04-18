REM for %%f in (D:\sinnori_framework\core_build\lib\ex\*.jar) do set CLASSPATH=%CLASSPATH%;%%f
REM 이 동작하지 않았는데 REM 이에 대한 해결 방법을 아래 사이트에서 얻음.
REM 해결 방법 제시 참고 사이트 : http://alvinalexander.com/blog/post/page-1/thu-mar-9-2006-dynamically-build-environment-variables-in-dos-c

SET SINNORI_HOME=D:\gitsinnori\sinnori
SET PROJECT_NAME=sample_test
SET SAMPLE_BASE_WEB_BUILD_HOME=%SINNORI_HOME%\project\%PROJECT_NAME%\client_build\web_build
SET CLASSPATH=
for %%f in (%SAMPLE_BASE_WEB_BUILD_HOME%\corelib\ex\*.jar) do call CPAPPEND.BAT %%f
echo classpath=%CLASSPATH%

set JAVA_OPTS=-Dsinnori.projectName=%PROJECT_NAME% -Dsinnori.installedPath=%SINNORI_HOME%

echo JAVA_OPTS=%JAVA_OPTS%