cd D:\gitsinnori\sinnori\project\sample_sync_fileupdown\client_build\app_build
java -Xmx2048m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_sync_fileupdown\resources\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_sync_fileupdown\log\appclient \
-Dsinnori.installedPath=D:\gitsinnori\sinnori \
-Dsinnori.projectName=sample_sync_fileupdown \
-jar dist\SinnoriAppClientRun.jar
cd -