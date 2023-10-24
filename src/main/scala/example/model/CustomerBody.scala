package example.model

import zio.json._

case class CustomerBody(
  name: String,
  age: Int,
  email: String,
  phoneNumber: String,
  phoneWork: Option[String])

//  implicit val customerDecoder: JsonDecoder[CustomerBody] = DeriveJsonDecoder.gen[CustomerBody]
//  implicit val customerEncoder: JsonEncoder[CustomerBody] = DeriveJsonEncoder.gen[CustomerBody]
object CustomerBody {
  implicit val customerDecoder: JsonDecoder[CustomerBody] = DeriveJsonDecoder.gen[CustomerBody]
  implicit val customerEncoder: JsonEncoder[CustomerBody] = DeriveJsonEncoder.gen[CustomerBody]
}


