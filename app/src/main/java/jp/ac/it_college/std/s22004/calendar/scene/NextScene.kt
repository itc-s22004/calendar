package jp.ac.it_college.std.s22004.calendar.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import java.time.LocalDate

@Composable
fun NextScene(modifier: Modifier = Modifier ,calendarDay: CalendarDay) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ){
        Text(text = "${calendarDay.date}", fontSize = 50.sp)

    }
}


@Preview(showBackground = true)
@Composable
fun NextScenePreview() {
    CalendarTheme {
        NextScene(Modifier, CalendarDay(
            date = LocalDate.now(), // 現在の日付
            position = DayPosition.MonthDate
        ))
    }
}

//CalendarDay(date=2024-03-14, position=MonthDate)