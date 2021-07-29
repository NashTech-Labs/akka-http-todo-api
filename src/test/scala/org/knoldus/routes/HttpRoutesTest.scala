package org.knoldus.routes

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.knoldus.actor.TaskActor
import org.knoldus.model.{JsonResponse, Successful, TaskModel, TaskRow}
import org.knoldus.services.TaskService
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.knoldus.marshaling.CustomJsonProtocol

import java.util.UUID
import scala.concurrent.Future

class HttpRoutesTest extends  AnyWordSpecLike with Matchers with ScalatestRouteTest with HttpRoutes with SprayJsonSupport with CustomJsonProtocol {

  val mockTaskService: TaskService = mock[TaskService]
  val actor:ActorRef = system.actorOf(Props(new TaskActor(mockTaskService)),"Task")


  val validTask: TaskModel = TaskModel("Title","Description")
  val taskId: UUID = UUID.randomUUID()
  val taskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = false)
  val completedTaskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = true)


  "TODO Api Backend" should{
    "return all the task with Status Codes OK" in{
      when(mockTaskService.getAll) thenReturn Future.successful(List(taskRow))
      Get("/api/task/") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(taskRow)
      }
    }

    "return task by ID with Status Codes OK if provided as path" in{
      when(mockTaskService.getById(taskId)) thenReturn Future.successful(List(taskRow))
      Get(s"/api/task/$taskId") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(taskRow)
      }
    }
    "return task by ID with Status Codes OK if provided as query parameter" in{
      when(mockTaskService.getById(taskId)) thenReturn Future.successful(List(taskRow))
      Get(s"/api/task?id=$taskId") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(taskRow)
      }
    }
    "return completed task with Status Codes OK" in{
      when(mockTaskService.getTaskByStatus(true)) thenReturn Future.successful(List(completedTaskRow))
      Get("/api/task?completed=true") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(completedTaskRow)
      }
    }

    "return pending task with Status Codes OK" in{
      when(mockTaskService.getTaskByStatus(false)) thenReturn Future.successful(List(taskRow))
      Get("/api/task?completed=false") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(taskRow)
      }
    }

    "return List of task with Status Codes OK as pagination data " in{
      when(mockTaskService.getTaskByPagination(0,1)) thenReturn Future.successful(List(taskRow))
      Get("/api/task?limit=0&skip=1") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[List[TaskRow]] shouldEqual List(taskRow)
      }
    }

    "return JsonResponse with Status Codes OK on mark a task as completed" in{
      when(mockTaskService.markTaskStatus(taskId,status = true)) thenReturn Future.successful(Successful("Task has been marked"))
      Put(s"/api/task/$taskId?completed=true") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Task has been marked",status = true)
      }
    }

    "return JsonResponse with Status Codes OK on update a task if task ID provided as path" in{
      when(mockTaskService.updateTask(taskId,validTask)) thenReturn Future.successful(Successful("Task has been updated"))
      Put(s"/api/task/$taskId", validTask) ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Task has been updated",status = true)
      }
    }

    "return JsonResponse with Status Codes OK on update a task if task ID provided as query parameter" in{
      when(mockTaskService.updateTask(taskId,validTask)) thenReturn Future.successful(Successful("Task has been updated"))
      Put(s"/api/task?id=$taskId", validTask) ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Task has been updated",status = true)
      }
    }

    "return JsonResponse with Status Codes OK on delete a task by id if task id provided as query parameter" in{
      when(mockTaskService.deleteTaskByID(taskId)) thenReturn Future.successful(Successful("Task has been deleted"))
      Delete(s"/api/task?id=$taskId") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Task has been deleted",status = true)
      }
    }

    "return JsonResponse with Status Codes OK on delete a task by id if task id provided as path" in{
      when(mockTaskService.deleteTaskByID(taskId)) thenReturn Future.successful(Successful("Task has been deleted"))
      Delete(s"/api/task/$taskId") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Task has been deleted",status = true)
      }
    }

    "return JsonResponse with Status Codes OK on delete all task" in{
      when(mockTaskService.deleteAll()) thenReturn Future.successful(Successful("Tasks has been deleted"))
      Delete(s"/api/task/") ~> route(actor) ~> check {
        status shouldEqual StatusCodes.OK
        entityAs[JsonResponse] shouldEqual JsonResponse("Tasks has been deleted",status = true)
      }
    }
  }
}
