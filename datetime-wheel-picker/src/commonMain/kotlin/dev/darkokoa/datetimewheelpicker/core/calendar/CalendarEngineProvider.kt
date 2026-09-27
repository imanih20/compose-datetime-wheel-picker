package dev.darkokoa.datetimewheelpicker.core.calendar

import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter

/**
 * Creates a [CalendarEngine] for the given [calendarType].
 *
 * - [CalendarType.Gregorian] always works — no external dependency.
 * - [CalendarType.Jalali] requires PersianDate, which is published as an Android
 *   library. The Android `actual` implementation delegates to PersianDate; unsupported
 *   platforms fail immediately with [UnsupportedOperationException].
 */
fun createCalendarEngine(calendarType: CalendarType, dateFormatter: DateFormatter): CalendarEngine =
  when (calendarType) {
    CalendarType.Gregorian -> GregorianCalendarEngine(dateFormatter)
    CalendarType.Jalali -> {
      if (!jalaliCalendarSupported) {
        throw UnsupportedOperationException("Jalali calendar requires PersianDate and is available on Android only.")
      }
      JalaliCalendarEngine(dateFormatter)
    }
  }

/**
 * Whether the Jalali calendar (via PersianDate) is available on the current platform.
 *
 * Returns `true` on Android; `false` on other targets where PersianDate is not
 * available.
 */
val jalaliCalendarAvailable: Boolean get() = jalaliCalendarSupported
