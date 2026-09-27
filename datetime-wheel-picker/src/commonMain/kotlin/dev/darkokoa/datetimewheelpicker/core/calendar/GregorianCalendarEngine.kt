package dev.darkokoa.datetimewheelpicker.core.calendar

import dev.darkokoa.datetimewheelpicker.core.isLeapYear
import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

/**
 * Gregorian calendar engine backed by `kotlinx.datetime.LocalDate`.
 *
 * Month names are resolved through the same [DateFormatter] localisation that the
 * existing picker uses, keeping backwards compatibility with every locale Strings
 * file.
 */
internal class GregorianCalendarEngine(
  private val dateFormatter: DateFormatter,
) : CalendarEngine {
  override val calendarType: CalendarType = CalendarType.Gregorian

  override fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
      2 -> if (LocalDate(year, month, 1).isLeapYear) 29 else 28
      4, 6, 9, 11 -> 30
      1, 3, 5, 7, 8, 10, 12 -> 31
      else -> error("Invalid month number: $month")
    }
  }

  override fun isLeapYear(year: Int): Boolean = dev.darkokoa.datetimewheelpicker.core.isLeapYear(year)

  override fun monthName(month: Int, displayStyle: MonthDisplayStyle): String =
    dateFormatter.formatMonth(Month(month), displayStyle)

  override fun formatYear(year: Int): String = dateFormatter.formatYear(year)

  override fun formatDay(day: Int): String = dateFormatter.formatDay(day)

  override fun resolvePreviousValid(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
    val maxDay = daysInMonth(year, month)
    val clampedDay = minOf(day, maxDay)
    return Triple(year, month, clampedDay)
  }

  override fun fromGregorian(localDate: LocalDate): Triple<Int, Int, Int> =
    Triple(localDate.year, localDate.month.number, localDate.day)

  override fun toGregorian(year: Int, month: Int, day: Int): LocalDate =
    LocalDate(year, month, day)
}
