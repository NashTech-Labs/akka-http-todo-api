package org.knoldus.marshaling

import org.knoldus.model.{JsonResponse, TaskModel, TaskRow}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat, deserializationError}

import java.util.UUID

trait CustomJsonProtocol extends DefaultJsonProtocol{

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID): JsString = JsString(x.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(x) => UUID.fromString(x)
      case x => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit val TaskRowFormat: RootJsonFormat[TaskRow] = jsonFormat4(TaskRow)
  implicit val TaskModelFormat: RootJsonFormat[TaskModel] = jsonFormat2(TaskModel)
  implicit val JsonResponseFormat: RootJsonFormat[JsonResponse] = jsonFormat2(JsonResponse)

}
