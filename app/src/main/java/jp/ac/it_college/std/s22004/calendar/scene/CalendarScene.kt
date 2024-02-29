package jp.ac.it_college.std.s22004.calendar.scene

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomDatePicker() {
    // 今の月
    val currentMonth = remember { YearMonth.now() }
    // 現在より前の年月
    val startMonth = remember { currentMonth.minusMonths(100) }
    // 現在より後の年月
    val endMonth = remember { currentMonth.plusMonths(100) }
    // 曜日
    val daysOfWeek = remember { daysOfWeek() }

    var selectionDay by remember { mutableStateOf<LocalDate?>(null) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }
    var isSelected: Boolean by remember { mutableStateOf(false) }

    // カレンダーの状態を持つ
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    Column {
        Text(text = "日付 : $selectionDay", modifier = Modifier.padding(10.dp))
        HorizontalCalendar(
            state = state,
            // 日付を表示する部分
            dayContent = {
                Day(it, isSelected = selection == it) { clickDay ->
//                    println("clickDay: ${clickDay.date}")
                    selection = clickDay
                    selectionDay = clickDay.date
                }
            },
            // カレンダーのヘッダー
            monthHeader = { month ->
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
//                val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
//                MonthHeader(daysOfWeek = daysOfWeek)
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
            },
        )
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
                // 土日だけそれぞれ色を変えたいので対応したカラーコードを返している
//                color = getDayOfWeekTextColor(index)
            )
        }
    }
}


@Composable
private fun Day(
    day: CalendarDay,
    isSelected: Boolean = false, // ← 追加
    onClick: (CalendarDay) -> Unit = {}
) {
    val boxColor = remember { Color.Magenta }
    var selectBool by remember { mutableStateOf(false) }

    val textColor = when (day.position) {
        DayPosition.MonthDate -> when (day.date.dayOfWeek) {
            DayOfWeek.SATURDAY -> Color.Blue
            DayOfWeek.SUNDAY -> Color.Red
            else -> Color.Unspecified
        }

        DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(color = if (isSelected) Color.Cyan else Color.Transparent) // ← 追加
            .border(width = 0.5.dp, color = Color.LightGray)
            .padding(1.dp)
            .clickable(enabled = day.position == DayPosition.MonthDate) {
                onClick(day)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 3.dp, start = 4.dp),
            text = day.date.dayOfMonth.toString(), color = textColor
        )
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

@Preview(showBackground = true)
@Composable
fun StartScenePreview() {
    CalendarTheme {
//        CalendarScene(2024)
        CustomDatePicker()
    }
}