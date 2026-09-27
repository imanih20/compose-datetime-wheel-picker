package dev.darkokoa.datetimewheelpicker.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.darkokoa.datetimewheelpicker.core.calendar.CalendarEngine
import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import kotlinx.datetime.LocalDate

internal data class DayOfMonth(
  val text: String,
  val value: Int,
  val index: Int
)

internal data class Month(
  val text: String,
  val value: Int,
  val index: Int
)

internal data class Year(
  val text: String,
  val value: Int,
  val index: Int
)

@Composable
internal fun rememberFormattedDayOfMonths(
  month: Int,
  year: Int,
  calendarEngine: CalendarEngine,
) = remember(month, year, calendarEngine) {
  val daysInMonth = calendarEngine.daysInMonth(year, month)

  (1..daysInMonth).map {
    DayOfMonth(
      text = calendarEngine.formatDay(it),
      value = it,
      index = it - 1
    )
  }
}

@Composable
internal fun rememberFormattedMonths(
  datePickerWidth: Dp,
  calendarEngine: CalendarEngine,
  monthDisplayStyle: MonthDisplayStyle = MonthDisplayStyle.FULL,
  monthShortDisplayStyle: MonthDisplayStyle = MonthDisplayStyle.SHORT,
) = remember(datePickerWidth, calendarEngine, monthDisplayStyle, monthShortDisplayStyle) {
  (1..12).map { monthNumber ->
    val monthName = calendarEngine.monthName(monthNumber, monthDisplayStyle)
    val monthShortName = calendarEngine.monthName(monthNumber, monthShortDisplayStyle)
    Month(
      text = if (datePickerWidth / 3 < 55.dp) monthShortName else monthName,
      value = monthNumber,
      index = monthNumber - 1
    )
  }
}

@Composable
internal fun rememberFormattedYears(
  yearsRange: IntRange?,
  calendarEngine: CalendarEngine,
) = remember(yearsRange, calendarEngine) {
  yearsRange?.map {
    Year(
      text = calendarEngine.formatYear(it),
      value = it,
      index = yearsRange.indexOf(it)
    )
  }
}
