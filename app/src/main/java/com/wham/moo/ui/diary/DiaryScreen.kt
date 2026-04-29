package com.wham.moo.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.BlueMood
import com.wham.moo.ui.theme.BlueMoodBg
import com.wham.moo.ui.theme.GreenMood
import com.wham.moo.ui.theme.GreenMoodBg
import com.wham.moo.ui.theme.OrangeMood
import com.wham.moo.ui.theme.OrangeMoodBg
import com.wham.moo.ui.theme.PurpleMood
import com.wham.moo.ui.theme.PurpleMoodBg
import com.wham.moo.ui.theme.StellaBg2
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel
import com.wham.moo.ui.viewmodel.formatDate
import java.util.Calendar
import java.util.Locale

@Composable
fun DiaryScreen(viewModel: StellaViewModel) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val diaries by viewModel.diariesForSelectedDate.collectAsState()
    var selectedMood by remember { mutableStateOf("calm") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "情绪日记",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "记录当下，接纳所有情绪",
            fontSize = 15.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        DateSelector(selectedDate = selectedDate, onSelect = { viewModel.selectDate(it) })
        Spacer(modifier = Modifier.height(16.dp))

        WriteCard(
            selectedMood = selectedMood,
            onMoodSelect = { selectedMood = it },
            content = content,
            onContentChange = { content = it },
            onSave = {
                if (content.isNotBlank()) {
                    viewModel.addDiary(content, selectedMood, selectedDate)
                    content = ""
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "历史日记",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        if (diaries.isEmpty()) {
            Text(
                text = "这一天还没有日记 🌙",
                fontSize = 14.sp,
                color = StellaTextLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                diaries.forEach { diary ->
                    DiaryListItem(diary = diary, onDelete = { viewModel.deleteDiary(diary.id) })
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun DateSelector(selectedDate: String, onSelect: (String) -> Unit) {
    val today = Calendar.getInstance()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in -3..3) {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, i) }
            val dateStr = formatDate(cal)
            val isSelected = dateStr == selectedDate
            val label = when (i) {
                0 -> "今天"
                else -> cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = StellaTextLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) StellaPrimary else Color.White)
                        .clickable { onSelect(dateStr) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${cal.get(Calendar.DAY_OF_MONTH)}",
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else StellaTextMain
                    )
                }
            }
        }
    }
}



@Composable
fun WriteCard(
    selectedMood: String,
    onMoodSelect: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit
) {
    StellaCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WriteMoodOption("calm", Icons.Default.SentimentSatisfied, GreenMood, GreenMoodBg, selectedMood == "calm") { onMoodSelect("calm") }
                WriteMoodOption("happy", Icons.Default.SentimentVerySatisfied, OrangeMood, OrangeMoodBg, selectedMood == "happy") { onMoodSelect("happy") }
                WriteMoodOption("sad", Icons.Default.SentimentDissatisfied, BlueMood, BlueMoodBg, selectedMood == "sad") { onMoodSelect("sad") }
                WriteMoodOption("anxious", Icons.Default.MoodBad, PurpleMood, PurpleMoodBg, selectedMood == "anxious") { onMoodSelect("anxious") }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(StellaSoft)
                    .padding(12.dp)
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxSize(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, color = StellaTextMain),
                    decorationBox = { innerTextField ->
                        if (content.isEmpty()) {
                            Text("写下今天的心情与感悟...", fontSize = 15.sp, color = StellaTextLight)
                        }
                        innerTextField()
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = StellaTextSub, modifier = Modifier.size(20.dp))
                    Icon(Icons.Default.Label, contentDescription = null, tint = StellaTextSub, modifier = Modifier.size(20.dp))
                }
                TextButton(
                    onClick = onSave,
                    shape = RoundedCornerShape(20.dp),
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(containerColor = StellaPrimary, contentColor = Color.White)
                ) {
                    Text("保存", fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}

@Composable
fun WriteMoodOption(mood: String, icon: ImageVector, iconColor: Color, bgColor: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (selected) bgColor else StellaBg2)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = mood,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun DiaryListItem(diary: DiaryEntry, onDelete: () -> Unit) {
    val (icon, iconColor, bgColor) = when (diary.mood) {
        "happy" -> Triple(Icons.Default.SentimentVerySatisfied, OrangeMood, OrangeMoodBg)
        "sad" -> Triple(Icons.Default.SentimentDissatisfied, BlueMood, BlueMoodBg)
        "anxious" -> Triple(Icons.Default.MoodBad, PurpleMood, PurpleMoodBg)
        else -> Triple(Icons.Default.SentimentSatisfied, GreenMood, GreenMoodBg)
    }
    StellaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = diary.time, fontSize = 11.sp, color = StellaTextLight)
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = StellaTextLight,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onDelete() }
                    )
                }
                Text(
                    text = diary.content,
                    fontSize = 14.sp,
                    color = StellaTextMain,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}