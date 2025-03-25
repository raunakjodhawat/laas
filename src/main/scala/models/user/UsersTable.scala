package models.user

import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def salt = column[String]("salt")
  def passwordHash = column[String]("password_hash")

  override def * : ProvenShape[User] = (
    id,
    username,
    salt,
    passwordHash
  ).mapTo[User]
}
