# Operadores Consumer
## Qué es
Creo que es  en un poco intuitiva la definicón de este operador, este operador consume datos del stream de datos para escribirlos ya sea a TERADATA o un destino externo, como un archivo plano o HDFS.
## Tipos de operadores Consumer
* En esta imagen se pueden apreciar los diferentes tipos d e operadores consumer.
![operador](operator.png)
## Extracción de datos y enviarlos directamente a HDFS
* Para este job el flujo sería el siguiente:
![flujo](flujo.png)
## Configuración de HDFS en máquina virtual.

* Primero tenemos que intercambiar  llaves ssh
```
ssh-keygen -t rsa
ssh-copy-id user@host
ssh-add
ssh user@host --> Cuando iniciemos sesión no se nos debe solicitar contraseña
```
* Editar core-site.xml
```XML
<configuration>
  <property>
      <name>fs.defaultFS</name>
      <value>hdfs://hostname:9000</value>
  </property>
  <property>
    <name>fs.default.name</name>
    <value>hdfs://hostname:8020</value>
  </property>
</configuration>
```
* Editar  yarn-site.xml
```XML
<configuration>
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
  <property>
    <name>yarn.resourcemanager.address</name>
    <value>hostname:8032</value>
  </property>
</configuration>
```
* Editar mapred-site.xml.template
```XML
<configuration>
  <property>
    <name>mapreduce.framework.name</name>
    <value>yarn</value>
  </property>
  <property>
      <name>mapreduce.jobtracker.address</name>
      <value>hostname:9001</value>
</property>
</configuration>
```
## TPT
* Archivo del job

```
DEFINE JOB TERADATA_TO_HDFS
DESCRIPTION 'export a table to hdfs'
(
DEFINE SCHEMA SCHEMA_EMP FROM TABLE 'NEW_DATA';        
 DEFINE OPERATOR READER_T
        DESCRIPTION 'export'
        TYPE EXPORT
        SCHEMA SCHEMA_EMP
        ATTRIBUTES(
                VARCHAR SOURCETDPID             = @SOURCETDPID,
                VARCHAR UserName                = @SourceUserName,
                VARCHAR UserPassword            = @SourceUserPassword,
                VARCHAR WorkingDatabase         = @SourceWorkingDatabase,
                VARCHAR SelectStmt              = @ExportSelectStmt		

              );


       DEFINE OPERATOR WRITER
        DESCRIPTION 'writte to hdfs'
        TYPE DATACONNECTOR CONSUMER
        SCHEMA SCHEMA_EMP
        ATTRIBUTES(

                VARCHAR HadoopHost              = @FileWriterHadoopHost,                  
                VARCHAR HadoopJobType           = @FileWriterHadoopJobType,
                VARCHAR HadoopSeparator         = @FileWriterHadoopSeparator,
                VARCHAR HadoopTargetPaths       = @FileWriterHadoopTargetPaths,
                VARCHAR HadoopUser              = @FileWriterHadoopUser,
                INTEGER HadoopNumMappers        = @FileWriterHadoopNumMappers
        );

        APPLY TO OPERATOR (
                WRITER[1]
        )

        SELECT * FROM OPERATOR (
                READER_T[1]
        );


);
```
* Archivo de Parámetros

```
SOURCETDPID                     = 'XXXX'
SourceUserName                  = 'XXXX'
SourceUserPassword              = 'XXXX'
SourceWorkingDatabase           = 'XXXX'
ExportSelectStmt                = 'select * from XXXX;'

FileWriterHadoopHost            = 'hostname'
FileWriterHadoopJobType         = 'hdfs'
FileWriterHadoopSeparator       = ','                
TargetOpenMode                  = 'Write'
FileWriterHadoopTargetPaths     = 'hdfs://hostname:8020/landing_zone/teradata/'
FileWriterHadoopUser            = 'hadoop'
FileWriterHadoopNumMappers      = 1
```
* ejecutamos el job de TPT
```
tbuild -f job -v archivo de parametros
```
* Renombramos el archivo para que el  formato sea csv
```
hdfs dfs -mv /landing_zone/teradata/part-m-00000 /landing_zone/teradata/file.csv
```
