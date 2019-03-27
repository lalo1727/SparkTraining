import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.{BasicDBList, BasicDBObject, DBCursor, DBObject}
import com.mongodb.casbah.{MongoClient, MongoDB}
import org.slf4j.LoggerFactory

object DriverMongo {

  var logger = LoggerFactory.getLogger(getClass)

  var host = "10.15.191.240"
  var port = 27017
  var db = "test"
  var collection = "CFDI_MALLADO2"
  var auth = "None"
  var user = "root"
  var pass = "root"

  var cliente: Option[MongoClient] = None
  var dbMongo: Option[MongoDB] = None

  def main(args: Array[String]): Unit = {
    println("Hola, mongo")

    val insumos = List("CFDFACTORAJEFACTURAS", "CFDCONFIRMINGFACTURAS")
    var doc = MongoDBObject(
      "categoria" -> "FACTURAS",
      "num_malla" -> 1,
      "descripcion" -> "FACTORAJE",
      "insumo" -> insumos
    )
    val insumos1 = List("CFDOPOPICS")
    var doc1 = MongoDBObject(
      "categoria" -> "FACTURAS",
      "num_malla" -> 2,
      "descripcion" -> "OPOPICS(TECNOLOGICAS)",
      "insumo" -> insumos1
    )
    val indices = List("categoria", "num_malla")

    if (conectar == 1) {
      logger.info("Se ha conectado a Mongo !!!")
    }

    if(crearColeccionIndices(collection, indices) == 1) { // Crea una nueva colección si no existe{}
      logger.info("Se ha creado la colección y sus indices correctamente")
    }

    if(insertarDocumento(collection, doc) == 1) { // Inserta un documento a la colección
      logger.info(s"Se ha insertado el documento en la colección $collection correctamente !!!")
    }

    if(insertarDocumento(collection, doc1) == 1) { // Inserta un documento a la colección
      logger.info(s"Se ha insertado el documento en la colección $collection correctamente !!!")
    }

    if (consultarInsumos(collection,1,"FACTURAS") == 1) {
      logger.info("Consulta exitosa !!!")
    }

    if (desconectar() == 1)
      logger.info("Se ha desconectado de MongoDB !!!")

  }

  def insertarDocumento(collection: String, documento: DBObject): Int = {

    if (cliente.isEmpty) {
      logger.warn("No se tiene una conexión activa")
      return 0
    }

    try {
      var col = dbMongo.get.getCollection(collection)
      col.insert(documento)
    } catch {
      case ex: Exception => {
        logger.error(s"Error al hacer la inserción de datos, $ex")
        return 0
      }
    }
    return 1
  }

  def crearColeccionIndices(collectionName: String, indices: List[String]): Int = {

    if (cliente.isEmpty) {
      logger.warn("No se tiene una conexión activa")
      return 0
    }

    try {
      if (!dbMongo.get.collectionExists(collectionName)) {
        dbMongo.get.createCollection(collectionName, new BasicDBObject)
        val coleccion = dbMongo.get.getCollection(collectionName)
        // Se van a crear los indices a la colección
        indices.foreach(indice =>
          coleccion.createIndex(indice)
        )
      } else
        logger.warn(s"La colección '$collectionName' ya existe")
    } catch {
      case ex: Exception => {
        logger.error(s"Error al crear la colección, $ex")
        return 0
      }
    }
    return 1
  }

  def consultarInsumos(coleccion: String, malla: Int, categoria: String): Int = {

    if (cliente.isEmpty) {
      logger.warn("No se tiene una conexión activa")
      return 0
    }

    try {
      val col = dbMongo.get.getCollection(coleccion)
      var res = col.findOne( MongoDBObject(
        "categoria" -> categoria,
        "num_malla" -> malla )
      )
      var insumos = res.toMap.get("insumo").asInstanceOf[BasicDBList]
      logger.info(s"Los insumos de la malla $malla, categoría $categoria son los siguientes: ")
      insumos.toArray().foreach(in => println(in))
      println()
    } catch {
      case ex: Exception => {
        println(s"Error al hacer consultas: $ex")
      }
    }
    return 1
  }

  def conectar: Int = {
    try {
      if (cliente.isEmpty) {
        cliente = Some(MongoClient(host, port))
        dbMongo = Some(cliente.get(db))
      }
    } catch {
      case ex: Exception => {
        logger.error(s"Error en la conexión a MongoDB: $ex")
        return 0
      }
    }
    return 1
  }

  def desconectar(): Int = {
    if (cliente.isEmpty)
      logger.warn("No se encuentra una conexión activa")
    0
    try {
      cliente.get.close()
    } catch {
      case ex: Exception => {
        logger.error(s"Error al cerrar la conexión de MongoDB: $ex")
        return 0
      }
    }
    cliente = None
    dbMongo = None
    return 1
  }

}