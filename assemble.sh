#Se supone que uno está dentro del proyecto maven descargado de Git
#En caso contrario, favor de cambiar de directorio a donde se ubique el proyecto descargado: cd APIRestJava

mysql -u root -p < marvel_sync.sql

mvn clean dependency:copy-dependencies package