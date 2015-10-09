java -server -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_base/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_base/log/server \
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori \
-Dsinnori.projectName=sample_base \
-jar /home/madang01/gitsinnori/sinnori/project/sample_base/server_build/dist/SinnoriServerRun.jar