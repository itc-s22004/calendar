package jp.ac.it_college.std.s22004.calendar.firebase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kizitonwose.calendar.core.CalendarDay
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneOffset

data class Schedule(
    val date: String,
    val time: String,
    val schedule: String?
)

fun addScheduleToFirestore(date: LocalDate, time: LocalTime, schedule: String) {
    val db = FirebaseFirestore.getInstance()

    val scheduleData = hashMapOf(
        "date" to date.toString(),
        "time" to time.toString(),
        "schedule" to schedule
    )

    db.collection("schedules")
        .add(scheduleData)
        .addOnSuccessListener { documentReference ->
            println("DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            println("Error adding document: $e")
        }
}

@Composable
fun GetDate(calendarDay: CalendarDay): List<Schedule> {
    var schedulesList by remember { mutableStateOf(listOf<Schedule>()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(calendarDay) {
        db.collection("schedules")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val fetchedSchedules = mutableListOf<Schedule>()
                for (doc in value!!) {
                    val date = doc.getString("date")
                    val time = doc.getString("time").toString()
                    val schedule = doc.getString("schedule")
                    if (date == calendarDay.date.toString()) {
                        fetchedSchedules.add(Schedule(date, time, schedule))
                    }
                }
                schedulesList = fetchedSchedules.sortedBy { it.time }
            }
    }
    return schedulesList
}

@Composable
fun GetDateMonth(currentMonth: YearMonth): Map<LocalDate, Int> {
    var schedulesCountByDay by remember { mutableStateOf(mapOf<LocalDate, Int>()) }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(currentMonth) {
        val startOfMonth = currentMonth.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant()
        val endOfMonth = currentMonth.atEndOfMonth().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC).toInstant()

        db.collection("schedules")
            .whereGreaterThanOrEqualTo("date", startOfMonth.toString())
            .whereLessThanOrEqualTo("date", endOfMonth.toString())
            .get()
            .addOnSuccessListener { result ->
                val tempSchedulesCountByDay = mutableMapOf<LocalDate, Int>()

                for (document in result) {
                    val dateStr = document.getString("date") ?: continue
                    val date = LocalDate.parse(dateStr)
                    val count = tempSchedulesCountByDay.getOrDefault(date, 0)
                    tempSchedulesCountByDay[date] = count + 1
                }

                schedulesCountByDay = tempSchedulesCountByDay
            }
    }
    return schedulesCountByDay

}