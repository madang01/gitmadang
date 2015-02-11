cd D:\gitsinnori\sinnori\project\sample_base2\client_build\app_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base2\log\client \
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\sinnori.properties \
-Dsinnori.projectName=sample_base2 \
-jar dist\SinnoriAppClientRun.jar
cd -