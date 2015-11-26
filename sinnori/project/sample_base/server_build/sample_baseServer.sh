cd /home/madang01/gitsinnori/sinnori/project/sample_base/server_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_base/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_base/log/server \
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori \
-Dsinnori.projectName=sample_base \
-jar dist/SinnoriServerRun.jar
cd -