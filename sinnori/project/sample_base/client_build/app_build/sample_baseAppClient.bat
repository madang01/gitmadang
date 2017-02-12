set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_base\client_build\app_build
java  ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base\log\client ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_base ^
-jar dist\SinnoriAppClientRun.jar
cd /D %OLDPWD%