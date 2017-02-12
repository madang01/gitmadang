set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_fileupdown\client_build\app_build
java  ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_fileupdown\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_fileupdown\log\client ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_fileupdown ^
-jar dist\SinnoriAppClientRun.jar
cd /D %OLDPWD%