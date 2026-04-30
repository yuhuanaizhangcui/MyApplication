package com.wham.moo.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember
import coil.compose.AsyncImage
import androidx.navigation.NavController
import com.wham.moo.data.entity.DiaryEntry
import com.wham.moo.data.entity.Wish
import com.wham.moo.ui.components.MoodIcon
import com.wham.moo.ui.components.SectionHeader
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.BlueMood
import com.wham.moo.ui.theme.BlueMoodBg
import com.wham.moo.ui.theme.GreenMood
import com.wham.moo.ui.theme.GreenMoodBg
import com.wham.moo.ui.theme.OrangeMood
import com.wham.moo.ui.theme.OrangeMoodBg
import com.wham.moo.ui.theme.PurpleMood
import com.wham.moo.ui.theme.PurpleMoodBg
import com.wham.moo.ui.theme.StellaAccent
import com.wham.moo.ui.theme.StellaAccentLight
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaPrimaryContainer
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(viewModel: StellaViewModel, navController: NavController) {
    val diaries by viewModel.allDiaries.collectAsState()
    val weekData by viewModel.weekChartData.collectAsState()
    val diaryCount by viewModel.diaryCount.collectAsState()
    val wishes by viewModel.allWishes.collectAsState()
    val today = currentDate()
    val todayMood = diaries.find { it.date == today && it.content.startsWith("今日心情：") }?.mood

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        GreetingHeader()
        Spacer(modifier = Modifier.height(16.dp))
        MoodSelector(selectedMood = todayMood) { mood ->
            viewModel.addOrUpdateMood(mood, today)
        }
        Spacer(modifier = Modifier.height(16.dp))
        WeekChartCard(weekData, diaryCount)
        Spacer(modifier = Modifier.height(16.dp))
        QuickActions(navController, wishes.firstOrNull())
        Spacer(modifier = Modifier.height(20.dp))
        SectionHeader(title = "最近日记")
        RecentDiaries(diaries.filter { !it.content.startsWith("今日心情：") }.take(3), navController)
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun GreetingHeader() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "早上好"
        hour < 18 -> "下午好"
        else -> "晚上好"
    }
    val today = Calendar.getInstance()
    val month = today.get(Calendar.MONTH) + 1
    val day = today.get(Calendar.DAY_OF_MONTH)
    val weekDay = when (today.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "周一"
        Calendar.TUESDAY -> "周二"
        Calendar.WEDNESDAY -> "周三"
        Calendar.THURSDAY -> "周四"
        Calendar.FRIDAY -> "周五"
        Calendar.SATURDAY -> "周六"
        Calendar.SUNDAY -> "周日"
        else -> ""
    }
    Column {
        Text(
            text = "$greeting ✨",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = StellaTextMain
        )
        Text(
            text = "$month 月 $day 日  $weekDay",
            fontSize = 14.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "愿今日温柔相伴，内心平静",
            fontSize = 15.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun MoodSelector(selectedMood: String?, onSelect: (String) -> Unit) {
    StellaCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "今日心情",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = StellaTextMain
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodItem(
                    "平静",
                    Icons.Default.SentimentSatisfied,
                    GreenMood,
                    GreenMoodBg,
                    selectedMood == "calm"
                ) { onSelect("calm") }
                MoodItem(
                    "开心",
                    Icons.Default.SentimentVerySatisfied,
                    OrangeMood,
                    OrangeMoodBg,
                    selectedMood == "happy"
                ) { onSelect("happy") }
                MoodItem(
                    "低落",
                    Icons.Default.SentimentDissatisfied,
                    BlueMood,
                    BlueMoodBg,
                    selectedMood == "sad"
                ) { onSelect("sad") }
                MoodItem(
                    "焦虑",
                    Icons.Default.MoodBad,
                    PurpleMood,
                    PurpleMoodBg,
                    selectedMood == "anxious"
                ) { onSelect("anxious") }
            }
        }
    }
}

@Composable
fun MoodItem(
    label: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (selected) bgColor else bgColor.copy(alpha = 0.5f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (selected) iconColor else StellaTextSub,
            modifier = Modifier.padding(top = 6.dp),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun WeekChartCard(weekData: List<Int>, totalCount: Int) {
    val maxVal = weekData.maxOrNull()?.coerceAtLeast(1) ?: 1
    val colors = listOf(
        StellaPrimary,
        StellaAccentLight,
        BlueMood.copy(alpha = 0.7f),
        PurpleMood.copy(alpha = 0.7f),
        StellaPrimary,
        StellaAccentLight,
        StellaPrimary
    )

    StellaCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "情绪本周统计",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = StellaTextMain
                )
                Text(
                    text = "总计 ${totalCount} 篇日记",
                    fontSize = 12.sp,
                    color = StellaTextLight
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weekData.forEachIndexed { index, value ->
                    val heightFraction = (value.toFloat() / maxVal).coerceAtLeast(0.08f)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (value > 0) {
                            Text(
                                text = "$value",
                                fontSize = 10.sp,
                                color = StellaTextLight,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .fillMaxWidth()
                                .height((heightFraction * 80).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(colors[index])
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("一", "二", "三", "四", "五", "六", "日").forEach {
                    Text(text = it, fontSize = 12.sp, color = StellaTextLight)
                }
            }
        }
    }
}

@Composable
fun QuickActions(navController: NavController, latestWish: Wish?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StellaCard(
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate("mind") }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(StellaPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = StellaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "正念冥想",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "放松身心",
                    fontSize = 12.sp,
                    color = StellaTextSub,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        StellaCard(
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate("wish") }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(StellaAccentLight.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StellaAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "我的愿望",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = latestWish?.title ?: "还没有愿望，去许一个吧",
                    fontSize = 12.sp,
                    color = StellaTextSub,
                    modifier = Modifier.padding(top = 2.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RecentDiaries(diaries: List<DiaryEntry>, navController: NavController) {
    if (diaries.isEmpty()) {
        Text(
            text = "还没有日记，去写一篇吧 ✍️",
            fontSize = 14.sp,
            color = StellaTextLight,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            diaries.forEach { diary ->
                DiaryCard(diary, navController)
            }
        }
    }
}

@Composable
fun DiaryCard(diary: DiaryEntry, navController: NavController) {
    val (icon, iconColor, bgColor) = moodConfig(diary.mood)
    val images = remember(diary.imageUris) {
        diary.imageUris.split(",").filter { it.isNotBlank() }
    }
    StellaCard(
        onClick = {
            val encodedUris = android.net.Uri.encode(diary.imageUris)
            val encodedContent = android.net.Uri.encode(diary.content)
            navController.navigate(
                "diary_detail/${diary.id}/$encodedContent/${diary.mood}/${diary.time}/${diary.date}/$encodedUris"
            )
        }
    ) {
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${diary.date} · ${diary.time}",
                    fontSize = 11.sp,
                    color = StellaTextLight
                )
                Text(
                    text = diary.content,
                    fontSize = 14.sp,
                    color = StellaTextMain,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )
                if (images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HomeImageGrid(imageUris = images)
                }
            }
        }
    }
}

fun moodConfig(mood: String): Triple<ImageVector, Color, Color> {
    return when (mood) {
        "happy" -> Triple(Icons.Default.SentimentVerySatisfied, OrangeMood, OrangeMoodBg)
        "sad" -> Triple(Icons.Default.SentimentDissatisfied, BlueMood, BlueMoodBg)
        "anxious" -> Triple(Icons.Default.MoodBad, PurpleMood, PurpleMoodBg)
        else -> Triple(Icons.Default.SentimentSatisfied, GreenMood, GreenMoodBg)
    }
}

fun currentDate(): String {
    val cal = Calendar.getInstance()
    return String.format(Locale.getDefault(), "%04d-%02d-%02d",
        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
}

/**
 * 首页日记卡片中的图片九宫格组件（最多展示3张，保持卡片紧凑）
 */
@Composable
fun HomeImageGrid(imageUris: List<String>, modifier: Modifier = Modifier) {
    if (imageUris.isEmpty()) return

    // 首页卡片最多展示3张图，保持紧凑
    val displayUris = imageUris.take(3)
    val count = displayUris.size
    val columns = if (count == 1) 1 else 3
    val spacing = 4.dp

    if (count == 1) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.5f)
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = displayUris[0],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            displayUris.forEach { uri ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp))
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        // 如果超过3张，显示剩余数量提示
        if (imageUris.size > 3) {
            Text(
                text = "共 ${imageUris.size} 张图片",
                fontSize = 11.sp,
                color = StellaTextLight,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}