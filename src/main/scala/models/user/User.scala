package models.user

import doobie._
import doobie.implicits._
import java.sql.Timestamp
import java.util.UUID

object User {
  val createUsersTable =
    sql"""
         |CREATE TABLE IF NOT EXISTS users(
         |  id UUID PRIMARY KEY,
         |  email TEXT NOT NULL UNIQUE,
         |  password_hash TEXT NOT NULL,
         |  password_algo TEXT NOT NULL,
         |  created_at TIMESTAMP NOT NULL,
         |  updated_at TIMESTAMP NOT NULL
         |)
         |""".stripMargin
}
case class User(
  id: UUID,
  email: String,
  password_hash: String,
  password_algo: String,
  created_at: Timestamp,
  updated_at: Timestamp
)
