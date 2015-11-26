cd /home/madang01/gitsinnori/sinnori/project/sample_test/server_build
java -Xmx1024m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_test/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_test/log/server \
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori \
-Dsinnori.projectName=sample_test \
-jar dist/SinnoriServerRun.jar
cd -