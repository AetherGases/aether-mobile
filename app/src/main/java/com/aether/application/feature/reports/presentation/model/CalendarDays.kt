package com.aether.application.feature.reports.presentation.model

import com.aether.application.feature.reports.presentation.screen.CalendarDay
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ptBr = Locale.forLanguageTag("pt-BR")
private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", ptBr)

fun LocalDate.toCalendarDay(
    selectedDate: LocalDate?,
    enabledDates: Set<LocalDate>
) = CalendarDay(
    number = "%02d".format(dayOfMonth),
    isSelected = this == selectedDate,
    isEnabled = this in enabledDates,
    date = this
)

/** Sunday-first weeks, padded with nulls to match the weekday header. */
fun buildMonthWeeks(
    month: YearMonth,
    selectedDate: LocalDate?,
    enabledDates: Set<LocalDate>
): List<List<CalendarDay?>> {
    val leadingBlanks = month.atDay(1).dayOfWeek.value % 7
    val cells: List<CalendarDay?> =
        List(leadingBlanks) { null } +
            (1..month.lengthOfMonth()).map {
                month.atDay(it).toCalendarDay(selectedDate, enabledDates)
            }
    return cells.chunked(7).map { week -> week + List(7 - week.size) { null } }
}

fun YearMonth.label(): String =
    format(monthFormatter).replaceFirstChar { it.titlecase(ptBr) }
