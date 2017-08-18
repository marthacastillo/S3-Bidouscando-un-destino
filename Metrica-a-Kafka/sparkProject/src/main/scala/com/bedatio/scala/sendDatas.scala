package com.bedatio.scala

import org.apache.spark.{SparkConf, SparkContext}
import com.databricks.spark.avro._
import org.apache.spark.sql.SQLContext
import java.util.Properties
import org.apache.kafka.clients.producer._


object sendDatas {

  def filterData(dataDir: String): String ={
    val conf = new SparkConf().setAppName("Spark Application").setMaster("local[2]").set("spark.executor.memory","1g")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val avroDF = sqlContext.read.avro(dataDir)
    avroDF.printSchema()
    avroDF.show()
    println("Total de registros: " +avroDF.count())
    val filterBadType = avroDF.filter($"type".rlike("[0-9-a-z0-9_]"))
    filterBadType.show()
    val countBad = filterBadType.count()
    println("Registros con errores: "+ countBad)
    val filterGoodType = avroDF.filter(!$"type".rlike("[0-9-a-z0-9_]"))
    filterGoodType.show()
    val countGood = filterGoodType.count()
    println("Registros sin errores: " + countGood)
    //val filtString = df.filter($"")

    filterGoodType.write.parquet("hdfs://TDExpress16:8020/master/data.parquet")
    filterBadType.write.avro("hdfs://TDExpress16:8020/raw/por_recuperar/bad_data.avro")

    val kafkaServer: String = "localhost:9092"
    val topic = "kafkatopic1"
    val topicMessage = "Registros sin errores: "+countGood + " Registros con errores: "+ countBad

    sendToTopic(kafkaServer,topic,topicMessage)

  }

  def main(args: Array[String]){
    val dataDir = "hdfs://TDExpress16:8020/raw/data.avro"
    filterData(dataDir)
  }

  def sendToTopic(host: String ,topic: String, message: String) : String = {

    //Kafka Ṕroducer
    val props = new Properties()
    props.put("bootstrap.servers",host)
    props.put("acks","1")
    props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val record = new ProducerRecord(topic, Integer.toString(1),message)
    producer.send(record)
    producer.close()

    message

  }

}
