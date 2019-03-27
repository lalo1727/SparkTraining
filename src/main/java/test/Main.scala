package test

import com.google.gson.{Gson, JsonParser}
import test.Modelo.DatosP

object Main {
  def main(args: Array[String]): Unit = {

    var persona2 = DatosP("Monse", "Hernandez", "Queretaro", "MenChaka")

    var a = "Monse|Hernandez|Queretaro|MenChaka"

    var datos = a.split('|')
    println(datos.length)

    var dir = DatosP(datos(0), datos(1), datos(2), datos(3))
    println("" +dir)

    var gson = new Gson()
    var json = gson.toJson(persona2)
    println(json)

    val parser = new JsonParser()
    val parse = parser.parse(json)
  }
}
