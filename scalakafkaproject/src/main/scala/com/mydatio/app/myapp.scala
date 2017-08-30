package com.mydatio.app

import org.apache.spark.{SparkConf, SparkContext}
import com.databricks.spark.csv._
import com.databricks.spark.avro._
import org.apache.spark.sql.SQLContext
import java.util.Properties
import org.apache.kafka.clients.producer._
import org.apache.spark.sql.types._


object myapp {
  def main(args: Array[String]){
    //val logFile = "$SPARK_HOME/README.md" // Should be some file on your system
    val conf = new SparkConf().setAppName("Spark Application").setMaster("local[2]").set("spark.executor.memory","1g")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

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



    val df = sqlContext.read
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .option("header", "false")
      .schema(customSchema)
      .load("hdfs://TDExpress16/landing_zone/teradata/part-m-00000")

    df.printSchema()
    println("Total de registros: " +df.count())

    // Se filtran los registros que tienen los requisitos

    val aceptados= df.filter($"publicationDate".rlike("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0]{2}):([0]{2}):([0]{2})"))

    aceptados.show()

    val conteo_aceptados = aceptados.count()

    println("  Registros buenos: "+ conteo_aceptados)

    //Se guardan los registros buenos en HDFS en raw
    aceptados.write.avro("hdfs://TDExpress16/raw/data.avro")

    //Se filtran los registros que no tienen los requisitos
    val no_aceptados= df.filter(!$"publicationDate".rlike("([0-9]{4})-([0-9]{2})-([0-9]{2})T([0]{2}):([0]{2}):([0]{2})"))

    no_aceptados.show()
    val conteo_no_aceptados = no_aceptados.count()
    println("   Registros malos: " + conteo_no_aceptados)

    //Se guardan en HDFS

    no_aceptados.write.format("com.databricks.spark.csv").save("hdfs://TDExpress16/landing_zone/por_recuperar")

    // Kafka Producer
    val props = new Properties()
    props.put("bootstrap.servers","localhost:9092")
    props.put("acks","1")
    props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val topic = "test"
    val cadena = "Registros sin errores: "+ conteo_aceptados + " Registros con errores: "+ conteo_no_aceptados

    val record = new ProducerRecord(topic, Integer.toString(1),cadena)
    producer.send(record)

    producer.close()

  }

}

