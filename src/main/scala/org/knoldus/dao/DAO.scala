package org.knoldus.dao

import org.knoldus.model.{TaskModel, TaskRow}

import java.util.UUID
import scala.concurrent.Future

trait DAO {

  def add(task:TaskModel):Future[Int]
  def getAll:Future[Seq[TaskRow]]
  def getById(id:UUID): Future[Seq[TaskRow]]
  def getByStatus(status:Boolean): Future[Seq[TaskRow]]
  def markStatus(id:UUID, status:Boolean):Future[Int]
  def updateTask(id:UUID, updatedTask:TaskModel):Future[Int]
  def deleteById(id:UUID):Future[Int]
  def deleteAll():Future[Int]
  def getTaskByPagination(limit:Int, skip:Int):Future[Seq[TaskRow]]

}
