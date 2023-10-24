package example.repository

import example.dynamodblocal.DynamoDB.dynamoDBExecutorLayer
import zio.{ZIO, ZLayer}
import zio.dynamodb.DynamoDBQuery.putItem
import zio.dynamodb.{DynamoDBExecutor, Item}
import zio.http.Response
import example.model.Customer

trait CustomerRepository{
  def createCustomer(customer: Customer):ZIO[DynamoDBExecutor, Throwable, Response]
  def getCustomer(id: String):ZIO[DynamoDBExecutor, Throwable, Option[Customer]]
  def getAllCustomers():ZIO[DynamoDBExecutor, Throwable, Iterator[Customer]]
  def deleteCustomer(id: String):ZIO[DynamoDBExecutor, Throwable, Response]

}


object CustomerRepository {
  lazy val live: ZLayer[DynamoDBExecutor, Nothing, CustomerRepository] = ZLayer.fromFunction { dynamoDBExecutorLayer =>
    CustomerRepositoryLive(dynamoDBExecutorLayer)
  }
}




