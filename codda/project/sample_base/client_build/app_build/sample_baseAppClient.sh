cd D:\gitsinnori\sinnori\project\sample_base\client_build\app_build
java  \
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\resources\logback.xml \
-Dsinnori.logPath=D:\gitmadang\codda\project\sample_base\log\appclient \
-Dsinnori.installedPath=D:\gitsinnori\sinnori \
-Dsinnori.projectName=sample_base \
-jar dist\SinnoriAppClientRun.jar $1
cd -