package example.repository

import example.model.Customer
import zio.{ZIO, stream}
import zio.dynamodb.{DynamoDBExecutor, Item, PrimaryKey}
import zio.dynamodb.DynamoDBQuery.{getItem, putItem, scanAllItem, deleteItem}
import zio.dynamodb.FromAttributeValue.attrMapFromAttributeValue
import zio.http.Response
import zio.prelude.data.Optional.AllValuesAreNullable

case class CustomerRepositoryLive(dynamoDBExecutor: DynamoDBExecutor) extends CustomerRepository{

  def createCustomer(customer: Customer): ZIO[DynamoDBExecutor, Throwable, Response] = {
    val createLogic: ZIO[DynamoDBExecutor, Throwable, Unit] = for {
      _ <- putItem("customer", Item(
        "id" -> customer._id,
        "name" -> customer.name,
        "age" -> customer.age,
        "phone" -> customer.phoneNumber,
        "email" -> customer.email,
        "phoneWork" -> customer.phoneWork)).execute
    } yield ()

    createLogic.map(_ => Response.text("Customer created successfully"))
  }

  def getCustomer(id: String):ZIO[DynamoDBExecutor, Throwable, Option[Customer]] = {
    val fetchLogic : ZIO[DynamoDBExecutor, Throwable, Option[Item]] =
      getItem("customer", PrimaryKey("id" -> id)).execute

    fetchLogic.map {
      case Some(item) =>
        val _id: String = item.get[String]("id").fold(error => "", success => success.toString)
        val name: String = item.get[String]("name").fold(error => "", success => success.toString)
        val age: Int = item.get[Int]("age").getOrElse(0)
        val phoneNumber: String = item.get[String]("phone").fold(error => "", success => success.toString)
        val email: String = item.get[String]("email").fold(error => "", success => success.toString)
        val phoneWork: String = item.get[String]("phoneWork").fold(error => "", success => success.toString) // Convert optional attribute to Option[String]

        Some(Customer.fromDetails(name, email, phoneNumber, age, Some(phoneWork), Some(_id)))

      case None => None
    }

  }

  def itemToCustomer(itemOpt: Option[Item]): Option[Customer] = {
    itemOpt match {
      case Some(item) =>
        val _id: String = item.get[String]("id").fold(error => "", success => success.toString)
        val name: String = item.get[String]("name").fold(error => "", success => success.toString)
        val age: Int = item.get[Int]("age").getOrElse(0)
        val phoneNumber: String = item.get[String]("phone").fold(error => "", success => success.toString)
        val email: String = item.get[String]("email").fold(error => "", success => success.toString)
        val phoneWork: String = item.get[String]("phoneWork").fold(error => "", success => success.toString)
        // Convert optional attribute to Option[String]

        Some(Customer.fromDetails(name, email, phoneNumber, age, Some(phoneWork), Some(_id)))

      case None => None
    }
  }


  def getAllCustomers():ZIO[DynamoDBExecutor, Throwable, Iterator[Customer]] = {
    val fetchLogic: ZIO[DynamoDBExecutor, Throwable, stream.Stream[Throwable, Item]] =
      scanAllItem("customer").execute

    fetchLogic.flatMap { streamOfItems =>
      streamOfItems
        .map(item => itemToCustomer(Some(item)))
        .collect { case Some(customer) => customer } // Only take the Customers that exist.
        .runCollect // Collect the stream into a list
        .map(_.iterator) // Convert the list to an iterator
    }
    }

  def deleteCustomer(id: String):ZIO[DynamoDBExecutor, Throwable, Response] = {
    val deleteLogic : ZIO[DynamoDBExecutor, Throwable, Option[Item]] =
      deleteItem("customer", PrimaryKey("id" -> id)).execute

    deleteLogic.map(response => Response.text(s"Customer with $id is deleted"))
  }

}
