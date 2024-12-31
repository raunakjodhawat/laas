package repository

import models.{PasswordResetOTP, PhoneNumber}
import slick.jdbc.PostgresProfile.api.*

import java.text.SimpleDateFormat

object RepositoryUtility {
  private val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  given phoneNumberType: BaseColumnType[Option[PhoneNumber]] =
    MappedColumnType.base[Option[PhoneNumber], String](
      _.map(p => s"${p.countryCode}-${p.number}").getOrElse(""),
      s => {
        val parts = s.split("-")
        if (parts.length == 2) Some(PhoneNumber(parts(0).toInt, parts(1).toLong))
        else None
      }
    )

  given passwordResetOptionType: BaseColumnType[Option[PasswordResetOTP]] =
    MappedColumnType.base[Option[PasswordResetOTP], String](
      _.map(p => s"${p.code}-${simpleDateFormat.format(p.expireAt)}").getOrElse(""),
      s => {
        val parts = s.split("-")
        if (parts.length == 2) Some(PasswordResetOTP(parts(0).toLong, simpleDateFormat.parse(parts(1))))
        else None
      }
    )
}
