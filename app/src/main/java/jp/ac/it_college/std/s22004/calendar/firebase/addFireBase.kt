package jp.ac.it_college.std.s22004.calendar.firebase

import androidx.compose.material3.Text
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
fun getDate(calendarDay: CalendarDay): List<Schedule> {
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