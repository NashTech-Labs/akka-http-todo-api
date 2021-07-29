package org.knoldus.db

import org.knoldus.dao.DAO
import org.knoldus.model.{TaskModel, TaskRow}
import slick.jdbc.MySQLProfile
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class MysqlDB(db: MySQLProfile.backend.DatabaseDef)(implicit ec: ExecutionContext) extends TableQuery(new Schema(_)) with DAO{
  override def add(task: TaskModel): Future[Int] = {
    db.run(this+=TaskRow(UUID.randomUUID(), task.title,task.description,isCompleted = false))
  }

  override def getAll: Future[Seq[TaskRow]] = db.run(this.result)

  override def getById(id: UUID): Future[Seq[TaskRow]] = {
    db.run(this.filter(_.taskId === id).result)
  }

  override def getByStatus(status: Boolean): Future[Seq[TaskRow]] ={
    db.run(this.filter(_.isComplete === status).result)
  }

  override def markStatus(id: UUID, status: Boolean): Future[Int] = {
    db.run(this.filter(_.taskId === id).map(task => task.isComplete).update(status))
  }

  override def updateTask(id: UUID, updatedTask: TaskModel): Future[Int] = {
    db.run(this.filter(_.taskId === id).map(task => (task.title,task.description)).update(updatedTask.title,updatedTask.description))
  }

  override def deleteById(id: UUID): Future[Int] = {
    db.run(this.filter(_.taskId === id).delete)
  }

  override def deleteAll(): Future[Int] = {
    db.run(this.delete)
  }

  override def getTaskByPagination(limit: Int, skip: Int): Future[Seq[TaskRow]] = {
    db.run(this.drop(skip).take(limit).result)
  }
}
