package test

import java.io.{BufferedReader, InputStreamReader}
import java.math.BigInteger
import java.security.MessageDigest
import test.Modelo.DatosP
import com.google.gson.Gson
import com.jcraft.jsch.{ChannelSftp, JSch}
import org.slf4j.LoggerFactory

object TestFTP  {
  def main(args: Array[String]): Unit = {
    import java.util.Properties

    import org.apache.kafka.clients.producer._

    val  props = new Properties()
    props.put("bootstrap.servers", "10.15.191.240:9092")

    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)

    val TOPIC="test"

    // Variables de conexión al FileSystem
    val logger = LoggerFactory.getLogger(getClass)
    val user = "root"
    val host = "10.15.191.240"
    val password = "root"
    val folderFiles = "/root/kafka/hadoop/"

    // Variables y configuración para la conexión con la biblioteca
    val jsch = new JSch()
    val session = jsch.getSession(user, host, 22)
    session.setPassword(password)
    session.setConfig( "StrictHostKeyChecking", "no" )
    session.connect()

    //Gson
    // val gson = new Gson()

    // Variables de Apache Spark
    //val conf = new SparkConf().setAppName("collect").setMaster("local[1]")
    //val sc = new SparkContext(conf)

    if(session.isConnected)
      logger.info(s"Connected to server: $host ... \n")

    val sftp = session.openChannel("sftp")
    sftp.connect()

    if(sftp.isConnected)
      logger.info("Connected to sftp channel ... \n")
    else
      logger.warn("Not connected to sftp channel ... \n")

    // Se leen los archivos del directorio
    var channelSftp : ChannelSftp = null
    channelSftp = sftp.asInstanceOf[ChannelSftp]
    var vector = channelSftp.ls(folderFiles)

    // Se hace un barrido del directorio e imprime los ficheros existentes
    for(i <- 0 to vector.size() - 1){
      var file = vector.get(i).asInstanceOf[ChannelSftp#LsEntry]
      if(!file.getAttrs.isDir)
        println(file.getFilename)
    }
    println("\n")

    // Se lee un archivo del directorio
    //val stream = sftp.asInstanceOf[ChannelSftp].get(folderFiles + "book7.xlsx")
    val stream = sftp.asInstanceOf[ChannelSftp].get(folderFiles + "JECE.txt")
    val br = new BufferedReader(new InputStreamReader(stream))

    // Variables dentro del recorrido del archivo
    var line: String = null
    var num_line = 1
    var start_flag = "CONTROLRET"
    var rdds = scala.collection.mutable.ListBuffer.empty[(String, Int, String, String)]

    //////////////////////////////////////////////////////
    while ({line = br.readLine; line != null}) {
      //   println(line)
      //   var uno = "ola,cosa,loca,tres"

      var lineDos = line.split('|')

      //println(lineDos.length)
      println(lineDos)
      var persona = DatosP(lineDos(0),lineDos(1),lineDos(2),lineDos(3))
      println(persona)
      val gson = new Gson()
      val json = gson.toJson(persona)
      println(json)
      val record = new ProducerRecord(TOPIC, "key", s"$json")
      producer.send(record)
    }
    /////////////////////////////////////////////////////////

    // Se va leyendo el archivo linea por linea
    while ({line = br.readLine; line != null}) {
      val data = line.split('|')
      println(data.apply(2))
      if(data(0) == start_flag){
        if(!rdds.isEmpty){
          println("Archivo nuevo")
          println(rdds)
          //var rdd = sc.parallelize(rdds)
          // Antes de vaciar la lista de archivos, se va a almacenar en HIVE
          rdds.clear()
        }
      }
      val tuple1 = (hashString(data(0)), num_line, data(0), line)
      rdds += (tuple1)
      num_line = num_line + 1

    }


    br.close
    producer.close()

    sftp.disconnect()
    session.disconnect()
  }

  /***
    * Función que permite generar una clave hash con algun algoritmo
    * a partir de una cadena de entrada
    * @param s cadena de entrada para calcular una clave hash
    * @return clave hash calculada a partir de la cadena de entrada
    */
  def hashString(s: String): String = {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(s.getBytes)
    val bigInt = new BigInteger(1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }


  def insertData(): Unit = {

  }

}
