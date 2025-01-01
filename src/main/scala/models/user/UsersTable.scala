package models.user

import models.PasswordResetOTP
import repositories.RepositoryUtility.given
import slick.ast.TypedType
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.{ProvenShape, Tag}

class UsersTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[Option[String]]("username", O.Default(None))
  def email = column[String]("email", O.Unique)
  def salt = column[String]("salt")
  def passwordResetOTP = column[Option[PasswordResetOTP]]("password_reset_otp")
  def passwordHash = column[String]("password_hash")

  override def * : ProvenShape[User] = (
    id,
    username,
    email,
    salt,
    passwordResetOTP,
    passwordHash
  ).mapTo[User]
}

object UsersTable {
  def createPartialIndexes(db: Database): DBIO[Unit] = DBIO.seq(
    sqlu"""CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username_not_null ON users(username) WHERE username IS NOT NULL AND username <> ''"""
  )
}
