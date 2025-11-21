
package com.example.studytimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTimerApp()
        }
    }
}
@Composable
fun StudyTimerApp() {
    var currentScreen by remember { mutableStateOf("timer") }

    // 기록 리스트
    val studyRecords = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { currentScreen = "timer" }) {
                Text("타이머")
            }

            Button(onClick = { currentScreen = "record" }) {
                Text("기록")
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (currentScreen == "timer") {
                TimerScreen(
                    onRecordAdd = { studyRecords.add(it) } // ✔ 기록 추가
                )
            } else {
                RecordScreen(records = studyRecords) // ✔ 기록 전달
            }
        }
    }
}


@Composable
fun RecordScreen(records: List<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "기록 화면",
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 기록 목록 출력
        records.forEach { record ->
            Text(text = "- $record")
        }
    }
}


@Composable
fun TimerScreen(
    onRecordAdd: (String) -> Unit
) {

    var remainingTime by remember { mutableStateOf(0) } // 남은 시간(초)
    var isRunning by remember { mutableStateOf(false) } // 타이머 실행 여부
    var finished by remember { mutableStateOf(false) }  // 타이머 종료 여부

    // 새로 추가
    var inputHours by remember { mutableStateOf("0") }
    var inputMinutes by remember { mutableStateOf("0") }
    var inputSeconds by remember { mutableStateOf("0") }

    // 카운트다운 실행 (한 번만 LaunchedEffect 사용)
    LaunchedEffect(Unit) {
        while (true) {
            if (isRunning && remainingTime > 0) {
                kotlinx.coroutines.delay(1000)
                remainingTime = remainingTime - 1
            } else if (remainingTime <= 0 && isRunning) {
                isRunning = false
                finished = true
            } else {
                kotlinx.coroutines.delay(100) // 타이머가 안 돌 때는 잠깐 대기
            }
        }
    }

    // 배경색 설정
    val bgColor = if (finished) Color(0xFFFFA5A5) else Color.White

    // 전체 UI 감싸기
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // 5
        // 5~6번 영역 대체
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 🔸 타이머 작동 중일 때만 남은 시간 표시
            if (isRunning) {
                Text(
                    text = formatTime(remainingTime),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            // 🔸 타이머 멈춤 상태일 때만 입력창 보이기
            if (!isRunning) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = inputHours,
                        onValueChange = { inputHours = it.filter { c -> c.isDigit() } },
                        label = { Text("시간") },
                        singleLine = true,
                        modifier = Modifier.width(80.dp)
                    )
                    TextField(
                        value = inputMinutes,
                        onValueChange = { inputMinutes = it.filter { c -> c.isDigit() } },
                        label = { Text("분") },
                        singleLine = true,
                        modifier = Modifier.width(80.dp)
                    )
                    TextField(
                        value = inputSeconds,
                        onValueChange = { inputSeconds = it.filter { c -> c.isDigit() } },
                        label = { Text("초") },
                        singleLine = true,
                        modifier = Modifier.width(80.dp)
                    )
                }
            }

            // 🔸 시작 / 중단 버튼
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = {
                    val h = inputHours.toIntOrNull() ?: 0
                    val m = inputMinutes.toIntOrNull() ?: 0
                    val s = inputSeconds.toIntOrNull() ?: 0

                    remainingTime = h * 3600 + m * 60 + s

                    if (remainingTime > 0) {
                        isRunning = true
                        finished = false
                    }
                }) {
                    Text("시작")
                }

                Button(onClick = {
                    isRunning = false
                }) {
                    Text("중단")
                }
            }
        }

    }





    @Composable
    fun RecordScreen(records: List<String>) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("공부 기록", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(16.dp))

            records.forEach { record ->
                Text(record)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}
fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}