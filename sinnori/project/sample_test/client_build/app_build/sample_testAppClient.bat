set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_test\client_build\app_build
java -Xmx1024m -Xms1024m ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_test\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_test\log\client ^
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_test\config\sinnori.properties ^
-Dsinnori.projectName=sample_test ^
-jar dist\SinnoriAppClientRun.jar
cd /D %OLDPWD%