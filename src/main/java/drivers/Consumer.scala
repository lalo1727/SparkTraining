
import java.util
import java.util.Properties

import com.google.gson.JsonParser
import org.apache.kafka.clients.consumer.KafkaConsumer
import test.Modelo.DatosP
import scala.collection.JavaConverters._


object Consumer {

  var datos = scala.collection.mutable.ArrayBuffer.empty[DatosP]

  def main(args: Array[String]): Unit = {


    val TOPIC = "test"

    val props = new Properties()
    props.put("bootstrap.servers", "10.15.191.240:9092")

    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "something")

    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(util.Collections.singletonList(TOPIC))

    while (true) {
      val records = consumer.poll(100)
      for (record <- records.asScala) {
        println(record.value())
        val parser = new JsonParser
        var parse2 = parser.parse(record.value()).getAsJsonObject
        println(parse2)

        println(parse2.get("nombre"))
        println(parse2.get("apellido"))
        println(parse2.get("ciudad"))
        println(parse2.get("calle"))

        val mongo = DriverMongo
        mongo.conectar


        var doc = DatosP(

          parse2.get("nombre").getAsString,
          parse2.get("apellido").getAsString,
          parse2.get("ciudad").getAsString,
          parse2.get("calle").getAsString

        )

        datos += doc


        val dataSet = Spark
        dataSet.RecibeArray(datos)
        mongo.desconectar()
      }
    }
  }
}