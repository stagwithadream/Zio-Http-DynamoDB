package example.http

import example.dynamodblocal.DynamoDB.dynamoDBExecutorLayer
import example.model.CustomerBody
import zio._
import zio.dynamodb.DynamoDBExecutor
import zio.http._
import zio.http.{Http, Method, Request, Response}
import zio.dynamodb.DynamoDBQuery.{put, putItem, succeed}
import zio.dynamodb.{DynamoDBExecutor, Item, PrimaryKey, TestDynamoDBExecutor}
import example.model.Customer
import zio.http.Status.{BadRequest, Created}
import zio.json._
import example.model.CustomerBody._
import example.repository.CustomerRepository

// Create a concrete implementation of CustomerApi
case class CustomerApiLive(customerRepo:CustomerRepository) extends CustomerApi {
  override def httpApp: Http[Any, Throwable, Request, Response] = Http.collectZIO[Request] {
    case Method.GET -> Root / "status" => ZIO.succeed(Response.text("Server Running: CustomerApiLive Active"))

    case req @ Method.PUT -> Root / "customer" =>
      req.body.asString.map(_.fromJson[CustomerBody]).flatMap {
        case Left(e) =>
          ZIO
            .logErrorCause(s"Failed to parse the input: $e", Cause.fail(e))
            .as(
              Response.status(BadRequest),
            )
        case Right(customerBody) =>
          customerRepo.createCustomer(Customer(
              customerBody.name,
              customerBody.email,
              customerBody.phoneNumber,
              customerBody.age,
              customerBody.phoneWork
            ))
            .provide(dynamoDBExecutorLayer)
            .catchAll(err => ZIO.succeed(Response.text(s"Error: ${err.getMessage}")))

      }

    case req @ Method.GET -> Root / "customer" / id =>
      customerRepo.getCustomer(id)
        .provide(dynamoDBExecutorLayer)
        .foldZIO(
        failure =>
          ZIO
            .logErrorCause(s"Failed to read customer", Cause.fail(failure))
            .as(Response.status(Status.NotFound)),
        customer => customer match{
          case Some(customer) => ZIO.logInfo(s"customer read: $id").as(Response.json(customer.toJson))
          case None => ZIO.succeed(Response.text("Customer not found"))
        }
      )

    case Method.GET -> Root / "customers" =>
      customerRepo.getAllCustomers()
      .provide(dynamoDBExecutorLayer)
        .foldZIO(
          failure =>
            ZIO
              .logErrorCause(s"Failed to read customer", Cause.fail(failure))
              .as(Response.status(Status.NotFound)),
          customerIterator => {
            val customerList = customerIterator.toList
            ZIO.succeed(Response.json(customerList.toJson))
          }
        )

    case Method.DELETE -> Root / "customer" / id =>
      customerRepo.deleteCustomer(id)
      .provide(dynamoDBExecutorLayer)
      .foldZIO(
        failure =>
          ZIO
            .logErrorCause(s"Failed to read customer", Cause.fail(failure))
            .as(Response.status(Status.NotFound)),
        customer => ZIO.logInfo(s"customer read: $id").as(customer)

      )

}

}