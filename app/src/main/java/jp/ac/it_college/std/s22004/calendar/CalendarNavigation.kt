package jp.ac.it_college.std.s22004.calendar

import NextScene
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import jp.ac.it_college.std.s22004.calendar.scene.CustomDatePicker
import java.time.LocalDate

object Destinations {
    const val START = "start"
    const val DAY = "day"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderNavigation(
    navController: NavHostController = rememberNavController(),
) {
    var titleText by remember { mutableStateOf("") }
    var showText by remember { mutableStateOf(false) }

    var calendarDay = CalendarDay(
        date = LocalDate.now(), // 現在の日付
        position = DayPosition.MonthDate // 位置は仮にMIDDLEとします
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text(text = titleText) },
                    navigationIcon = {
                        if (titleText != "カレンダー") {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showText = true }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )

            },
        ) {
            NavHost(
                navController = navController,
                startDestination = Destinations.START,
                modifier = Modifier.padding(it)
            ) {
                composable(Destinations.START) {
                    titleText = "カレンダー"
                    CustomDatePicker(
                        modifier = Modifier,
                        onDayClick = {day ->
                            calendarDay = day
                            navController.navigate(Destinations.DAY)
                        },
                    )
                }
                composable(Destinations.DAY) {
                    titleText = "選んだ日付"
                    NextScene(Modifier, calendarDay)
                }
            }
        }
    }
}
