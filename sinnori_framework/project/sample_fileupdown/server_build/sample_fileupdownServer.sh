java -d64 -server -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_fileupdown/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori_framework/project/sample_fileupdown/log/server \
-Dsinnori.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_fileupdown/config/sinnori.properties \
-Dsinnori.projectName=sample_fileupdown \
-jar /home/madang01/gitsinnori/sinnori_framework/project/sample_fileupdown/server_build/dist/SinnoriServerMain.jar