cd /home/madang01/gitmadang/codda/project/sample_base/client_build/app_build
java -Xmx2048m -Xms1024m \
-Dlogback.configurationFile=/home/madang01/gitmadang/codda/project/sample_base/resources/logback.xml \
-Dcodda.logPath=/home/madang01/gitmadang/codda/project/sample_base/log/appclient \
-Dcodda.installedPath=/home/madang01/gitmadang/codda \
-Dcodda.projectName=sample_base \
-jar dist/CoddaAppClientRun.jar
cd -