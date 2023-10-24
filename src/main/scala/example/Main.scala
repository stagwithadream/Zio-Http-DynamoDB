package example

import zio._
import zio.Console.printLine
import zio.http._
import example.http.CustomerApi
import zio.aws.core.config
import zio.aws.{dynamodb, netty}
import zio.dynamodb.DynamoDBQuery.{get, put}
import zio.dynamodb.DynamoDBExecutor
import zio.schema.{DeriveSchema, Schema}
import zio.ZIOAppDefault
import zio.dynamodb.ProjectionExpression
import zio.dynamodb.DynamoDBQuery.put
import zio.dynamodb._
import example.dynamodblocal.DynamoDB._
import example.model._
import example.repository.CustomerRepository
import zio.stream.ZStream
import zio.{Console, ZIOAppDefault}

object Main extends ZIOAppDefault {

  val app = ZIO
    .serviceWithZIO[CustomerApi](customerApi => Server.serve(customerApi. httpApp.withDefaultErrorResponse))
    .provide(
      Server.defaultWithPort(8080),
      CustomerApi.live,
      dynamoDBExecutorLayer,
      CustomerRepository.live,

    )
  override def run: URIO[Any, ExitCode] = app.exitCode
}