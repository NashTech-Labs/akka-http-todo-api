package org.knoldus.db

import org.knoldus.model.TaskRow
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

import java.util.UUID

class Schema(tag: Tag) extends Table[TaskRow](tag, "Todo_Details") {

  def taskId: Rep[UUID] = column[UUID]("TASK_ID", O.PrimaryKey)
  def title:Rep[String] = column[String]("TITLE")
  def description:Rep[String] = column[String]("DESCRIPTION")
  def isComplete:Rep[Boolean]  = column[Boolean]("IS_COMPLETE")
  def * : ProvenShape[TaskRow] = (taskId, title, description, isComplete) <> (TaskRow.tupled, TaskRow.unapply)
}
