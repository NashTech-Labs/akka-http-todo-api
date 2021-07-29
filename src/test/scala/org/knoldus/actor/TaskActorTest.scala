package org.knoldus.actor

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.knoldus.actor.TaskActor.{AddTask, DeleteAllTask, DeleteTaskById, GetAllTask, GetTask, GetTaskByPagination, GetTaskByStatus, MarkStatus, UpdateTask}
import org.knoldus.model.{Failed, Successful, TaskModel, TaskRow}
import org.knoldus.services.TaskService
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.wordspec.AnyWordSpecLike

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskActorTest extends  TestKit(ActorSystem("TaskActor")) with AnyWordSpecLike with ImplicitSender {

  val mockTaskService: TaskService = mock[TaskService]
  val actor:ActorRef = system.actorOf(Props(new TaskActor(mockTaskService)),"Task")


  val validTask: TaskModel = TaskModel("Title","Description")
  val taskId: UUID = UUID.randomUUID()
  val taskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = false)
  val completedTaskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = true)



  "AddTask" should{

    "return Successful" in{
      when(mockTaskService.add(validTask)) thenReturn Future(Successful("Task has been added"))
      actor ! AddTask(validTask)
      expectMsg(Successful("Task has been added"))
    }

    "return Failed" in {
      when(mockTaskService.add(validTask)) thenReturn Future(Failed("Task couldn't be added"))
      actor ! AddTask(validTask)
      expectMsg(Failed("Task couldn't be added"))
    }
  }

  "GetAllTask" should{
    "return list of task" in {
      when(mockTaskService.getAll) thenReturn Future(Seq(taskRow))
      actor ! GetAllTask
      expectMsg(List(taskRow))
    }

    "return empty list" in {
      when(mockTaskService.getAll) thenReturn Future(Seq.empty[TaskRow])
      actor ! GetAllTask
      expectMsg(List.empty[TaskRow])
    }
  }

  "GetTask" should{
    "return list of task" in {
      when(mockTaskService.getById(taskId)) thenReturn Future(Seq(taskRow))
      actor ! GetTask(taskId)
      expectMsg(List(taskRow))
    }

    "return empty list" in {
      when(mockTaskService.getById(taskId)) thenReturn Future(Seq.empty[TaskRow])
      actor ! GetTask(taskId)
      expectMsg(List.empty[TaskRow])
    }
  }

  "GetTaskByStatus" should{
    "return list of completed task" in {
      when(mockTaskService.getTaskByStatus(true)) thenReturn Future(Seq(completedTaskRow))
      actor ! GetTaskByStatus(true)
      expectMsg(List(completedTaskRow))
    }

    "return list of pending task" in {
      when(mockTaskService.getTaskByStatus(false)) thenReturn Future(Seq(taskRow))
      actor ! GetTaskByStatus(false)
      expectMsg(List(taskRow))
    }

    "return empty list" in {
      when(mockTaskService.getTaskByStatus(true)) thenReturn Future(Seq.empty[TaskRow])
      actor ! GetTaskByStatus(true)
      expectMsg(List.empty[TaskRow])
    }
  }

  "MarkStatus" should{
    "return Successful" in {
      when(mockTaskService.markTaskStatus(taskId,status = true)) thenReturn Future(Successful("Task has been marked"))
      actor ! MarkStatus(taskId,status = true)
      expectMsg(Successful("Task has been marked"))
    }

    "return Failed" in {
      when(mockTaskService.markTaskStatus(taskId,status = true)) thenReturn Future(Failed("Task couldn't be marked"))
      actor ! MarkStatus(taskId,status = true)
      expectMsg(Failed("Task couldn't be marked"))
    }
  }

  "UpdateTask" should{
    "return Successful" in {
      when(mockTaskService.updateTask(taskId,validTask)) thenReturn Future(Successful("Task has been updated"))
      actor ! UpdateTask(taskId,validTask)
      expectMsg(Successful("Task has been updated"))
    }

    "return Failed" in {
      when(mockTaskService.updateTask(taskId,validTask)) thenReturn Future(Failed("Task couldn't be updated"))
      actor ! UpdateTask(taskId,validTask)
      expectMsg(Failed("Task couldn't be updated"))
    }
  }
  "DeleteTaskById" should{
    "return Successful" in {
      when(mockTaskService.deleteTaskByID(taskId)) thenReturn Future(Successful("Task has been deleted"))
      actor ! DeleteTaskById(taskId)
      expectMsg(Successful("Task has been deleted"))
    }

    "return Failed" in {
      when(mockTaskService.deleteTaskByID(taskId)) thenReturn Future(Failed("Task couldn't be deleted"))
      actor ! DeleteTaskById(taskId)
      expectMsg(Failed("Task couldn't be deleted"))
    }
  }

  "DeleteAll" should{
    "return Successful" in {
      when(mockTaskService.deleteAll()) thenReturn Future(Successful("Tasks has been deleted"))
      actor ! DeleteAllTask
      expectMsg(Successful("Tasks has been deleted"))
    }

    "return Failed" in {
      when(mockTaskService.deleteAll()) thenReturn Future(Failed("Tasks couldn't be deleted"))
      actor ! DeleteAllTask
      expectMsg(Failed("Tasks couldn't be deleted"))
    }
  }

  "GetTaskByPagination" should{
    "return list of Tasks" in {
      when(mockTaskService.getTaskByPagination(0,1)) thenReturn Future(Seq(taskRow))
      actor ! GetTaskByPagination(0,1)
      expectMsg(List(taskRow))
    }

    "return Failed" in {
      when(mockTaskService.getTaskByPagination(0,1)) thenReturn Future(Seq.empty[TaskRow])
      actor ! GetTaskByPagination(0,1)
      expectMsg(List.empty[TaskRow])
    }
  }

}
