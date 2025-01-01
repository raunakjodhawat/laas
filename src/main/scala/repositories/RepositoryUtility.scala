package repositories

import models.PasswordResetOTP
import slick.jdbc.PostgresProfile.api.*
import utility.Utils.simpleDateFormat

object RepositoryUtility {

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
