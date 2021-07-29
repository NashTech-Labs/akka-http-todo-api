package org.knoldus.main

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.knoldus.actor.TaskActor
import org.knoldus.config.DatabaseConnection
import org.knoldus.routes.HttpRoutes
import org.knoldus.services.TaskService

import scala.util.{Failure, Success}

object Driver extends App with HttpRoutes{

  implicit val system: ActorSystem = ActorSystem("TodoAPI")
  implicit val materialize:ActorMaterializer.type = ActorMaterializer
  import system.dispatcher

  val databaseConnection = new DatabaseConnection
  val taskService = new TaskService(databaseConnection.userDb)
  val actor = system.actorOf(Props(new TaskActor(taskService)),"Task")


  val binding = Http().newServerAt("localhost",8080).bindFlow(route(actor))
  binding.onComplete{
    case Success(binding)=>
      val address =binding.localAddress.getHostName
      val port = binding.localAddress.getPort
      system.log.info(s"Server is listening on http://$address:$port")
    case Failure(ex) =>
      system.log.info(s"Server could not be started : $ex")
      system.terminate()
  }

}
