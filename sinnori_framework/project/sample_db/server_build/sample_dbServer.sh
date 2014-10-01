java -d64 -server -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_db/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori_framework/project/sample_db/log/server \
-Dsinnori.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_db/config/sinnori.properties \
-Dsinnori.projectName=sample_db \
-jar /home/madang01/gitsinnori/sinnori_framework/project/sample_db/server_build/dist/SinnoriServerMain.jar