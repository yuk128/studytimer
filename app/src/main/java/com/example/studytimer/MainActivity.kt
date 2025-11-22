package com.example.studytimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.time.format.DateTimeFormatter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import java.text.SimpleDateFormat
import java.util.*

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
    // 화면 전환 상태 ("timer" 또는 "record")
    var currentScreen by remember { mutableStateOf("timer") }

    // 기록 리스트
    val studyRecords = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize()) {

        // 상단 버튼으로 화면 전환
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

        // 화면 영역
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                "timer" -> TimerScreen(
                    onRecordAdd = { studyRecords.add(it) } // Timer에서 기록 추가
                )
                "record" -> RecordScreen(records = studyRecords) // Record 화면
            }
        }
    }
}






@Composable
fun TimerScreen(onRecordAdd: (String) -> Unit) {
    var remainingTime by remember { mutableStateOf(0) }      // 초 단위
    var isRunning by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }

    var inputHours by remember { mutableStateOf("0") }
    var inputMinutes by remember { mutableStateOf("0") }
    var inputSeconds by remember { mutableStateOf("0") }

    // 타이머 카운트다운
    LaunchedEffect(Unit) {
        while (true) {
            if (isRunning && remainingTime > 0) {
                delay(1000)
                remainingTime--
            } else if (remainingTime <= 0 && isRunning) {
                // 타이머 종료 직후 처리
                isRunning = false
                finished = true

                // 기록 추가 (입력값 기준 총 시간)
                val h = inputHours.toIntOrNull() ?: 0
                val m = inputMinutes.toIntOrNull() ?: 0
                val s = inputSeconds.toIntOrNull() ?: 0
                val totalSeconds = h * 3600 + m * 60 + s
                val timeString = formatTime(totalSeconds)
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                onRecordAdd("집중 $timeString 완료 - $timestamp")
            } else {
                delay(100)
            }
        }
    }

    // 입력 기준 총 초 (0이면 progress 0)
    val totalSeconds = (inputHours.toIntOrNull() ?: 0) * 3600 +
            (inputMinutes.toIntOrNull() ?: 0) * 60 +
            (inputSeconds.toIntOrNull() ?: 0)
    val progress = if (totalSeconds > 0) remainingTime.toFloat() / totalSeconds else 0f

    // 배경은 항상 흰색(요청대로 배경 빨강 대신 원형 바 색만 변경)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // 1) 원형 바 (크기 조절: 여기 sizeDp 변경하면 전체 크기 바뀜)
        val circleSize = 420.dp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.size(circleSize)
        ) {
            CircularTimer(progress = progress, finished = finished, sizeDp = circleSize)
        }

        // 2) 원 안에 겹쳐 표시할 내용 (글자/입력/버튼)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.align(Alignment.Center)
        ) {
            // 남은시간(실행 중일때 크게 표시, 멈춰있으면 입력창 보임)
            if (isRunning) {
                Text(text = formatTime(remainingTime), fontSize = 36.sp)
            }

            if (!isRunning) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = inputHours,
                        onValueChange = { inputHours = it.filter { c -> c.isDigit() } },
                        label = { Text("시간") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                    TextField(
                        value = inputMinutes,
                        onValueChange = { inputMinutes = it.filter { c -> c.isDigit() } },
                        label = { Text("분") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                    TextField(
                        value = inputSeconds,
                        onValueChange = { inputSeconds = it.filter { c -> c.isDigit() } },
                        label = { Text("초") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    // 입력값으로 remainingTime 재계산
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
                    // 일시정지
                    isRunning = false
                }) {
                    Text("중단")
                }

                Button(onClick = {
                    // 초기화
                    isRunning = false
                    remainingTime = 0
                    inputHours = "0"
                    inputMinutes = "0"
                    inputSeconds = "0"
                    finished = false
                }) {
                    Text("리셋")
                }


            }
        }
    }
}

@Composable
fun CircularTimer(progress: Float, finished: Boolean = false, sizeDp: Dp = 400.dp) {
    Canvas(modifier = Modifier.size(sizeDp)) {
        val strokeWidth = (size.minDimension / 30f).coerceAtLeast(2f)

        val radius = size.minDimension / 2f - strokeWidth
        val center = Offset(size.width / 2f, size.height / 2f)

        // 배경 원
        drawCircle(
            color = Color(0xFFE6E6E6),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // 진행 아크 색
        val arcColor = if (finished) Color.Red else Color(0xFF3DDC84)

        // 진행 아크 그리기
        drawArc(
            color = arcColor,
            startAngle = -90f,
            sweepAngle = if (finished) 360f else (360f * progress).coerceIn(0f, 360f),
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}





@Composable
fun RecordScreen(records: SnapshotStateList<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("공부 기록", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // 스크롤 가능한 리스트
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(records) { index, record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0E0E0))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 기록 텍스트
                    Text(text = record)

                    // 삭제 버튼
                    Button(onClick = { records.removeAt(index) }) {
                        Text("삭제")
                    }
                }
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

fun getCurrentTimestamp(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}
