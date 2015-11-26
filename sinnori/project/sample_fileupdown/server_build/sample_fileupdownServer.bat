set OLDPWD=%CD%
cd /D /home/madang01/gitsinnori/sinnori/project/sample_fileupdown/server_build
java -Xmx1024m -Xms1024m ^
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_fileupdown/config/logback.xml ^
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_fileupdown/log/server ^
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori ^
-Dsinnori.projectName=sample_fileupdown ^
-jar dist/SinnoriServerRun.jar
cd /D %OLDPWD%