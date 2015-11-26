cd /home/madang01/gitsinnori/sinnori/project/sample_test/client_build/app_build
java  \
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_test/config/logback.xml \
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_test/log/client \
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori \
-Dsinnori.projectName=sample_test \
-jar dist/SinnoriAppClientRun.jar
cd -