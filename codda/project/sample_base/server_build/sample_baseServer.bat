set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_base\server_build
java -server -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_base\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_base\log\server ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_base ^
-jar dist\CoddaServerRun.jar
cd /D %OLDPWD%