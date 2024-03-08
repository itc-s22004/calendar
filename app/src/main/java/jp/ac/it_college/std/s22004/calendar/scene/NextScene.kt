package jp.ac.it_college.std.s22004.calendar.scene

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import jp.ac.it_college.std.s22004.calendar.component.GetHoliday
import jp.ac.it_college.std.s22004.calendar.component.HolidayItem
import jp.ac.it_college.std.s22004.calendar.firebase.addScheduleToFirestore
import jp.ac.it_college.std.s22004.calendar.firebase.GetDate
import jp.ac.it_college.std.s22004.calendar.ui.theme.CalendarTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


@Composable
fun NextScene(modifier: Modifier = Modifier, calendarDay: CalendarDay) {
    val holidays = GetHoliday()
    val getDate = GetDate(calendarDay)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "${calendarDay.date}", fontSize = 50.sp)
        ScheduleDialog(calendarDay.date)
        LazyColumn {
            items(holidays) { holiday ->
                if (holiday.date == calendarDay.date.toString()) {
                    Text(text = HolidayItem(holiday), fontSize = 30.sp)

                }
            }
        }
        Text(
            text = "予定",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
        ) {
            items(getDate) { schedule ->
                Row(
                    modifier = Modifier.padding(start = 30.dp, top = 15.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = schedule.time,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(end = 16.dp) // 時間と予定の間にスペースを追加
                    )
                    Box(
                        modifier = Modifier.weight(1f) // 余ったスペースを予定で埋める
                    ) {
                        Text(
                            text = "${schedule.schedule}",
                            fontSize = 30.sp,
                            lineHeight = 35.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleDialog(day: LocalDate) {
    var showDialog: Boolean by remember { mutableStateOf(false) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var schedule by remember { mutableStateOf("") }
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    var openDaiLog by remember { mutableStateOf(false) }


    Button(onClick = { showDialog = true }) {
        Text("スケジュールを追加")
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("スケジュールを追加") },
            text = {
                Column {
                    Button(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val hour = calendar.get(Calendar.HOUR_OF_DAY)
                            val minute = calendar.get(Calendar.MINUTE)
                            TimePickerDialog(
                                context,
                                { _: TimePicker, hourOfDay: Int, minuteOfHour: Int ->
                                    time = LocalTime.of(hourOfDay, minuteOfHour)
                                },
                                hour,
                                minute,
                                true
                            ).show()

                        }, modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(if (time == null) "時間を選択" else time!!.format(timeFormatter))
                    }
                    TextField(
                        value = schedule,
                        onValueChange = { schedule = it },
                        label = { Text("予定") }
                    )
                }
                if (openDaiLog) {
                    TimeDialog()
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
                        addScheduleToFirestore(day, time!!, schedule)
                    },
                    enabled = time != null
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
                date = LocalDate.now(),
                position = DayPosition.MonthDate
            )
        )
    }
}

@Composable
fun TimeDialog() {
    val context = LocalContext.current
    var time by remember { mutableStateOf<LocalTime?>(null) }

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    TimePickerDialog(context, { _: TimePicker, hourOfDay: Int, minuteOfHour: Int ->
        time = LocalTime.of(hourOfDay, minuteOfHour)
    }, hour, minute, true).show()
}