set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_sync_fileupdown\server_build
java -server -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_sync_fileupdown\resources\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_sync_fileupdown\log\server ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_sync_fileupdown ^
-jar dist\SinnoriServerRun.jar
cd /D %OLDPWD%