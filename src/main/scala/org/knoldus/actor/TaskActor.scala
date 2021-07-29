package org.knoldus.actor

import akka.actor.{Actor, ActorLogging}
import org.knoldus.model.TaskModel
import org.knoldus.services.TaskService
import akka.pattern.pipe

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

object TaskActor{
  case class AddTask(taskDetails: TaskModel)
  case object GetAllTask
  case class GetTask(id:UUID)
  case class GetTaskByStatus(status:Boolean)
  case class MarkStatus(id:UUID, status:Boolean)
  case class UpdateTask(id:UUID, updatedTask:TaskModel)
  case class DeleteTaskById(id:UUID)
  case object DeleteAllTask
  case class GetTaskByPagination(limit:Int, skip:Int)
}

class TaskActor(taskService:TaskService) extends Actor with ActorLogging {
  import TaskActor._
  override def receive: Receive = {

    case AddTask(details:TaskModel) =>
      val result = taskService.add(details)
      result.pipeTo(sender())

    case GetAllTask =>
      val result = taskService.getAll
      result.pipeTo(sender())

    case GetTask(id:UUID) =>
      val result = taskService.getById(id)
      result.pipeTo(sender())

    case GetTaskByStatus(status:Boolean) =>
      val result = taskService.getTaskByStatus(status)
      result.pipeTo(sender())

    case MarkStatus(id:UUID, status:Boolean) =>
      val result = taskService.markTaskStatus(id,status)
      result.pipeTo(sender())

    case UpdateTask(id:UUID, task:TaskModel) =>
      val result = taskService.updateTask(id,task)
      result.pipeTo(sender())

    case DeleteTaskById(id:UUID)=>
      val result = taskService.deleteTaskByID(id)
      result.pipeTo(sender())

    case DeleteAllTask=>
      val result = taskService.deleteAll()
      result.pipeTo(sender())

    case GetTaskByPagination(limit:Int, skip:Int) =>
      val result = taskService.getTaskByPagination(limit,skip)
      result.pipeTo(sender())
  }
}
