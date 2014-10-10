java -d64 -server -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_test/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori_framework/project/sample_test/log/server \
-Dsinnori.configurationFile=/home/madang01/gitsinnori/sinnori_framework/project/sample_test/config/sinnori.properties \
-Dsinnori.projectName=sample_test \
-jar /home/madang01/gitsinnori/sinnori_framework/project/sample_test/server_build/dist/SinnoriServerMain.jar