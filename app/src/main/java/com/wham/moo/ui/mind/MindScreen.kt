package com.wham.moo.ui.mind

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.DoneGreen
import com.wham.moo.ui.theme.StellaAccent
import com.wham.moo.ui.theme.StellaBg2
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaPrimaryContainer
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel
import kotlinx.coroutines.delay

@Composable
fun MindScreen(viewModel: StellaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "正念冥想",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "慢下来，感受呼吸",
            fontSize = 15.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        MeditationTimerCard(viewModel)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AudioCard(
                title = "白噪音",
                subTitle = "雨声助眠",
                icon = Icons.Default.MusicNote,
                tint = Color(0xFF14B8A6),
                bg = Color(0xFFF0FDFA),
                audioUrl = "https://cdn.pixabay.com/download/audio/2022/03/24/audio_c8c8a73467.mp3"
            )
            AudioCard(
                title = "治愈放松",
                subTitle = "轻音乐",
                icon = Icons.Default.Eco,
                tint = DoneGreen,
                bg = Color(0xFFF0FDF4),
                audioUrl = "https://cdn.pixabay.com/download/audio/2022/05/27/audio_1808fbf07a.mp3"
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun AudioCard(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    bg: Color,
    audioUrl: String
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    fun play() {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { it.start() }
            mediaPlayer.setOnCompletionListener { isPlaying = false }
            isPlaying = true
        } catch (_: Exception) {
            isPlaying = false
        }
    }

    fun pause() {
        if (mediaPlayer.isPlaying) mediaPlayer.pause()
        isPlaying = false
    }

    StellaCard(modifier = modifier, onClick = { if (isPlaying) pause() else play() }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
                    }
                } else {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
                }
            }
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 8.dp))
            Text(subTitle, fontSize = 11.sp, color = StellaTextSub)
        }
    }
}

@Composable
fun MeditationTimerCard(viewModel: StellaViewModel) {
    var selectedMinutes by remember { mutableIntStateOf(3) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(selectedMinutes * 60) }
    var showDone by remember { mutableStateOf(false) }

    val totalSeconds = selectedMinutes * 60
    val progress = if (isRunning || showDone) remainingSeconds / totalSeconds.toFloat() else 1f

    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breatheProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breathe"
    )

    val breatheScale = 1f + breatheProgress * 0.35f
    val breatheAlpha = 0.5f + breatheProgress * 0.5f
    val ringScale = 1f + breatheProgress * 0.6f
    val ringAlpha = 0.4f - breatheProgress * 0.4f
    val outerRingScale = 1f + breatheProgress * 0.9f
    val outerRingAlpha = 0.25f - breatheProgress * 0.25f

    val breatheText = when {
        breatheProgress < 0.375f -> "吸气"
        breatheProgress < 0.5f -> "屏息"
        breatheProgress < 0.875f -> "呼气"
        else -> "停息"
    }

    LaunchedEffect(isRunning) {
        while (isRunning && remainingSeconds > 0) {
            delay(1000)
            remainingSeconds--
        }
        if (isRunning && remainingSeconds <= 0) {
            isRunning = false
            showDone = true
            viewModel.addMeditationSession(selectedMinutes)
        }
    }

    DisposableEffect(Unit) {
        onDispose { isRunning = false }
    }

    StellaCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isRunning) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val baseRadius = size.minDimension / 2 - 16.dp.toPx()

                        drawCircle(
                            color = StellaPrimary.copy(alpha = outerRingAlpha),
                            radius = baseRadius * outerRingScale,
                            center = Offset(centerX, centerY)
                        )
                        drawCircle(
                            color = StellaPrimary.copy(alpha = ringAlpha),
                            radius = baseRadius * ringScale,
                            center = Offset(centerX, centerY)
                        )
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = StellaBg2,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = StellaPrimary,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(if (isRunning) breatheScale else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        StellaPrimary.copy(alpha = if (isRunning) breatheAlpha else 0.2f),
                                        StellaPrimary.copy(alpha = if (isRunning) breatheAlpha * 0.5f else 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = StellaPrimary.copy(alpha = if (isRunning) 1f else 0.6f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Text(
                text = if (showDone) "练习完成 🎉" else if (isRunning) breatheText else "深呼吸练习",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = if (isRunning) StellaPrimary else StellaTextMain,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = if (isRunning) formatTime(remainingSeconds) else "$selectedMinutes 分钟",
                fontSize = 14.sp,
                color = StellaTextSub,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DurationChip(3, selectedMinutes, isRunning) {
                    if (!isRunning) { selectedMinutes = 3; remainingSeconds = 180; showDone = false }
                }
                DurationChip(5, selectedMinutes, isRunning) {
                    if (!isRunning) { selectedMinutes = 5; remainingSeconds = 300; showDone = false }
                }
                DurationChip(10, selectedMinutes, isRunning) {
                    if (!isRunning) { selectedMinutes = 10; remainingSeconds = 600; showDone = false }
                }
            }

            Button(
                onClick = {
                    if (isRunning) {
                        isRunning = false
                        remainingSeconds = selectedMinutes * 60
                    } else if (showDone) {
                        showDone = false
                        remainingSeconds = selectedMinutes * 60
                    } else {
                        isRunning = true
                        remainingSeconds = selectedMinutes * 60
                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
            ) {
                Text(
                    text = when {
                        isRunning -> "结束练习"
                        showDone -> "再来一次"
                        else -> "开始练习"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DurationChip(minutes: Int, selected: Int, disabled: Boolean, onClick: () -> Unit) {
    val isSelected = minutes == selected
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) StellaPrimary else StellaBg2)
            .clickable(enabled = !disabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = "$minutes min",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else StellaTextSub
        )
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%d:%02d", m, s)
}