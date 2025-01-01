package models

case class PhoneNumber(
  countryCode: Int,
  number: Long
)
object PhoneNumber {
  extension (phoneNumber: PhoneNumber) {
    def toString: String = {
      s"${phoneNumber.countryCode}-${phoneNumber.number}"
    }
  }

  def fromString(phoneNumberString: String): PhoneNumber = {
    val Array(countryCode, number) = phoneNumberString.split("-")
    PhoneNumber(countryCode.toInt, number.toLong)
  }
}
