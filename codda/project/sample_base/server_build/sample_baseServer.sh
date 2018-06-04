cd D:\gitsinnori\sinnori\project\sample_base\server_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\resources\logback.xml \
-Dcodda.logPath=D:\gitsinnori\sinnori\project\sample_base\log\server \
-Dcodda.installedPath=D:\gitsinnori\sinnori \
-Dcodda.projectName=sample_base \
-jar dist\SinnoriServerRun.jar
cd -