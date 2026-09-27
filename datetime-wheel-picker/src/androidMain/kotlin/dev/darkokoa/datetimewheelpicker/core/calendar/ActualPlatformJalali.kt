package dev.darkokoa.datetimewheelpicker.core.calendar

import saman.zamani.persiandate.PersianDate

internal actual fun jalaliDaysInMonth(year: Int, month: Int): Int {
  return PersianDate().getMonthLength(year, month)
}

internal actual fun jalaliIsLeapYear(year: Int): Boolean {
  return PersianDate.isJalaliLeap(year)
}

internal actual fun jalaliToGregorian(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? {
  val result = PersianDate().jalali_to_gregorian(year, month, day)
  return Triple(result[0], result[1], result[2])
}

internal actual fun gregorianToJalali(year: Int, month: Int, day: Int): Triple<Int, Int, Int>? {
  val result = PersianDate().gregorian_to_jalali(year, month, day)
  return Triple(result[0], result[1], result[2])
}

internal actual val jalaliCalendarSupported: Boolean = true
