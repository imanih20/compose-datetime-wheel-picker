package dev.darkokoa.datetimewheelpicker.core.calendar

private fun unsupported(): Nothing =
  throw UnsupportedOperationException("Jalali calendar requires PersianDate and is available on Android only.")

internal actual fun jalaliDaysInMonth(year: Int, month: Int): Int = unsupported()

internal actual fun jalaliIsLeapYear(year: Int): Boolean = unsupported()

internal actual fun jalaliToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? =
  unsupported()

internal actual fun gregorianToJalali(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? =
  unsupported()

internal actual val jalaliCalendarSupported: Boolean = false
