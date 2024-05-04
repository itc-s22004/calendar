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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Button
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
import jp.ac.it_college.std.s22004.calendar.component.GetHoliday
import jp.ac.it_college.std.s22004.calendar.component.Holiday
import jp.ac.it_college.std.s22004.calendar.firebase.GetDateMonth
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomDatePicker(
    modifier: Modifier = Modifier,
    onDayClick: (CalendarDay) -> Unit = {}
) {
    val currentMonth = remember { YearMonth.now() }

    // 現在より前の年月
    val startMonth = remember { currentMonth.minusMonths(50) }
    // 現在より後の年月
    val endMonth = remember { currentMonth.plusMonths(100) }
    // 曜日
    val daysOfWeek = remember { daysOfWeek() }

    var selectionDay by remember { mutableStateOf<LocalDate?>(null) }
    var selection by remember { mutableStateOf<CalendarDay?>(null) }

    // カレンダーの状態を持つ
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first(),
        outDateStyle = OutDateStyle.EndOfGrid
    )

    val holidays = GetHoliday()
    var schedulesCountByDay by remember { mutableStateOf(mapOf<LocalDate, Int>()) }
    schedulesCountByDay = GetDateMonth(state.firstVisibleMonth.yearMonth)

    val coroutineScope = rememberCoroutineScope()

    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp), contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.firstVisibleMonth.yearMonth}",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 50.sp,
                )
                Button(modifier = Modifier
                    .size(100.dp)
                    .padding(start = 8.dp), // 左側にパディングを追加して幅を開ける
                    onClick = {
                        // コルーチンスコープ内でsuspend関数を呼び出す
                        coroutineScope.launch {
                            state.scrollToMonth(currentMonth)
                        }
                    }
                ) {
                    Text("戻る")
                }
            }
        }
        DaysOfWeekTitle(daysOfWeek)  //曜日固定

        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                if (day.position == DayPosition.MonthDate || day.position == DayPosition.InDate) {
                    val textColor = when (day.position) {
                    DayPosition.MonthDate -> when (day.date?.dayOfWeek) {
                        DayOfWeek.SATURDAY -> Color.Blue
                        DayOfWeek.SUNDAY -> Color.Red
                        else -> Color.Unspecified
                    }

                    DayPosition.InDate -> Color.LightGray

                    else -> Color.Unspecified
                }
                Box(
                    modifier = Modifier
                        .aspectRatio(0.49f)
                        .background(Color.Transparent)
                        .border(
                            width = if (LocalDate.now() == day.date) 2.dp else 0.5.dp,
                            color = if (LocalDate.now() == day.date) Color.Magenta else Color.LightGray
                        )
//                        .padding(10.dp)
                        .clickable(enabled = day.position == DayPosition.MonthDate) {
                            selectionDay = day.date
                            selection = day
                            onDayClick(selection!!)
                            println(selection)
                        },
                ) {
//                        Text(
//                            modifier = Modifier
////                                .background(color = )
//                                .padding(top = 3.dp, start = 4.dp),
//                            text = day.date.dayOfMonth.toString(), color = textColor
//                        )
                    CalendarDay(day = day, holidays = holidays, textColor = textColor)

                    schedulesCountByDay[day.date]?.let { count ->
                        if (count > 0) {
                            Text(
                                text = "予定：$count",
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(start = 2.dp, top = 30.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                }
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
                        .border(width = 0.5.dp, color = Color.LightGray)
                ) {
                    content()
                }

            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StartScenePreview() {
    CalendarTheme {
        CustomDatePicker()
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {  //　月加水木金同日
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        for ((index, dayOfWeek) in daysOfWeek.withIndex()) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            )
        }
    }
}

@Composable
fun CalendarDay(day: CalendarDay, holidays: List<Holiday>, textColor: Color) { // 祝日表示変えた
    val isHoliday = holidays.any { holiday ->
        holiday.date == day.date.toString()
    }

    Text(
        modifier = Modifier
            .background(color = if (isHoliday) Color.Yellow else Color.Transparent)
            .padding(top = 3.dp, start = 4.dp),
        text = day.date.dayOfMonth.toString(),
        color = textColor
    )
}