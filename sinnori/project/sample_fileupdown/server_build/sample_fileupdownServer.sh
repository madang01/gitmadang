cd D:\gitsinnori\sinnori\project\sample_fileupdown\server_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_fileupdown\config\logback.xml \
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_fileupdown\log\server \
-Dsinnori.installedPath=D:\gitsinnori\sinnori \
-Dsinnori.projectName=sample_fileupdown \
-jar dist\SinnoriServerRun.jar
cd -