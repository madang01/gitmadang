set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_base\server_build
java -server -Xmx512m -Xms512m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_base\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_base\log\server ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_base ^
-jar dist\CoddaServerRun.jar
cd /D %OLDPWD%