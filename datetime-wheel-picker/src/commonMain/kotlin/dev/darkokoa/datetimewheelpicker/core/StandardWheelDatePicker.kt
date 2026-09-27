@file:Suppress("NOTHING_TO_INLINE")

package dev.darkokoa.datetimewheelpicker.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.darkokoa.datetimewheelpicker.core.calendar.CalendarEngine
import dev.darkokoa.datetimewheelpicker.core.calendar.CalendarType
import dev.darkokoa.datetimewheelpicker.core.calendar.createCalendarEngine
import dev.darkokoa.datetimewheelpicker.core.calendar.resolveYearsRange
import dev.darkokoa.datetimewheelpicker.core.format.CjkSuffixConfig
import dev.darkokoa.datetimewheelpicker.core.format.DateField
import dev.darkokoa.datetimewheelpicker.core.format.DateFormatter
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.core.format.dateFormatter
import dev.darkokoa.datetimewheelpicker.core.isRtlLanguage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@Composable
internal fun StandardWheelDatePicker(
  modifier: Modifier = Modifier,
  startDate: LocalDate = LocalDate.now(),
  minDate: LocalDate = LocalDate.EPOCH,
  maxDate: LocalDate = LocalDate.CYB3R_1N1T_ZOLL,
  yearsRange: IntRange? = IntRange(minDate.year, maxDate.year),
  dateFormatter: DateFormatter = dateFormatter(
    locale = Locale.current,
    monthDisplayStyle = MonthDisplayStyle.SHORT,
    cjkSuffixConfig = CjkSuffixConfig.HideAll
  ),
  calendar: CalendarType = CalendarType.Gregorian,
  viewportSize: DpSize = DpSize(256.dp, 128.dp),
  rows: WheelRows = WheelRows.Count(3),
  textStyle: TextStyle = MaterialTheme.typography.titleMedium,
  textColor: Color = LocalContentColor.current,
  selectedTextStyle: TextStyle = textStyle,
  selectedTextColor: Color = textColor,
  selectorProperties: SelectorProperties = WheelPickerDefaults.selectorProperties(),
  barrelProperties: BarrelProperties = WheelPickerDefaults.barrelPropertiesFor(rows),
  onSnappedDateChanged: (snappedDate: SnappedDate) -> Unit = {},
  onSnappedDate: (snappedDate: SnappedDate) -> Int? = { _ -> null },
) {
  val itemCount = if (yearsRange == null) 2 else 3
  val itemWidth = viewportSize.width / itemCount

  // Calendar engine: Gregorian is always available; Jalali requires PersianDate on Android.
  // The engine is recreated only when the calendar type or formatter changes.
  val calendarEngine = remember(calendar, dateFormatter) {
    createCalendarEngine(calendar, dateFormatter)
  }

  // Resolve the date components in the selected calendar.
  val (initialYear, initialMonth, initialDay) = remember(startDate, minDate, maxDate, calendarEngine) {
    val coerced = startDate.coerceIn(minDate, maxDate)
    val components = calendarEngine.dateComponents(coerced)
      ?: Triple(coerced.year, coerced.month.number, coerced.day)
    Triple(components.first, components.second, components.third)
  }

  var snappedDate by remember { mutableStateOf(startDate.coerceIn(minDate, maxDate)) }
  val snappedDateChangeNotifier = remember { SnappedChangeNotifier<LocalDate>() }
  val notifySnappedDateChanged: (SnappedDate) -> Unit = { newSnappedDate ->
    snappedDateChangeNotifier.notifyIfChanged(newSnappedDate.snappedLocalDate) {
      onSnappedDateChanged(newSnappedDate)
    }
  }

  // Resolve current calendar components from snappedDate (recomputed on each scroll).
  val snappedComponents = remember(snappedDate, calendarEngine) {
    val components = calendarEngine.dateComponents(snappedDate)
      ?: Triple(snappedDate.year, snappedDate.month.number, snappedDate.day)
    Triple(components.first, components.second, components.third)
  }
  val snappedYear = snappedComponents.first
  val snappedMonth = snappedComponents.second
  val snappedDay = snappedComponents.third

  val dayOfMonths =
    rememberFormattedDayOfMonths(snappedMonth, snappedYear, calendarEngine)

  val months = rememberFormattedMonths(viewportSize.width, calendarEngine, MonthDisplayStyle.SHORT, MonthDisplayStyle.SHORT)
  val displayedYearsRange = remember(yearsRange, minDate, maxDate, calendarEngine) {
    calendarEngine.resolveYearsRange(yearsRange, minDate, maxDate)
  }
  val years = rememberFormattedYears(displayedYearsRange, calendarEngine)

  // Helper: apply a new snapped date only if within Gregorian min/max (existing behaviour).
  fun updateSnappedDate(newDate: LocalDate) {
    if (!newDate.isBefore(minDate) && !newDate.isAfter(maxDate)) {
      snappedDate = newDate
    }
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    WheelSelector(
      viewportSize = viewportSize,
      rows = rows,
      barrelProperties = barrelProperties,
      properties = selectorProperties,
    )
    val pickerLayoutDirection =
      if (calendar == CalendarType.Jalali && Locale.current.isRtlLanguage) {
        LayoutDirection.Rtl
      } else {
        LocalLayoutDirection.current
      }
    CompositionLocalProvider(LocalLayoutDirection provides pickerLayoutDirection) {
      Row {
      dateFormatter.dateOrder.fields.forEach { dateField ->
        when (dateField) {
          DateField.DAY -> {
            val currentDayOfMonths = dayOfMonths
            FixedSizeWheelTextPicker(
              viewportSize = DpSize(
                width = itemWidth,
                height = viewportSize.height
              ),
              texts = currentDayOfMonths.map { it.text },
              rows = rows,
              textStyle = textStyle,
              textColor = textColor,
              selectedTextStyle = selectedTextStyle,
              selectedTextColor = selectedTextColor,
              selectorProperties = WheelPickerDefaults.selectorProperties(
                enabled = false
              ),
              barrelProperties = barrelProperties,
              startIndex = currentDayOfMonths.find { it.value == initialDay }?.index ?: 0,
              onScrollFinished = { snappedIndex ->
                val newDayOfMonth = currentDayOfMonths.find { it.index == snappedIndex }?.value
                newDayOfMonth?.let {
                  val newDate = calendarEngine.withDayOfMonth(snappedDate, newDayOfMonth)
                  newDate?.let { updateSnappedDate(it) }

                  val newDay = currentDayOfMonths.find { it.value == calendarEngine.dateComponents(snappedDate)?.third ?: snappedDate.day }?.index
                  newDay?.let {
                    onSnappedDate(
                      SnappedDate.DayOfMonth(
                        localDate = snappedDate,
                        index = newDay
                      )
                    )?.let { return@FixedSizeWheelTextPicker it }
                  }
                }
                return@FixedSizeWheelTextPicker currentDayOfMonths.find { it.value == (calendarEngine.dateComponents(snappedDate)?.third ?: snappedDate.day) }?.index
              },
              onScrollChanged = { snappedIndex ->
                currentDayOfMonths.find { it.index == snappedIndex }?.value?.let { newDay ->
                  val pendingDate = calendarEngine.withDayOfMonth(snappedDate, newDay)
                  pendingDate?.let {
                    notifySnappedDateChanged(SnappedDate.DayOfMonth(localDate = it, index = snappedIndex))
                  }
                }
                }
            )
          }

          DateField.MONTH -> {
            val currentMonths = months
            FixedSizeWheelTextPicker(
              viewportSize = DpSize(
                width = itemWidth,
                height = viewportSize.height
              ),
              texts = currentMonths.map { it.text },
              rows = rows,
              textStyle = textStyle,
              textColor = textColor,
              selectedTextStyle = selectedTextStyle,
              selectedTextColor = selectedTextColor,
              selectorProperties = WheelPickerDefaults.selectorProperties(
                enabled = false
              ),
              barrelProperties = barrelProperties,
              startIndex = currentMonths.find { it.value == initialMonth }?.index ?: 0,
              onScrollFinished = { snappedIndex ->
                val newMonth = currentMonths.find { it.index == snappedIndex }?.value
                newMonth?.let {
                  val newDate = calendarEngine.withMonthNumber(snappedDate, newMonth)
                  newDate?.let { updateSnappedDate(it) }

                  val newIndex = currentMonths.find { it.value == (calendarEngine.dateComponents(snappedDate)?.second ?: snappedDate.month.number) }?.index
                  newIndex?.let {
                    onSnappedDate(
                      SnappedDate.Month(
                        localDate = snappedDate,
                        index = newIndex
                      )
                    )?.let { return@FixedSizeWheelTextPicker it }
                  }
                }
                return@FixedSizeWheelTextPicker currentMonths.find { it.value == (calendarEngine.dateComponents(snappedDate)?.second ?: snappedDate.month.number) }?.index
              },
              onScrollChanged = { snappedIndex ->
                currentMonths.find { it.index == snappedIndex }?.value?.let { newMonth ->
                  val pendingDate = calendarEngine.withMonthNumber(snappedDate, newMonth)
                  pendingDate?.let {
                    notifySnappedDateChanged(SnappedDate.Month(localDate = it, index = snappedIndex))
                  }
                }
                }
            )
          }

          DateField.YEAR -> {
            val currentYears = years
            currentYears?.let { years ->
              FixedSizeWheelTextPicker(
                viewportSize = DpSize(
                  width = itemWidth,
                  height = viewportSize.height
                ),
                texts = years.map { it.text },
                rows = rows,
                textStyle = textStyle,
                textColor = textColor,
                selectedTextStyle = selectedTextStyle,
                selectedTextColor = selectedTextColor,
                selectorProperties = WheelPickerDefaults.selectorProperties(
                  enabled = false
                ),
                barrelProperties = barrelProperties,
                startIndex = years.find { it.value == initialYear }?.index ?: 0,
                onScrollFinished = { snappedIndex ->
                  val newYear = years.find { it.index == snappedIndex }?.value
                  newYear?.let {
                    val newDate = calendarEngine.withYear(snappedDate, newYear)
                    newDate?.let { updateSnappedDate(it) }

                    val newIndex = years.find { it.value == (calendarEngine.dateComponents(snappedDate)?.first ?: snappedDate.year) }?.index
                    newIndex?.let {
                      onSnappedDate(
                        SnappedDate.Year(
                          localDate = snappedDate,
                          index = newIndex
                        )
                      )?.let { return@FixedSizeWheelTextPicker it }
                    }
                  }
                  return@FixedSizeWheelTextPicker years.find { it.value == (calendarEngine.dateComponents(snappedDate)?.first ?: snappedDate.year) }?.index
                },
                onScrollChanged = { snappedIndex ->
                  years.find { it.index == snappedIndex }?.value?.let { newYear ->
                    val pendingDate = calendarEngine.withYear(snappedDate, newYear)
                      pendingDate?.let {
                        notifySnappedDateChanged(SnappedDate.Year(localDate = it, index = snappedIndex))
                      }
                      }
                    }
              )
            }
          }
        }
      }
      }
    }
  }
}
