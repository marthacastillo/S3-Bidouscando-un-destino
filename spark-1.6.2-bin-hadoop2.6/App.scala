//package com.myapp2.kafka

import org.apache.spark._
import com.databricks.spark.csv._
import com.databricks.spark.avro._
import org.apache.spark.sql.SQLContext
import java.util.Properties
import org.apache.kafka.clients.producer._
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.regexp_replace

//object App {
  //def main(args: Array[String]){
    //val logFile = "$SPARK_HOME/README.md" // Should be some file on your system
    //val conf = new SparkConf().setAppName("Spark Application1").setMaster("local[2]").set("spark.executor.memory","1g")
    //val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._


// Se define el esquema
val customSchema = StructType(Array(
  StructField("type",StringType,true),
  StructField("freq",StringType,true),
  StructField("px",StringType,true),
  StructField("r",IntegerType,true),
  StructField("rDesc",StringType,true),
  StructField("ps",IntegerType,true),
  StructField("TotalRecords",IntegerType,true),
  StructField("isOriginal",DoubleType,true),
  StructField("publicationDate",StringType,true),
  StructField("isPartnerDetail",IntegerType,true)))
// se carga el dataset por recuperar
val dfpr = (sqlContext.read
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .option("header", "false")
      .schema(customSchema)
      .load("hdfs://TDExpress16:8020/landing_zone/por_recuperar/part-00000"))
//Se imprimen los datos
println("esquema de los por recuperar: \n" )
dfpr.printSchema
println("número de registgros de los por recuperar: \n" )
dfpr.count
println("los registros por recuperar: \n" )
dfpr.show

//Transforming   values with errors in publicationDate with regexp_replace
val dfpr2=dfpr.withColumn("publicationDate", regexp_replace(dfpr("publicationDate"),"([0-9]{2})-([0-9]{2})-([0-9]{4})T([0-9]{2}):([0-9]{2}):([0-9]{2})","$3-$2-$1T$4:$5:$6"))

    //Dataframe with not recovery values
val dfnr = dfpr2.filter(!$"publicationDate".rlike("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0]{2}):([0]{2}):([0]{2})"))

    //Saving not recovery values
dfnr.write.format("com.databricks.spark.csv").save("hdfs://TDExpress16:8020/landing_zone/no_recuperados")

//Generating a new dataframe with recovery values
val rs2 = dfpr2.filter($"publicationDate".rlike("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0]{2}):([0]{2}):([0]{2})"))

println("esquema de los recuperarados: \n")
rs2.printSchema

println("registros  recuperarados: \n" )
rs2.show


//Loading the dataframe with no appended records
val  df1=(sqlContext.read.avro("hdfs://TDExpress16:8020/raw/data.avro"))



println("Registros del archivo avro sin los registros recuperados: \n")
df1.count

// append the recovery rows to avro dataframe
//Asignin to a val the number of recovery values
val rs2_1= rs2.count
rs2.write.mode("append").avro("hdfs://TDExpress16:8020/raw/data.avro")

//Asigning to a val the number of not recovery values
val nrv= dfpr2.count - rs2_1

//Printing values
// Loading the dataframe with appended records
val  df2=(sqlContext.read.avro("hdfs://TDExpress16:8020/raw/data.avro"))
println("Registros del archivo avro con los registros recuperados: \n")
df2.count

//Enviando las metricas a kafka
val props = new Properties()

props.put("bootstrap.servers","localhost:9092")
props.put("acks","1")
props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
val producer = new KafkaProducer[String, String](props)

val topic = "test"

val cadena = " Registros recuperados: " + rs2_1 + "\n"  + "Registros no recuperados: " + nrv + "\n"
val record = new ProducerRecord(topic, Integer.toString(1),cadena)

    

producer.send(record)

producer.close()

  //}

//}
