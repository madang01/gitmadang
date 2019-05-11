cd /home/madang01/gitmadang/codda/project/sample_base/server_build
java -server -Xmx2048m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitmadang/codda/project/sample_base/resources/logback.xml \
-Dcodda.logPath=/home/madang01/gitmadang/codda/project/sample_base/log/server \
-Dcodda.installedPath=/home/madang01/gitmadang/codda \
-Dcodda.projectName=sample_base \
-jar dist/CoddaServerRun.jar
cd -