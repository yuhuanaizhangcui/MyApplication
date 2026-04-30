package com.wham.moo.ui.diary

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.wham.moo.ui.theme.BlueMood
import com.wham.moo.ui.theme.BlueMoodBg
import com.wham.moo.ui.theme.GreenMood
import com.wham.moo.ui.theme.GreenMoodBg
import com.wham.moo.ui.theme.OrangeMood
import com.wham.moo.ui.theme.OrangeMoodBg
import com.wham.moo.ui.theme.PurpleMood
import com.wham.moo.ui.theme.PurpleMoodBg
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.moodToLabel

/**
 * 日记详情页面
 * @param diaryId 日记ID
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId: Long,
    diaryContent: String,
    diaryMood: String,
    diaryTime: String,
    diaryDate: String,
    diaryImageUris: String,
    onDelete: (Long) -> Unit
) {
    val images = remember(diaryImageUris) {
        diaryImageUris.split(",").filter { it.isNotBlank() }
    }
    var fullscreenImageIndex by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 顶部导航栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = StellaTextMain
                    )
                }
                Text(
                    text = "日记详情",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StellaTextMain,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                // 右侧占位保持居中
                IconButton(onClick = {
                    onDelete(diaryId)
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.Transparent
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // 日期和心情
                val (moodIcon, moodColor, moodBgColor) = getMoodConfig(diaryMood)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(moodBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = moodIcon,
                            contentDescription = null,
                            tint = moodColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${diaryDate}  ${diaryTime}  ·  ${moodToLabel(diaryMood)}",
                        fontSize = 13.sp,
                        color = StellaTextSub
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 日记正文
                Text(
                    text = diaryContent,
                    fontSize = 16.sp,
                    lineHeight = 26.sp,
                    color = StellaTextMain
                )

                // 图片网格
                if (images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailImageGrid(
                        imageUris = images,
                        onImageClick = { index -> fullscreenImageIndex = index }
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // 全屏图片预览
        if (fullscreenImageIndex >= 0 && fullscreenImageIndex < images.size) {
            FullscreenImagePreview(
                images = images,
                currentIndex = fullscreenImageIndex,
                onDismiss = { fullscreenImageIndex = -1 },
                onPrevious = {
                    if (fullscreenImageIndex > 0) fullscreenImageIndex--
                },
                onNext = {
                    if (fullscreenImageIndex < images.size - 1) fullscreenImageIndex++
                }
            )
        }
    }
}

@Composable
fun DetailImageGrid(
    imageUris: List<String>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (imageUris.isEmpty()) return

    val count = imageUris.size
    val columns = when {
        count == 1 -> 1
        count == 2 || count == 4 -> 2
        else -> 3
    }
    val spacing = 4.dp

    if (count == 1) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onImageClick(0) }
        ) {
            AsyncImage(
                model = imageUris[0],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            imageUris.chunked(columns).forEachIndexed { rowIdx, row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEachIndexed { colIdx, uri ->
                        val globalIndex = rowIdx * columns + colIdx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageClick(globalIndex) }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    repeat(columns - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * 全屏图片预览，支持左右滑动切换
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FullscreenImagePreview(
    images: List<String>,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(currentIndex) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = images.getOrNull(currentPage),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // 点击左半部分返回或上一张
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (currentPage > 0) currentPage-- else onDismiss()
                }
        )

        // 点击右半部分下一张
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (currentPage < images.size - 1) currentPage++
                }
        )

        // 左箭头
        if (currentPage > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { currentPage-- },
                contentAlignment = Alignment.Center
            ) {
                Text("<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 右箭头
        if (currentPage < images.size - 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { currentPage++ },
                contentAlignment = Alignment.Center
            ) {
                Text(">", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 页码指示
        Text(
            text = "${currentPage + 1} / ${images.size}",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}

private fun getMoodConfig(mood: String): Triple<ImageVector, Color, Color> {
    return when (mood) {
        "happy" -> Triple(Icons.Default.SentimentVerySatisfied, OrangeMood, OrangeMoodBg)
        "sad" -> Triple(Icons.Default.SentimentDissatisfied, BlueMood, BlueMoodBg)
        "anxious" -> Triple(Icons.Default.MoodBad, PurpleMood, PurpleMoodBg)
        else -> Triple(Icons.Default.SentimentSatisfied, GreenMood, GreenMoodBg)
    }
}
