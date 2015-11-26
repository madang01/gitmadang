set OLDPWD=%CD%
cd /D /home/madang01/gitsinnori/sinnori/project/sample_base/client_build/app_build
java  ^
-Dlogback.configurationFile=/home/madang01/gitsinnori/sinnori/project/sample_base/config/logback.xml ^
-Dsinnori.logPath=/home/madang01/gitsinnori/sinnori/project/sample_base/log/client ^
-Dsinnori.installedPath=/home/madang01/gitsinnori/sinnori ^
-Dsinnori.projectName=sample_base ^
-jar dist/SinnoriAppClientRun.jar
cd /D %OLDPWD%