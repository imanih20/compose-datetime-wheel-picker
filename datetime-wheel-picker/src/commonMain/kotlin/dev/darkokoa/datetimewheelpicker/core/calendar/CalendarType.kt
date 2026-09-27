package dev.darkokoa.datetimewheelpicker.core.calendar

/**
 * The calendar system a [WheelDatePicker][dev.darkokoa.datetimewheelpicker.WheelDatePicker] uses.
 *
 * - [Gregorian] keeps the existing behaviour: months and days are calculated with
 *   `kotlinx.datetime.LocalDate`.
 * - [Jalali] uses the Persian (Solar Hijri) calendar. The Jalali leap-year decision and
 *   Gregorian↔Jalali conversion are delegated to PersianDate on Android; other platforms
 *   report the engine as unavailable.
 */
enum class CalendarType {
  Gregorian,
  Jalali,
}
