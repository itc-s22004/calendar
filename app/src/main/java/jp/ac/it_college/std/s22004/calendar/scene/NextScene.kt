package jp.ac.it_college.std.s22004.calendar.scene

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import jp.ac.it_college.std.s22004.calendar.compose.addScheduleToFirestore
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import java.time.LocalDate
import java.time.LocalTime

//import com.google.firebase.database.Fi

@Composable
fun NextScene(modifier: Modifier = Modifier, calendarDay: CalendarDay) {
    var openDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "${calendarDay.date}", fontSize = 50.sp)
        ScheduleDialog(calendarDay.date)
        Text(
            text = "予定",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }

}

@Composable
fun ScheduleDialog(day: LocalDate) {
    var showDialog: Boolean by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }

    Button(onClick = { showDialog = true }) {
        Text("スケジュールを追加")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("スケジュールを追加") },
            text = {
                Column {
                    TextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("時間") }
                    )
                    TextField(
                        value = schedule,
                        onValueChange = { schedule = it },
                        label = { Text("予定") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val database = FirebaseDatabase.getInstance()
                        val myRef = database.getReference("schedules")

                        val scheduleEntry = mapOf(
                            "day" to day,
                            "time" to time,
                            "schedule" to schedule
                        )
                        myRef.push().setValue(scheduleEntry)
                        showDialog = false
                        addScheduleToFirestore(day, time, schedule)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NextScenePreview() {
    CalendarTheme {
        NextScene(
            Modifier, CalendarDay(
                date = LocalDate.now(), // 現在の日付
                position = DayPosition.MonthDate
            )
        )
    }
}
