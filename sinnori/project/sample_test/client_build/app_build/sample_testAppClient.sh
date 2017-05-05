cd D:\gitsinnori\sinnori\project\sample_test\client_build\app_build
java -Xmx2048m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_test\resources\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_test\log\appclient \
-Dsinnori.installedPath=D:\gitsinnori\sinnori \
-Dsinnori.projectName=sample_test \
-jar dist\SinnoriAppClientRun.jar
cd -