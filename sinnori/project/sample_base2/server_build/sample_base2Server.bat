set OLDPWD=%CD%
cd /D D:\gitsinnori\sinnori\project\sample_base2\server_build
java -Xmx1024m -Xms1024m ^
-Dlogback.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\logback.xml ^
-Dsinnori.logPath=D:\gitsinnori\sinnori\project\sample_base2\log\server ^
-Dsinnori.configurationFile=D:\gitsinnori\sinnori\project\sample_base2\config\sinnori.properties ^
-Dsinnori.projectName=sample_base2 ^
-jar dist\SinnoriServerRun.jar
cd /D %OLDPWD%