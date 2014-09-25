cd D:\gitsinnori\sinnori_framework\project\sample_db\server_build
java -d64 -server -Xmx1024m -Xms1024m ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_db\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori_framework\project\sample_db\log\server ^
-Dsinnori.configurationFile=D:\gitsinnori\sinnori_framework\project\sample_db\config\sinnori.properties ^
-Dsinnori.projectName=sample_db ^
-jar D:\gitsinnori\sinnori_framework\project\sample_db\server_build\dist\SinnoriServerMain.jar