package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudyTimerApp()
        }
    }
}

data class StudyRecord(
    var title: String = "집중시간",
    val elapsedSeconds: Int,
    val timestamp: String
)
data class TodoItem(
    val id: Int,
    val text: String,
    val completed: Boolean = false
)




@Composable
fun StudyTimerApp() {
    var currentScreen by remember { mutableStateOf("timer") }

    var isFocusMode by remember { mutableStateOf(true) }

    var focusHours by remember { mutableStateOf("0") }
    var focusMinutes by remember { mutableStateOf("0") }
    var focusSeconds by remember { mutableStateOf("0") }

    var restHours by remember { mutableStateOf("0") }
    var restMinutes by remember { mutableStateOf("0") }
    var restSeconds by remember { mutableStateOf("0") }

    var remainingTime by remember { mutableStateOf(0) }
    var totalTime by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    var repeatCount by remember { mutableStateOf("1") }
    var repeatRemaining by remember { mutableStateOf(0) }
    var isRepeatMode by remember { mutableStateOf(false) }

    val studyRecords = remember { mutableStateListOf<StudyRecord>() }

    fun getFocusSeconds(): Int =
        (focusHours.toIntOrNull() ?: 0) * 3600 +
                (focusMinutes.toIntOrNull() ?: 0) * 60 +
                (focusSeconds.toIntOrNull() ?: 0)

    fun getRestSeconds(): Int =
        (restHours.toIntOrNull() ?: 0) * 3600 +
                (restMinutes.toIntOrNull() ?: 0) * 60 +
                (restSeconds.toIntOrNull() ?: 0)

    fun makeRecord(modeText: String, seconds: Int): StudyRecord {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        return StudyRecord(
            title = modeText,
            elapsedSeconds = seconds,
            timestamp = timestamp
        )
    }


    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            if (remainingTime > 0) {
                remainingTime--
            } else {
                if (isFocusMode) {
                    val elapsed = if (totalTime > 0) totalTime else getFocusSeconds()
                    studyRecords.add(makeRecord("집중", elapsed))
                }

                if (isRepeatMode) {
                    repeatRemaining--

                    if (repeatRemaining > 0) {
                        isFocusMode = !isFocusMode
                        totalTime = if (isFocusMode) getFocusSeconds() else getRestSeconds()
                        remainingTime = totalTime

                    } else {

                        isRunning = false
                        isRepeatMode = false
                    }
                } else {

                    isRunning = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (currentScreen) {
                "profile" -> {
                    ProfileScreen()
                }

                "timer" -> {
                    TimerScreen(
                        isFocusMode = isFocusMode,
                        onFocusModeChange = { isFocusMode = it },

                        focusHours = focusHours,
                        onFocusHoursChange = { focusHours = it },
                        focusMinutes = focusMinutes,
                        onFocusMinutesChange = { focusMinutes = it },
                        focusSeconds = focusSeconds,
                        onFocusSecondsChange = { focusSeconds = it },

                        restHours = restHours,
                        onRestHoursChange = { restHours = it },
                        restMinutes = restMinutes,
                        onRestMinutesChange = { restMinutes = it },
                        restSeconds = restSeconds,
                        onRestSecondsChange = { restSeconds = it },

                        remainingTime = remainingTime,
                        totalTime = totalTime,
                        isRunning = isRunning,
                        setRemainingTime = { remainingTime = it },
                        setTotalTime = { totalTime = it },
                        setRunning = { isRunning = it },

                        repeatCount = repeatCount,
                        onRepeatCountChange = { repeatCount = it },
                        repeatRemaining = repeatRemaining,
                        setRepeatRemaining = { repeatRemaining = it },
                        isRepeatMode = isRepeatMode,
                        setRepeatMode = { isRepeatMode = it },
                        onRequestStart = {
                            if (remainingTime <= 0) {
                                totalTime = if (isFocusMode) getFocusSeconds() else getRestSeconds()
                                remainingTime = totalTime
                                repeatRemaining = 0
                                isRepeatMode = false
                            }
                            isRunning = true
                        },
                        onRequestStop = {
                            if (remainingTime < totalTime && remainingTime > 0 && isFocusMode) {
                                val elapsed = totalTime - remainingTime
                                studyRecords.add(makeRecord("집중", elapsed))
                            }
                            isRunning = false
                        },
                        onRequestRepeat = {
                            val r = repeatCount.toIntOrNull() ?: 0
                            if (r > 0) {
                                repeatRemaining = r * 2
                                isRepeatMode = true
                                totalTime = if (isFocusMode) getFocusSeconds() else getRestSeconds()
                                remainingTime = totalTime
                                isRunning = true
                            }
                        },
                        onRequestReset = {
                            isRunning = false
                            remainingTime = 0
                            totalTime = 0
                            repeatRemaining = 0
                            isRepeatMode = false
                        },
                        onRecordAdd = { record -> studyRecords.add(record) }
                    )
                }

                "record" -> {
                    RecordScreen(
                        records = studyRecords,
                        onBack = { currentScreen = "timer" },
                        onRecordUpdate = { idx, newTitle ->
                            studyRecords[idx] = studyRecords[idx].copy(title = newTitle)
                        },
                        onRecordDelete = { idx ->
                            studyRecords.removeAt(idx)
                        }
                    )
                }

            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 프로필 버튼
            Image(
                painter = painterResource(id = R.drawable.plofil),
                contentDescription = "프로필",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { currentScreen = "profile" }
            )

            // 타이머 버튼
            Image(
                painter = painterResource(id = R.drawable.timer),
                contentDescription = "타이머",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { currentScreen = "timer" }
            )

            // 기록 버튼
            Image(
                painter = painterResource(id = R.drawable.list),
                contentDescription = "기록",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { currentScreen = "record" }
            )
        }


    }
}

fun formatTime(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TimerScreen(
    isFocusMode: Boolean,
    onFocusModeChange: (Boolean) -> Unit,

    focusHours: String,
    onFocusHoursChange: (String) -> Unit,
    focusMinutes: String,
    onFocusMinutesChange: (String) -> Unit,
    focusSeconds: String,
    onFocusSecondsChange: (String) -> Unit,

    restHours: String,
    onRestHoursChange: (String) -> Unit,
    restMinutes: String,
    onRestMinutesChange: (String) -> Unit,
    restSeconds: String,
    onRestSecondsChange: (String) -> Unit,

    remainingTime: Int,
    totalTime: Int,
    isRunning: Boolean,
    setRemainingTime: (Int) -> Unit,
    setTotalTime: (Int) -> Unit,
    setRunning: (Boolean) -> Unit,

    repeatCount: String,
    onRepeatCountChange: (String) -> Unit,
    repeatRemaining: Int,
    setRepeatRemaining: (Int) -> Unit,
    isRepeatMode: Boolean,
    setRepeatMode: (Boolean) -> Unit,

    onRequestStart: () -> Unit,
    onRequestStop: () -> Unit,
    onRequestRepeat: () -> Unit,
    onRequestReset: () -> Unit,

    onRecordAdd: (StudyRecord) -> Unit
)
{
    val progress = if (totalTime > 0) remainingTime.toFloat() / totalTime else 0f
    val circleSize = 420.dp
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentIsRunning by rememberUpdatedState(isRunning)
    val currentRemaining by rememberUpdatedState(remainingTime)
    var wasRunningBeforePause by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {

                Lifecycle.Event.ON_PAUSE -> {
                    wasRunningBeforePause = currentIsRunning
                    setRunning(false)
                }

                Lifecycle.Event.ON_RESUME -> {
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }





    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (remainingTime > 0 || isRunning) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(
                        id = if (isFocusMode) R.drawable.book else R.drawable.game
                    ),
                    contentDescription = if (isFocusMode) "집중 모드" else "휴식 모드",
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Box(
            modifier = Modifier.size(circleSize),
            contentAlignment = Alignment.Center
        ) {

            CircularTimer(
                progress = progress,
                color = if (isFocusMode) Color(0xFF2196F3) else Color(0xFF4CAF50),
                sizeDp = circleSize
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                if (isRunning || remainingTime > 0) {
                    Text(formatTime(remainingTime), fontSize = 36.sp)
                } else {

                    Text("집중시간", fontSize = 22.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = focusHours,
                            onValueChange = { onFocusHoursChange(it.filter(Char::isDigit)) },
                            label = { Text("시") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )

                        TextField(
                            value = focusMinutes,
                            onValueChange = { onFocusMinutesChange(it.filter(Char::isDigit)) },
                            label = { Text("분") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )

                        TextField(
                            value = focusSeconds,
                            onValueChange = { onFocusSecondsChange(it.filter(Char::isDigit)) },
                            label = { Text("초") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("휴식시간", fontSize = 22.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = restHours,
                            onValueChange = { onRestHoursChange(it.filter(Char::isDigit)) },
                            label = { Text("시") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )

                        TextField(
                            value = restMinutes,
                            onValueChange = { onRestMinutesChange(it.filter(Char::isDigit)) },
                            label = { Text("분") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )

                        TextField(
                            value = restSeconds,
                            onValueChange = { onRestSecondsChange(it.filter(Char::isDigit)) },
                            label = { Text("초") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(70.dp)
                        )
                    }
                }
            }


        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            SquareButton(
                icon = R.drawable.restart,
                desc = "리셋",
                onClick = { onRequestReset() }
            )

            SquareButton(
                icon = R.drawable.start,
                desc = "반복",
                onClick = { onRequestRepeat() }
            )

            SquareButton(
                icon = R.drawable.stop,
                desc = "중단",
                onClick = { onRequestStop() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text("반복 수:")
            TextField(
                value = repeatCount,
                onValueChange = { onRepeatCountChange(it.filter(Char::isDigit)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp)
            )
        }




        Spacer(modifier = Modifier.height(24.dp))

    }
}

@Composable
fun SquareButton(
    icon: Int,
    desc: String,
    size: Dp = 56.dp,
    iconSize: Dp = 32.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)

            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = desc,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun CircularTimer(
    progress: Float,
    color: Color,
    sizeDp: Dp = 100.dp,
    strokeWidth: Dp = 10.dp
) {
    Canvas(modifier = Modifier.size(sizeDp)) {

        val sweep = 360 * progress
        val halfStroke = strokeWidth.toPx() / 2

        drawArc(
            color = Color.LightGray.copy(alpha = 0.4f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            ),
            topLeft = Offset(halfStroke, halfStroke),
            size = androidx.compose.ui.geometry.Size(
                width = size.width - strokeWidth.toPx(),
                height = size.height - strokeWidth.toPx()
            )
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round
            ),
            topLeft = Offset(halfStroke, halfStroke),
            size = androidx.compose.ui.geometry.Size(
                width = size.width - strokeWidth.toPx(),
                height = size.height - strokeWidth.toPx()
            )
        )
    }
}

@Composable
fun RecordScreen(
    records: List<StudyRecord>,
    onBack: () -> Unit,
    onRecordUpdate: (index: Int, newTitle: String) -> Unit,
    onRecordDelete: (index: Int) -> Unit   // ✅ 추가
)
 {
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var editingTitle by remember { mutableStateOf("") }
     var deleteIndex by remember { mutableStateOf<Int?>(null) }


    val totalSeconds = records.sumOf { it.elapsedSeconds }
    val totalTimeStr = formatTime(totalSeconds)


    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("기록", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(records) { index, record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("${index + 1}. ${record.title}", fontSize = 18.sp)
                        Text("${formatTime(record.elapsedSeconds)} · ${record.timestamp}", fontSize = 12.sp)
                    }

                    Row {
                        IconButton(
                            onClick = {
                                editingIndex = index
                                editingTitle = record.title
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "수정",tint = Color.Gray)
                        }

                        IconButton(
                            onClick = {
                                deleteIndex = index
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "삭제",tint = Color.Gray)
                        }
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("총 시간: $totalTimeStr", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (editingIndex != null) {
        AlertDialog(
            onDismissRequest = { editingIndex = null },
            title = { Text("기록 제목 수정") },
            text = {
                TextField(
                    value = editingTitle,
                    onValueChange = { editingTitle = it },
                    label = { Text("제목") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    editingIndex?.let { idx ->
                        onRecordUpdate(idx, editingTitle)
                    }
                    editingIndex = null
                }) { Text("저장") }
            },
            dismissButton = {
                Button(onClick = { editingIndex = null }) { Text("취소") }
            }
        )
    }
     if (deleteIndex != null) {
         AlertDialog(
             onDismissRequest = { deleteIndex = null },
             title = { Text("기록 삭제") },
             text = { Text("이 기록을 삭제하시겠습니까?") },
             confirmButton = {
                 Button(
                     onClick = {
                         deleteIndex?.let { idx ->
                             onRecordDelete(idx)
                         }
                         deleteIndex = null
                     }
                 ) {
                     Text("삭제")
                 }
             },
             dismissButton = {
                 Button(
                     onClick = { deleteIndex = null }
                 ) {
                     Text("취소")
                 }
             }
         )
     }

 }

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        var todoList by remember { mutableStateOf(emptyList<TodoItem>()) }
        var editingTodoId by remember { mutableStateOf<Int?>(null) }
        var editingTodoText by remember { mutableStateOf("") }


        var newTodoText by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.plofil),
                    contentDescription = "프로필 사진",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(36.dp))
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "사용자 이름",
                        fontSize = 20.sp
                    )

                    Text(
                        text = SimpleDateFormat(
                            "yyyy.MM.dd",
                            Locale.getDefault()
                        ).format(Date()),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            PledgeBox()
        }


        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(12f)
                .padding(16.dp)
        ) {
            Text("Todo 리스트", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("할 일 입력") }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (newTodoText.isNotBlank()) {
                            todoList = todoList + TodoItem(
                                id = (todoList.maxOfOrNull { it.id } ?: 0) + 1,
                                text = newTodoText
                            )

                            newTodoText = ""
                        }
                    }
                ) {
                    Text("추가")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            )
            {
                items(todoList) { todo ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = todo.completed,
                            onCheckedChange = { checked ->
                                todoList = todoList.map {
                                    if (it.id == todo.id)
                                        it.copy(completed = checked)
                                    else it
                                }
                            }
                        )

                        Text(
                            text = todo.text,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(onClick = {
                            editingTodoId = todo.id
                            editingTodoText = todo.text
                        }) {
                            Text("수정")
                        }

                        TextButton(onClick = {
                            todoList = todoList.filter { it.id != todo.id }
                        }) {
                            Text("삭제")
                        }
                    }
                }
            }
            if (editingTodoId != null) {
                AlertDialog(
                    onDismissRequest = { editingTodoId = null },
                    title = { Text("할 일 수정") },
                    text = {
                        TextField(
                            value = editingTodoText,
                            onValueChange = { editingTodoText = it }
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            todoList = todoList.map {
                                if (it.id == editingTodoId)
                                    it.copy(text = editingTodoText)
                                else it
                            }
                            editingTodoId = null
                        }) {
                            Text("저장")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { editingTodoId = null }) {
                            Text("취소")
                        }
                    }
                )
            }


        }
    }
    }


    @Composable
    fun PledgeBox() {

        var pledgeText by rememberSaveable { mutableStateOf("") }
        var showEditDialog by remember { mutableStateOf(false) }


        var editingText by rememberSaveable(
            stateSaver = TextFieldValue.Saver
        ) {
            mutableStateOf(TextFieldValue(""))
        }

        val focusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 1.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = if (pledgeText.isBlank())
                        "오늘의 다짐 한 마디"
                    else
                        pledgeText,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    color = if (pledgeText.isBlank())
                        Color.Gray
                    else
                        Color.Black
                )

                IconButton(
                    onClick = {
                        editingText = TextFieldValue(pledgeText)
                        showEditDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "다짐 수정",
                        tint = Color.Gray
                    )
                }
            }
        }

        if (showEditDialog) {

            LaunchedEffect(showEditDialog) {
                focusRequester.requestFocus()
            }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("오늘의 다짐 수정") },
                text = {
                    TextField(
                        value = editingText,
                        onValueChange = { editingText = it },
                        placeholder = { Text("오늘의 다짐 한 마디") },
                        singleLine = true,
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            pledgeText = editingText.text
                            showEditDialog = false
                        }
                    ) {
                        Text("저장")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showEditDialog = false }
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }



