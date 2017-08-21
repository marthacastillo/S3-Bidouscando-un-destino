# Recuperar registros

Se realizo un script el cual recuperara los registros que no lograron ser exitosos en el primer procesamiento, para ser recuperados estos deben cumplir un filtro. Al finalizar las metricas deberan ser enviadas a Apache Kafka.

```scala
package com.bedatio.scala

import org.apache.spark.{SparkConf, SparkContext, rdd}
import com.databricks.spark.avro._
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.kafka.clients.producer._
import java.util.Properties
import java.io.FileNotFoundException
import java.io.FileNotFoundException
import java.io.IOException


object RecoveryData {

  def getData(dataDir: String): Unit = {
    try {

      val conf = new SparkConf().setAppName("Spark Application").setMaster("local[2]").set("spark.executor.memory", "1g")
      val sc = new SparkContext(conf)
      val sqlContext = SQLContextSingleton.getInstance(sc)
      import sqlContext.implicits._

      // LEEMOS EL SCHEMA DE RAW EN FORMATO AVRO
      val recoverDF = sqlContext.read.avro(dataDir)
      println("Comproando Schema: ")
      recoverDF.printSchema()

      val filterRecover = recoverDF.filter($"type".rlike("([A-Za-z]*)-([0-9]{2,4})"))
      val countRecover = filterRecover.count()
      val filterBadRecover = recoverDF.filter(!$"type".rlike("([A-Za-z]*)-([0-9]{2,4})"))
      val countBadRecover = filterBadRecover.count()
      println("Registros recuperados: ")
      filterRecover.show()

      var datadir: String = "hdfs://TDExpress16:8020/master/data.parquet"
      appendToData(filterRecover, datadir)
      var topicName: String = "kafkatopic1"
      var messageTopic: String = "Proceso de recuoperacion: Registros recuperadps: " + countRecover + " Registros con errores: " + countBadRecover
      var kafkaServer: String = "localhost:9092"
      sendToTopic(kafkaServer, topicName, messageTopic)
    } catch {
      case ex: FileNotFoundException => {
        println("Missing file exception")
      }
      case ex: IOException => {
        println("IO Exception")
      }
    }
  }

  def appendToData(dataFrame: DataFrame, dataDir: String): Unit ={
    try {
      dataFrame.write.mode("append").parquet(dataDir)
    }catch {
      case ex: FileNotFoundException =>{
        println("Missing file exception: "+ex)
      }
      case ex: IOException => {
        println("IOException: "+ex)
      }
    }
  }

  def sendToTopic(host: String ,topic: String, message: String) : Unit = {

    //Kafka Ṕroducer
    try {
      val props = new Properties()
      props.put("bootstrap.servers", host)
      props.put("acks", "1")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      val producer = new KafkaProducer[String, String](props)
      val record = new ProducerRecord(topic, Integer.toString(1), message)
      producer.send(record)
      producer.close()

    } catch {
      case ex: IOException => {
        println("IOException: " + ex)
      }
    }
  }

  def main(args: Array[String]){
    var avroFile: String = "hdfs://TDExpress16:8020/raw/por_recuperar/bad_data.avro"
    getData(avroFile)
  }

}

object SQLContextSingleton{
  @transient  private var instance: SQLContext = _

  def getInstance(sparkContext: SparkContext): SQLContext = {
    if (instance == null) {
      instance = new SQLContext(sparkContext)
    }
    instance
  }
}

```
