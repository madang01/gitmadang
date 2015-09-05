java -d64 -server -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_base2/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_base2/log/server \
-Dsinnori.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_base2/config/sinnori.properties \
-Dsinnori.projectName=sample_base2 \
-jar /home/madang01/gitsinnori/sinnori/project/sample_base2/server_build/dist/SinnoriServerRun.jar