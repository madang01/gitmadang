cd D:\gitsinnori\sinnori\project\sample_base\client_build\app_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\config\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base\log\client \
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_base\config\sinnori.properties \
-Dsinnori.projectName=sample_base \
-jar dist\SinnoriAppClientRun.jar
cd -