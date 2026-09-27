package dev.darkokoa.datetimewheelpicker.core.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * A minimal Jalali (Persian/Solar Hijri) date value.
 *
 * @property year Jalali year, e.g. `1403`
 * @property month Jalali month number, 1–12 (Bahman, Esfand, …)
 * @property day Jalali day of month, 1–[daysInMonth]
 */
data class JalaliDate(
  val year: Int,
  val month: Int,
  val day: Int,
) {
  init {
    require(month in 1..12) { "Jalali month must be 1..12, was $month" }
    require(day in 1..31) { "Jalali day must be 1..31, was $day" }
  }

  /**
   * Whether PersianDate recognizes this date as valid, including Esfand's leap-year limit.
   * Jalali calculations are available on Android only.
   */
  fun isValid(): Boolean = day <= jalaliDaysInMonth(year, month)

  /**
   * Converts this date to Gregorian. Throws [IllegalArgumentException] for an invalid date.
   */
  fun toLocalDate(): LocalDate {
    require(isValid()) { "Invalid Jalali date: $year/$month/$day" }
    val (gregorianYear, gregorianMonth, gregorianDay) =
      checkNotNull(jalaliToGregorian(year, month, day)) { "Jalali conversion is unavailable" }
    return LocalDate(gregorianYear, gregorianMonth, gregorianDay)
  }

  companion object {
    /** Converts a Gregorian date using PersianDate. */
    fun from(localDate: LocalDate): JalaliDate {
      val (year, month, day) = checkNotNull(
        gregorianToJalali(localDate.year, localDate.month.number, localDate.day)
      ) { "Jalali conversion is unavailable" }
      return JalaliDate(year, month, day)
    }
  }
}
