set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_base2\client_build\app_build
java ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base2\log\client ^
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\sinnori.properties ^
-Dsinnori.projectName=sample_base2 ^
-jar dist\SinnoriAppClientRun.jar
cd /D %OLDPWD%