package org.knoldus.services

import org.knoldus.dao.DAO
import org.knoldus.model.{Failed, Response, Successful, TaskModel, TaskRow}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TaskService(userDb: DAO) {

  def add(details:TaskModel):Future[Response] ={
    userDb.add(details).map{
      case 1 => Successful("Task has been added")
      case 0 => Failed("Task couldn't be added")
    }
  }

  def getAll: Future[Seq[TaskRow]] ={
    userDb.getAll
  }

  def getById(id:UUID): Future[Seq[TaskRow]] ={
    userDb.getById(id)
  }

  def getTaskByStatus(status:Boolean): Future[Seq[TaskRow]] ={
    userDb.getByStatus(status)
  }
  def markTaskStatus(id:UUID, status:Boolean): Future[Response] ={
    userDb.markStatus(id, status).map{
      case 1 => Successful("Task has been marked")
      case 0 => Failed("Task couldn't be marked")
    }
  }

  def updateTask(id:UUID, updatedTask:TaskModel): Future[Response] ={
    userDb.updateTask(id,updatedTask).map{
      case 1 => Successful("Task has been updated")
      case 0 => Failed("Task couldn't be updated")
    }
  }

  def deleteTaskByID(id:UUID): Future[Response] ={
    userDb.deleteById(id).map{
      case 1 => Successful("Task has been deleted")
      case 0 => Failed("Task couldn't be deleted")
    }
  }

  def deleteAll(): Future[Response] ={
    val result = userDb.deleteAll()
      result.onComplete{
          case Success(value) => println(s"Success: $value")
          case Failure(exception) => println(s"Failure : $exception")
        }
      result.map{

      case 0 => Failed("Tasks couldn't be deleted")
      case _ => Successful("Tasks has been deleted")
    }

  }

  def getTaskByPagination(limit:Int, skip:Int): Future[Seq[TaskRow]] ={
    userDb.getTaskByPagination(limit, skip)
  }


}
