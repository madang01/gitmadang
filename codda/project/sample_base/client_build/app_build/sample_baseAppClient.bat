set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_base\client_build\app_build
java -Xmx512m -Xms512m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_base\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_base\log\appclient ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_base ^
-jar dist\CoddaAppClientRun.jar 10
cd /D %OLDPWD%