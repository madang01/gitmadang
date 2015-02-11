cd D:\gitsinnori\sinnori\project\sample_base\server_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\config\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base\log\server \
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_base\config\sinnori.properties \
-Dsinnori.projectName=sample_base \
-jar dist\SinnoriServerRun.jar
cd -