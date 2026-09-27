package dev.darkokoa.datetimewheelpicker.core.calendar

import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import kotlinx.datetime.LocalDate

/**
 * Calendar-agnostic operations the wheel picker needs.
 *
 * Implementations:
 * - [GregorianCalendarEngine] — pure Kotlin, no external dependencies.
 * - [JalaliCalendarEngine] — delegates leap-year / conversion math to PersianDate on
 *   Android. On other platforms the factory throws.
 *
 * The picker works exclusively in [LocalDate] (Gregorian) for range checking and the
 * public callback API. Calendars that display in a non-Gregorian system (e.g. Jalali)
 * convert through this engine so the wheel UI stays calendar-agnostic.
 */
interface CalendarEngine {
  /** Which calendar this engine implements. */
  val calendarType: CalendarType

  // ---- Month / day ranges ----

  /** Number of days in [year] / [month] for this calendar. */
  fun daysInMonth(year: Int, month: Int): Int

  /** Whether [year] is a leap year in this calendar. */
  fun isLeapYear(year: Int): Boolean

  // ---- Display helpers ----

  /** Human-readable name for [month] (1-based) in the given [displayStyle]. */
  fun monthName(month: Int, displayStyle: MonthDisplayStyle): String

  /** Formatted year string for display in the wheel. */
  fun formatYear(year: Int): String

  /** Formatted day-of-month string for display in the wheel. */
  fun formatDay(day: Int): String

  // ---- Date clamping ----

  /**
   * Returns a valid (year, month, day) triple, clamping [day] to the maximum day of
   * [month] if it exceeds [daysInMonth(year, month)].
   */
  fun resolvePreviousValid(year: Int, month: Int, day: Int): Triple<Int, Int, Int>

  // ---- Conversion ----

  /**
   * Converts a Gregorian [localDate] to this calendar's (year, month, day).
   * Returns `null` if the conversion is unsupported on this platform.
   */
  fun fromGregorian(localDate: LocalDate): Triple<Int, Int, Int>?

  /**
   * Converts this calendar's (year, month, day) to a Gregorian [LocalDate].
   * Returns `null` if the conversion is unsupported on this platform.
   */
  fun toGregorian(year: Int, month: Int, day: Int): LocalDate?

  // ---- Date manipulation (operate on LocalDate, calendar-agnostic) ----

  /**
   * Sets the day-of-month on [localDate] to [day], clamping to the valid range for
   * the calendar's year/month. For Gregorian this is a direct `LocalDate` operation;
   * for Jalali it round-trips through the Jalali engine.
   */
  fun withDayOfMonth(localDate: LocalDate, day: Int): LocalDate? {
    val (y, m, _) = fromGregorian(localDate) ?: return null
    val clamped = resolvePreviousValid(y, m, day)
    return toGregorian(clamped.first, clamped.second, clamped.third)
  }

  /**
   * Sets the month on [localDate] to [monthNumber], clamping the day to the valid
   * range for the calendar's year/month.
   */
  fun withMonthNumber(localDate: LocalDate, monthNumber: Int): LocalDate? {
    val (y, _, d) = fromGregorian(localDate) ?: return null
    val clamped = resolvePreviousValid(y, monthNumber, d)
    return toGregorian(clamped.first, clamped.second, clamped.third)
  }

  /**
   * Sets the year on [localDate] to [year], clamping the day to the valid range for
   * the calendar's year/month.
   */
  fun withYear(localDate: LocalDate, year: Int): LocalDate? {
    val (_, m, d) = fromGregorian(localDate) ?: return null
    val clamped = resolvePreviousValid(year, m, d)
    return toGregorian(clamped.first, clamped.second, clamped.third)
  }

  /**
   * Returns the (year, month, day) triple for this calendar that corresponds to
   * [localDate], or `null` if the conversion is unsupported.
   */
  fun dateComponents(localDate: LocalDate): Triple<Int, Int, Int>? = fromGregorian(localDate)
}

/**
 * Resolves the displayed year range. The default range is specified using the public
 * Gregorian bounds, so translate those endpoints for non-Gregorian calendars. A caller-
 * supplied range is already expressed in the selected calendar and is kept unchanged.
 */
internal fun CalendarEngine.resolveYearsRange(
  yearsRange: IntRange?,
  minDate: LocalDate,
  maxDate: LocalDate,
): IntRange? {
  if (yearsRange == null || calendarType == CalendarType.Gregorian ||
    yearsRange != IntRange(minDate.year, maxDate.year)
  ) {
    return yearsRange
  }
  val minimumYear = dateComponents(minDate)?.first ?: return yearsRange
  val maximumYear = dateComponents(maxDate)?.first ?: return yearsRange
  return minimumYear..maximumYear
}
