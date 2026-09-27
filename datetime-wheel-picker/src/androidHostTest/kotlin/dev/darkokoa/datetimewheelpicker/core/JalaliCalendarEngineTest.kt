package dev.darkokoa.datetimewheelpicker.core

import dev.darkokoa.datetimewheelpicker.core.calendar.CalendarType
import dev.darkokoa.datetimewheelpicker.core.calendar.JalaliCalendarEngine
import dev.darkokoa.datetimewheelpicker.core.calendar.JalaliDate
import dev.darkokoa.datetimewheelpicker.core.calendar.jalaliMonthName
import dev.darkokoa.datetimewheelpicker.core.calendar.resolveYearsRange
import dev.darkokoa.datetimewheelpicker.core.format.CjkSuffixConfig
import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.DateOrder
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.strings.FaStrings
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import saman.zamani.persiandate.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JalaliCalendarEngineAndroidTest {

  private val dateFormatter = object : DateFormatter {
    override val dateOrder = DateOrder.DMY
    override val monthDisplayStyle = MonthDisplayStyle.FULL
    override val cjkSuffixConfig = CjkSuffixConfig.ShowAll
    override val formatYear: (Int) -> String = { it.toString() }
    override val formatMonth: (Month, MonthDisplayStyle) -> String = { month, _ -> month.name }
    override val formatDay: (Int) -> String = { it.toString() }
  }
  private val engine = JalaliCalendarEngine(dateFormatter)
  private val persianDate = PersianDate()

  @Test
  fun monthLengthsMatchPersianDateAndUpdateBySelection() {
    for (month in 1..6) assertEquals(31, engine.daysInMonth(1404, month))
    for (month in 7..11) assertEquals(30, engine.daysInMonth(1404, month))

    val leapYear = (1390..1410).first { PersianDate.isJalaliLeap(it) }
    val nonLeapYear = (leapYear + 1..leapYear + 5).first { !PersianDate.isJalaliLeap(it) }
    assertEquals(persianDate.getMonthLength(leapYear, 12), engine.daysInMonth(leapYear, 12))
    assertEquals(persianDate.getMonthLength(nonLeapYear, 12), engine.daysInMonth(nonLeapYear, 12))
    assertEquals(if (PersianDate.isJalaliLeap(leapYear)) 30 else 29, engine.daysInMonth(leapYear, 12))
    assertEquals(if (PersianDate.isJalaliLeap(nonLeapYear)) 30 else 29, engine.daysInMonth(nonLeapYear, 12))
  }

  @Test
  fun esfandDateValidationUsesPersianDate() {
    assertEquals(
      PersianDate.isJalaliLeap(1403),
      JalaliDate(1403, 12, 30).isValid(),
      "1403/12/30 validity must follow PersianDate"
    )
    val leapYear = (1390..1410).first { PersianDate.isJalaliLeap(it) }
    val nonLeapYear = (leapYear + 1..leapYear + 5).first { !PersianDate.isJalaliLeap(it) }

    val esfand29 = JalaliDate(nonLeapYear, 12, 29)
    assertTrue(esfand29.isValid())
    assertEquals(29, engine.dateComponents(esfand29.toLocalDate())?.third)

    val esfand30 = JalaliDate(leapYear, 12, 30)
    assertTrue(PersianDate.isJalaliLeap(leapYear))
    assertTrue(esfand30.isValid())
    assertEquals(30, engine.dateComponents(esfand30.toLocalDate())?.third)

    val invalidEsfand = JalaliDate(nonLeapYear, 12, 30)
    assertFalse(invalidEsfand.isValid())
    assertFailsWith<IllegalArgumentException> { invalidEsfand.toLocalDate() }
  }

  @Test
  fun changingYearClampsEsfandThirtyToTwentyNine() {
    val leapYear = (1390..1410).first { PersianDate.isJalaliLeap(it) }
    val nonLeapYear = (leapYear + 1..leapYear + 5).first { !PersianDate.isJalaliLeap(it) }
    val leapDay = JalaliDate(leapYear, 12, 30)
    assertTrue(leapDay.isValid())

    val changed = engine.withYear(leapDay.toLocalDate(), nonLeapYear)
    assertEquals(Triple(nonLeapYear, 12, 29), changed?.let(engine::dateComponents))
  }

  @Test
  fun changingMonthClampsThirtyOneToThirtyAndExpandsBack() {
    val source = JalaliDate(1404, 6, 31).toLocalDate()
    val shorterMonth = engine.withMonthNumber(source, 7)
    assertEquals(Triple(1404, 7, 30), shorterMonth?.let(engine::dateComponents))
    assertEquals(30, engine.daysInMonth(1404, 7))

    val longerMonth = engine.withMonthNumber(shorterMonth!!, 6)
    assertEquals(Triple(1404, 6, 30), longerMonth?.let(engine::dateComponents))
    assertEquals(31, engine.daysInMonth(1404, 6))
  }

  @Test
  fun gregorianAndJalaliConversionsRoundTrip() {
    val gregorian = LocalDate(2024, 3, 20)
    val jalali = JalaliDate.from(gregorian)
    assertEquals(Triple(1403, 1, 1), Triple(jalali.year, jalali.month, jalali.day))
    assertEquals(gregorian, jalali.toLocalDate())

    val reference = persianDate.gregorian_to_jalali(2024, 3, 20)
    assertEquals(reference[0], jalali.year)
    assertEquals(reference[1], jalali.month)
    assertEquals(reference[2], jalali.day)
    val converted = persianDate.jalali_to_gregorian(jalali.year, jalali.month, jalali.day)
    assertEquals(gregorian.year, converted[0])
    assertEquals(gregorian.month.number, converted[1])
    assertEquals(gregorian.day, converted[2])
  }

  @Test
  fun jalaliDefaultYearRangeIsConvertedButCustomRangeIsKept() {
    val minimum = LocalDate(2024, 3, 20)
    val maximum = LocalDate(2025, 3, 20)
    assertEquals(
      JalaliDate.from(minimum).year..JalaliDate.from(maximum).year,
      engine.resolveYearsRange(2024..2025, minimum, maximum)
    )
    assertEquals(
      1300..1350,
      engine.resolveYearsRange(1300..1350, minimum, maximum)
    )
    assertEquals(CalendarType.Jalali, engine.calendarType)
  }

  @Test
  fun persianLocalizationProvidesJalaliMonthNames() {
    val names = listOf(
      "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
      "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    names.forEachIndexed { index, name ->
      assertEquals(name, jalaliMonthName(FaStrings, index + 1, MonthDisplayStyle.FULL))
    }
  }
}
