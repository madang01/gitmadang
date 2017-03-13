set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_test\server_build
java  ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_test\resources\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_test\log\server ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_test ^
-jar dist\SinnoriServerRun.jar
cd /D %OLDPWD%