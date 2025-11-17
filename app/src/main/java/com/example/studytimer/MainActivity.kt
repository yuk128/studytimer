package com.example.studytimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studytimer.ui.theme.StudyTimerTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTimerTheme {
                CombinedScreen()
            }
        }
    }
}

// 🌟 StopWatch + History 통합 화면
@Composable
fun CombinedScreen() {
    val historyList = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StopWatchApp(
            onRecord = { record ->
                historyList.add(0, record)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        HistoryList(historyList)
    }
}


// ---------------- StopWatch ----------------
@Composable
fun StopWatchApp(onRecord: (String) -> Unit) {
    var time by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    // 1초마다 증가
    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            time += 1000L
        }
    }

    val formattedTime = formatTime(time)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = formattedTime,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Button(onClick = { isRunning = true }) {
                Text("Start")
            }

            // ⬇ STOP 시에만 기록 추가
            Button(onClick = {
                if (isRunning) {
                    isRunning = false
                    onRecord("종료: $formattedTime / ${getNowTime()}")
                }
            }) {
                Text("Stop")
            }

            // ⬇ RESET 기록 없음
            Button(onClick = {
                isRunning = false
                time = 0L
            }) {
                Text("Reset")
            }
        }
    }
}


// ---------------- 기록 리스트 UI ----------------
@Composable
fun HistoryList(history: List<String>) {
    Text(
        text = "기록",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth().height(300.dp)
    ) {
        items(history) { item ->
            Text(
                text = item,
                fontSize = 16.sp,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}


// ---------------- 시간 포맷 함수 ----------------
fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// ---------------- 현재 시간 ----------------
fun getNowTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date())
}


// ---------------- Preview ----------------
@Preview(showBackground = true)
@Composable
fun CombinedPreview() {
    StudyTimerTheme {
        CombinedScreen()
    }
}
