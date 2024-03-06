package jp.ac.it_college.std.s22004.calendar.scene

import android.graphics.Paint.Align
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.daysOfWeek
import jp.ac.it_college.std.s22004.calendar.compose.GetHoliday
import jp.ac.it_college.std.s22004.calendar.compose.Holiday
import jp.ac.it_college.std.s22004.calendar.compose.HolidayItem
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.min


@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    onDayClick: (CalendarDay) -> Unit = {}
) {
    val currentMonth = remember { YearMonth.now() }

    // 現在より前の年月
    val startMonth = remember { currentMonth.minusMonths(100) }
    // 現在より後の年月
    val endMonth = remember { currentMonth.plusMonths(100) }
    // 曜日
    val daysOfWeek = remember { daysOfWeek() }

    var selectionDay by remember { mutableStateOf<LocalDate?>(null) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    val holidays = GetHoliday()
    var currentDate by remember { mutableStateOf(LocalDate.now()) }


    // カレンダーの状態を持つ
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    var stateFirst = state.firstVisibleMonth.yearMonth
//    var stateFirstLocal: LocalDate = stateFirst.atDay(1)
    val stateFirstLocal = remember { mutableStateOf(stateFirst.atDay(1)) }
    val limitedDates = remember(currentMonth) {
        generateLimitedCalendarDays(currentMonth)
    }

//    val holidays = GetHoliday()
//    LaunchedEffect(Unit) {
//        scope.launch {
//            val apiDate = Api.getApi().data
//            val apiLocalDate = Api.getApi().localName
//
//            println(apiDate)
//        }
//    }
    Column() {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = {
                    currentDate = currentDate.minusMonths(1)
//                    stateFirstLocal = currentDate
                    stateFirstLocal.value = currentDate
//                    println(stateFirstLocal)
                }) {
                    Text(text = "<")
                }
                Text(
                    text = "${state.firstVisibleMonth.yearMonth}",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 50.sp,
                )
                Button(onClick = { /*TODO*/ }) {
                    Text(text = ">")
                }
            }

        }

        HorizontalCalendar(
            state = state,
            // 日付を表示する部分
            dayContent = { day ->
                val textColor = when (day.position) {
                    DayPosition.MonthDate -> when (day.date?.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue
                        DayOfWeek.SUNDAY -> Color.Red
                        else -> Color.Unspecified
                    }

                    DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
                    else -> Color.Unspecified
                }
                Box(
                    modifier = Modifier
                        .aspectRatio(0.5f)
//                        .background(color = if (isHoliday) Color.Green else Color.Transparent) // 祝日
//                        .background(color = dayColor)
//                        .border(width = 0.5.dp, color = if (LocalDate.now() == day.date) Color.Black else Color.LightGray)
                        .border(
                            width = if (LocalDate.now() == day.date) 2.dp else 0.5.dp,
                            color = if (LocalDate.now() == day.date) Color.Magenta else Color.LightGray
                        )
                        .padding(1.dp)
                        .clickable(enabled = day.position == DayPosition.MonthDate) {
                            selectionDay = day.date
                            selection = day
                            onDayClick(selection!!)
                            println(selection)
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 3.dp, start = 4.dp),
                        text = day.date.dayOfMonth.toString(), color = textColor
                    )
                    LazyColumn() {
                        items(holidays) { holiday ->
                            if (holiday.date == day.date.toString()) {
                                HolidayItem(holiday)
                            }
                        }
                    }
                }
            },
            monthHeader = { month ->
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
            },
            monthBody = { _, content ->
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(width = 0.5.dp, color = Color.LightGray) // 追加
                ) {
                    content()
                }
            }
//            state = state,
//            dayContent = { Day(it) }
        )
    }
}
@Composable
private fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun StartScenePreview() {
    CalendarTheme {
        CustomDatePicker()
    }
}


fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.getDefault()).let { value ->
        if (uppercase) value.uppercase(Locale.getDefault()) else value
    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.displayText(),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}


@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for ((index, dayOfWeek) in daysOfWeek.withIndex()) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
//                color = getDayOfWeekTextColor(index)
            )
        }
    }
}

fun generateLimitedCalendarDays(yearMonth: YearMonth): List<LocalDate> {
    val startDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = startDay.dayOfWeek.value
    val totalDays = daysInMonth + firstDayOfWeek - 1 // 月の日数 + 月の最初の日の週の位置 - 1
    val daysToShow = min(totalDays, 35) // 最大35日間または月の総日数、小さい方を使用

    return List(daysToShow) { index ->
        val dayOfMonth = index - firstDayOfWeek + 2 // indexを日に変換
        startDay.plusDays(dayOfMonth.toLong() - 1)
    }
}