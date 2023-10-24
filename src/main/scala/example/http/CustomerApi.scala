package example.http

import zio._
import zio.dynamodb.{DynamoDBExecutor, batchWriteFromStream}
import example.dynamodblocal.DynamoDB
import example.dynamodblocal.DynamoDB.dynamoDBExecutorLayer
import example.repository.CustomerRepository
import zio.http._
import zio.{Console, ZIOAppDefault}

// Define the CustomerApi trait
trait CustomerApi {
  def httpApp: Http[Any, Throwable, Request, Response]
}

  object CustomerApi {
    lazy val live: ZLayer[CustomerRepository, Nothing, CustomerApi] = ZLayer {
        for {
          customerRepo <- ZIO.service[CustomerRepository]
        } yield CustomerApiLive(customerRepo)
      }
  }