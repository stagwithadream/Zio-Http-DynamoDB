package example.model

import scala.util.Try
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import scala.annotation.tailrec

case class Customer(
                     name: String,
                     age: Int,
                     email: String,
                     phoneNumber: String,
                     phoneWork: Option[String],
                     _id: String
                   )

object Customer {
  def apply(
             name: String,
             email: String,
             phoneNumber: String,
             age: Int,
             workNumber: Option[String] = None,
             id: Option[String] = None
           ): Customer = {
        val actualId = id.getOrElse(java.util.UUID.randomUUID.toString)
        Customer(name, age, email, phoneNumber, workNumber, actualId)
  }

  def fromDetails(
                   name: String,
                   email: String,
                   phoneNumber: String,
                   age: Int,
                   workNumber: Option[String] = None,
                   id: Option[String] = None
                 ): Customer = {
    val actualId = id.getOrElse(java.util.UUID.randomUUID.toString)
    Customer(name, age, email, phoneNumber, workNumber, actualId)
  }

  object FieldNames {
    val name        = "name"
    val age         = "age"
    val id          = "_id"
    val email       = "email"
    val phoneWork   = "phoneWork"
    val phoneNumber = "phoneNumber"
  }


  implicit val customerEncoder: JsonEncoder[Customer] = DeriveJsonEncoder.gen[Customer]
}