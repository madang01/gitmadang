set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_base\server_build
java -Xmx1024m -Xms1024m ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base\resources\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base\log\server ^
-Dsinnori.installedPath=D:\gitsinnori\sinnori ^
-Dsinnori.projectName=sample_base ^
-jar dist\SinnoriServerRun.jar
cd /D %OLDPWD%