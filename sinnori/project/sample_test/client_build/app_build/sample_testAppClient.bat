set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_test\client_build\app_build
java  ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_test\resources\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_test\log\client ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_test ^
-jar dist\SinnoriAppClientRun.jar
cd /D %OLDPWD%