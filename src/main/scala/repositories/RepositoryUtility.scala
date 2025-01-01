package repositories

import models.{PasswordResetOTP, PhoneNumber}
import slick.jdbc.PostgresProfile.api.*
import utility.Utils.simpleDateFormat

object RepositoryUtility {
  given phoneNumberType: BaseColumnType[Option[PhoneNumber]] =
    MappedColumnType.base[Option[PhoneNumber], String](
      _.map(p => s"${p.countryCode}-${p.number}").getOrElse(""),
      s => Option(PhoneNumber.fromString(s))
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
