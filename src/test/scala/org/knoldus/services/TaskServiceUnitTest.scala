package org.knoldus.services
import org.knoldus.db.MysqlDB
import org.knoldus.model.{Failed, Successful, TaskModel, TaskRow}
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TaskServiceUnitTest extends AnyFlatSpec with ScalaFutures with Matchers {

  val mockDB: MysqlDB = mock[MysqlDB]
  val validTask: TaskModel = TaskModel("Title","Description")
  val taskId: UUID = UUID.randomUUID()
  val taskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = false)
  val completedTaskRow: TaskRow = TaskRow(taskId, "title","description", isCompleted = true)
  val taskService = new TaskService(mockDB)

  "add" should "return a successful " in {
    when(mockDB.add(validTask)) thenReturn Future(1)
    whenReady(taskService.add(validTask)){
      response => response shouldBe Successful("Task has been added")
    }
//    val a = taskService.add(validTask)
//    assert(a == Future(Successful("Task has been added")))

  }
  it should  "not add a task" in {
    when(mockDB.add(validTask)) thenReturn Future(0)
    whenReady(taskService.add(validTask)){
      response => response shouldBe Failed("Task couldn't be added")
    }
  }

  "getAll" should "return list of Task" in {
    when(mockDB.getAll) thenReturn Future(Seq(taskRow))
    whenReady(taskService.getAll){
      response => response shouldBe List(taskRow)
    }
  }

  it should "return an empty list of Task" in {
    when(mockDB.getAll) thenReturn Future(Seq.empty[TaskRow])
    whenReady(taskService.getAll){
      response => response shouldBe List.empty[TaskRow]
    }
  }

  "GetBYId" should "return List of Task" in {
    when(mockDB.getById(taskId)) thenReturn Future(Seq(taskRow))
    whenReady(taskService.getById(taskId)){
      response => response shouldBe List(taskRow)
    }
  }

  it should "not return Task" in {
    when(mockDB.getById(taskId)) thenReturn Future(Seq.empty[TaskRow])
    whenReady(taskService.getById(taskId)){
      response => response shouldBe List.empty[TaskRow]
    }
  }

  "getTaskByStatus" should "return List of Tasks marked as false" in {
    when(mockDB.getByStatus(false)) thenReturn Future(Seq(taskRow))
    whenReady(taskService.getTaskByStatus(false)){
      response => response shouldBe List(taskRow)
    }
  }

  it should "return List of Tasks marked as true" in {
    when(mockDB.getByStatus(true)) thenReturn Future(Seq(completedTaskRow))
    whenReady(taskService.getTaskByStatus(true)){
      response => response shouldBe List(completedTaskRow)
    }
  }

  "markTaskStatus" should "return Successful as marked as true" in {
    when(mockDB.markStatus(taskId, status = true)) thenReturn Future(1)
    whenReady(taskService.markTaskStatus(taskId, status = true)){
      response => response shouldBe Successful("Task has been marked")
    }
  }

  it should "return Successful as marked as false" in {
    when(mockDB.markStatus(taskId, status = false)) thenReturn Future(1)
    whenReady(taskService.markTaskStatus(taskId, status = false)){
      response => response shouldBe Successful("Task has been marked")
    }
  }

  it should "return Failed" in {
    when(mockDB.markStatus(taskId, status = false)) thenReturn Future(0)
    whenReady(taskService.markTaskStatus(taskId, status = false)){
      response => response shouldBe Failed("Task couldn't be marked")
    }
  }

  "updateTask" should "return Successful" in {
    when(mockDB.updateTask(taskId, validTask)) thenReturn Future(1)
    whenReady(taskService.updateTask(taskId, validTask)){
      response => response shouldBe Successful("Task has been updated")
    }
  }

  it should "return Failed" in {
    when(mockDB.updateTask(taskId, validTask)) thenReturn Future(0)
    whenReady(taskService.updateTask(taskId, validTask)){
      response => response shouldBe Failed("Task couldn't be updated")
    }
  }

  "deleteTaskByID" should "return Successful" in {
    when(mockDB.deleteById(taskId)) thenReturn Future(1)
    whenReady(taskService.deleteTaskByID(taskId)){
      response => response shouldBe Successful("Task has been deleted")
    }
  }

  it should "return Failed" in {
    when(mockDB.deleteById(taskId)) thenReturn Future(0)
    whenReady(taskService.deleteTaskByID(taskId)){
      response => response shouldBe Failed("Task couldn't be deleted")
    }
  }

  "deleteAll" should "return Successful" in {
    when(mockDB.deleteAll()) thenReturn Future(1)
    whenReady(taskService.deleteAll()){
      response => response shouldBe Successful("Tasks has been deleted")
    }
  }

  it should "return Failed" in {
    when(mockDB.deleteAll()) thenReturn Future(0)
    whenReady(taskService.deleteAll()){
      response => response shouldBe Failed("Tasks couldn't be deleted")
    }
  }

  "getTaskByPagination" should "return List of Task" in {
    when(mockDB.getTaskByPagination(1,0)) thenReturn Future(Seq(taskRow))
    whenReady(taskService.getTaskByPagination(1,0)){
      response => response shouldBe List(taskRow)
    }
  }
}
