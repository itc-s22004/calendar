package jp.ac.it_college.std.s22004.calendar.compose

import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

fun addScheduleToFirestore(date: LocalDate, time: String, schedule: String) {
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

