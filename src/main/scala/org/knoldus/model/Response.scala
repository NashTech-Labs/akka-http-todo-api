package org.knoldus.model

trait Response

case class Failed(message:String) extends Response
case class Successful(message:String) extends Response
case class JsonResponse(message : String, status : Boolean)