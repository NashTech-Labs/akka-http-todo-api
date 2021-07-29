package org.knoldus.model

import java.util.UUID

case class TaskRow(id:UUID, title:String, description:String, isCompleted:Boolean)
