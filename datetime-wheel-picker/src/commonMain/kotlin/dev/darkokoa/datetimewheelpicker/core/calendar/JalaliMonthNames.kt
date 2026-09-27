package dev.darkokoa.datetimewheelpicker.core.calendar

import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.strings.Strings

/**
 * Resolves the Jalali month name for [month] (1–12) from [strings], honouring the
 * requested [displayStyle].
 *
 * Jalali month numbering is 1 = فروردین … 12 = اسفند, which maps directly onto the
 * `jalaliMonth{N>Full>` / `jalaliMonth<N<Short>` fields in [Strings].
 */
internal fun jalaliMonthName(strings: Strings, month: Int, displayStyle: MonthDisplayStyle): String {
  val full = jalaliMonthFull(strings, month)
  val short = jalaliMonthShort(strings, month)
  return when (displayStyle) {
    MonthDisplayStyle.FULL -> full
    MonthDisplayStyle.SHORT -> short
    MonthDisplayStyle.NUMERIC -> month.toString()
  }
}

private fun jalaliMonthFull(strings: Strings, month: Int): String = when (month) {
  1 -> strings.jalaliMonth1Full
  2 -> strings.jalaliMonth2Full
  3 -> strings.jalaliMonth3Full
  4 -> strings.jalaliMonth4Full
  5 -> strings.jalaliMonth5Full
  6 -> strings.jalaliMonth6Full
  7 -> strings.jalaliMonth7Full
  8 -> strings.jalaliMonth8Full
  9 -> strings.jalaliMonth9Full
  10 -> strings.jalaliMonth10Full
  11 -> strings.jalaliMonth11Full
  12 -> strings.jalaliMonth12Full
  else -> error("Invalid Jalali month number: $month")
}

private fun jalaliMonthShort(strings: Strings, month: Int): String = when (month) {
  1 -> strings.jalaliMonth1Short
  2 -> strings.jalaliMonth2Short
  3 -> strings.jalaliMonth3Short
  4 -> strings.jalaliMonth4Short
  5 -> strings.jalaliMonth5Short
  6 -> strings.jalaliMonth6Short
  7 -> strings.jalaliMonth7Short
  8 -> strings.jalaliMonth8Short
  9 -> strings.jalaliMonth9Short
  10 -> strings.jalaliMonth10Short
  11 -> strings.jalaliMonth11Short
  12 -> strings.jalaliMonth12Short
  else -> error("Invalid Jalali month number: $month")
}
