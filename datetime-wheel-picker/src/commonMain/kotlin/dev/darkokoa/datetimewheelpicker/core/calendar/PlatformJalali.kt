package dev.darkokoa.datetimewheelpicker.core.calendar

/**
 * Platform-specific Jalali (Persian) calendar calculations.
 *
 * These delegate to PersianDate on Android. On other platforms they throw
 * [UnsupportedOperationException] because PersianDate is an Android library.
 */
internal expect fun jalaliDaysInMonth(year: Int, month: Int): Int

internal expect fun jalaliIsLeapYear(year: Int): Boolean

/**
 * Converts a Jalali (year, month, day) to a Gregorian (year, month, day) triple.
 * Returns `null` if the conversion is unsupported on this platform.
 */
internal expect fun jalaliToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int>?

/**
 * Converts a Gregorian (year, month, day) to a Jalali (year, month, day) triple.
 * Returns `null` if the conversion is unsupported on this platform.
 */
internal expect fun gregorianToJalali(year: Int, month: Int, day: Int): Triple<Int, Int, Int>?

/**
 * Whether the Jalali calendar (via PersianDate) is available on the current platform.
 */
internal expect val jalaliCalendarSupported: Boolean
