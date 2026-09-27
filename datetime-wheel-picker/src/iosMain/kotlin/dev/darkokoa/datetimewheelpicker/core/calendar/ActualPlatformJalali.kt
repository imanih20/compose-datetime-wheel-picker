package dev.darkokoa.datetimewheelpicker.core.calendar

internal actual fun jalaliDaysInMonth(year: Int, month: Int): Int {
  throw UnsupportedOperationException("Jalali calendar requires PersianDate, which is only available on Android.")
}

internal actual fun jalaliIsLeapYear(year: Int): Boolean {
  throw UnsupportedOperationException("Jalali calendar requires PersianDate, which is only available on Android.")
}

internal actual fun jalaliToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? {
  throw UnsupportedOperationException("Jalali calendar requires PersianDate, which is only available on Android.")
}

internal actual fun gregorianToJalali(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? {
  throw UnsupportedOperationException("Jalali calendar requires PersianDate, which is only available on Android.")
}

internal actual val jalaliCalendarSupported: Boolean = false
