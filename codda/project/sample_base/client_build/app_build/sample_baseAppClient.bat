set OLDPWD=%CD%
cd /D D:\gitmadang\codda\project\sample_base\client_build\app_build
java -Xmx2048m -Xms1024m ^
-Dlogback.configurationFile=D:\gitmadang\codda\project\sample_base\resources\logback.xml ^
-Dcodda.logPath=D:\gitmadang\codda\project\sample_base\log\appclient ^
-Dcodda.installedPath=D:\gitmadang\codda ^
-Dcodda.projectName=sample_base ^
-jar dist\CoddaAppClientRun.jar 1
cd /D %OLDPWD%