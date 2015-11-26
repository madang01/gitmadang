set OLDPWD=%CD%
cd /D /home/madang01/gitsinnori/sinnori/project/sample_fileupdown/client_build/app_build
java  ^
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_fileupdown/config/logback.xml ^
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_fileupdown/log/client ^
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori ^
-Dsinnori.projectName=sample_fileupdown ^
-jar dist/SinnoriAppClientRun.jar
cd /D %OLDPWD%