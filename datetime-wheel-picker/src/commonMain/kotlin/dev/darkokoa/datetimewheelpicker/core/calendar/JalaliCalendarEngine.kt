package dev.darkokoa.datetimewheelpicker.core.calendar

import androidx.compose.ui.text.intl.Locale
import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.core.resolveStrings
import dev.darkokoa.datetimewheelpicker.core.calendar.jalaliMonthName
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

/**
 * Jalali (Persian/Solar Hijri) calendar engine.
 *
 * All leap-year and conversion logic is delegated to PersianDate via the
 * [jalaliDaysInMonth], [jalaliIsLeapYear], [jalaliToGregorian] and
 * [gregorianToJalali] expect/actual functions. Month names are resolved from the
 * localised `Strings` (e.g. `FaStrings` provides فروردین … اسفند).
 */
internal class JalaliCalendarEngine(
  private val dateFormatter: DateFormatter,
) : CalendarEngine {
  override val calendarType: CalendarType = CalendarType.Jalali

  override fun daysInMonth(year: Int, month: Int): Int {
    require(month in 1..12) { "Invalid Jalali month number: $month" }
    return jalaliDaysInMonth(year, month)
  }

  override fun isLeapYear(year: Int): Boolean = jalaliIsLeapYear(year)

  override fun monthName(month: Int, displayStyle: MonthDisplayStyle): String {
    if (displayStyle == MonthDisplayStyle.NUMERIC) return dateFormatter.formatDay(month)
    val strings = Locale("fa").resolveStrings()
    return jalaliMonthName(strings, month, displayStyle)
  }

  override fun formatYear(year: Int): String = dateFormatter.formatYear(year)

  override fun formatDay(day: Int): String = dateFormatter.formatDay(day)

  override fun resolvePreviousValid(year: Int, month: Int, day: Int): Triple<Int, Int, Int> {
    require(month in 1..12) { "Invalid Jalali month number: $month" }
    require(day >= 1) { "Jalali day must be >= 1, was $day" }
    val maxDay = daysInMonth(year, month)
    val clampedDay = minOf(day, maxDay)
    return Triple(year, month, clampedDay)
  }

  override fun fromGregorian(localDate: LocalDate): Triple<Int, Int, Int>? {
    val result = gregorianToJalali(localDate.year, localDate.month.number, localDate.day)
    return result?.let { (y, m, d) -> Triple(y, m, d) }
  }

  override fun toGregorian(year: Int, month: Int, day: Int): LocalDate? {
    if (month !in 1..12 || day !in 1..daysInMonth(year, month)) return null
    val result = jalaliToGregorian(year, month, day)
    return result?.let { (y, m, d) -> LocalDate(y, m, d) }
  }
}
