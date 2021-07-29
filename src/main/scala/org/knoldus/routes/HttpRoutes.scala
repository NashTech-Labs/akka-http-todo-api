package org.knoldus.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import org.knoldus.actor.TaskActor.{AddTask, DeleteAllTask, DeleteTaskById, GetAllTask, GetTask, GetTaskByPagination, GetTaskByStatus, MarkStatus, UpdateTask}
import org.knoldus.marshaling.CustomJsonProtocol
import org.knoldus.model.{Failed, JsonResponse, Successful, TaskModel, TaskRow}

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

trait HttpRoutes extends SprayJsonSupport with CustomJsonProtocol{

  def route(actor:ActorRef) : Route ={
    implicit val timeout:Timeout = Timeout(2 seconds)

    /**
     *
     * POST - api/task - create task
     * GET - api/task - Get All Task
     * GET - api/task/X - Get task by ID
     * GET - api/task?id=X - same as above
     * GET - api/task?completed=true - Get completed task
     * GET - api/task?completed=false - Get Pending Task
     * GET - api/task?limit=2&skip=10
     * PUT - api/task/X?completed=true - Update status as completed
     * PUT - api/task/X?completed=true - Update status as uncompleted
     * PUT - api/task/X - Update task by ID
     * PUT - api/task?id=X - Same as above
     * DELETE - api/task/X - Delete task by ID
     * DELETE - api/task?id=X - Same as above
     * DELETE - api/task - Delete All Task
     *
     */

    val todoRoutes =
      pathPrefix("api" / "task"){
        post{
          entity(as[TaskModel]){ data=>
            complete(
              (actor ? AddTask(data)).map{
                case Successful(message:String) =>
                  (StatusCodes.OK,JsonResponse(message, status = true))

                case Failed(message:String) =>
                  (StatusCodes.BadRequest,JsonResponse(message, status = false))
              }
            )
          }
        } ~
          get{
              (path(JavaUUID) | parameter('id.as[UUID])){ taskId =>
                val result = (actor ? GetTask(taskId)).mapTo[Seq[TaskRow]]
                complete(StatusCodes.OK,result)

              } ~
              parameter('completed.as[Boolean]){ status =>
                val result = (actor ? GetTaskByStatus(status)).mapTo[Seq[TaskRow]]
                complete(StatusCodes.OK, result)

              } ~
                parameters('limit.as[Int], 'skip.as[Int]){ (limit,skip)=>
                  val result = (actor ? GetTaskByPagination(limit,skip)).mapTo[Seq[TaskRow]]
                  complete(result)


                }~
              pathEndOrSingleSlash {
                val result = (actor ? GetAllTask).mapTo[Seq[TaskRow]]
                complete(StatusCodes.OK, result)
              }
          } ~
          put{
            path(JavaUUID){ taskId=>
              parameter('completed.as[Boolean]){ status =>
                complete(
                  (actor ? MarkStatus(taskId,status)).map{
                    case Successful(message:String) => (StatusCodes.OK, JsonResponse(message, status = true))
                    case Failed(message:String) => (StatusCodes.BadRequest, JsonResponse(message, status = false))
                  }
                )
              }

            } ~
              (path(JavaUUID) | parameter('id.as[UUID])){ taskId =>
                entity(as[TaskModel]){ taskDetails =>
                  complete(
                    (actor ? UpdateTask(taskId, taskDetails)).map{
                      case Successful(message:String) =>
                        (StatusCodes.OK, JsonResponse(message, status = true))
                      case Failed(message:String) =>
                        (StatusCodes.BadRequest, JsonResponse(message, status = false))

                    }
                  )

                }

              }

          } ~
          delete{
            (path(JavaUUID) | parameter('id.as[UUID])){ taskId=>
              complete(
                (actor ? DeleteTaskById(taskId)).map{
                  case Successful(message:String) =>
                    (StatusCodes.OK, JsonResponse(message, status = true))
                  case Failed(message:String) =>
                    (StatusCodes.BadRequest, JsonResponse(message, status = false))
                }
              )

            }~
              pathEndOrSingleSlash{
                complete(
                  (actor ? DeleteAllTask).map{
                    case Successful(message:String) =>
                      (StatusCodes.OK, JsonResponse(message, status = true))
                    case Failed(message:String) =>
                      (StatusCodes.BadRequest, JsonResponse(message, status = false))
                  }
                )
              }
          }
      }
    todoRoutes
  }


}
