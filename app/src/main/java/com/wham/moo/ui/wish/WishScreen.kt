package com.wham.moo.ui.wish

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wham.moo.data.entity.Wish
import com.wham.moo.ui.components.ProgressBar
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.DoneGreen
import com.wham.moo.ui.theme.StellaAccent
import com.wham.moo.ui.theme.StellaAccentLight
import com.wham.moo.ui.theme.StellaBg2
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel

@Composable
fun WishScreen(viewModel: StellaViewModel) {
    val wishes by viewModel.allWishes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "星愿清单",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "心怀期许，慢慢发光",
            fontSize = 15.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StellaAccentLight, contentColor = StellaAccent)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("添加新愿望", fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (wishes.isEmpty()) {
            Text(
                text = "还没有愿望，许个愿吧 🌟",
                fontSize = 14.sp,
                color = StellaTextLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                wishes.sortedByDescending { it.id }.forEach { wish ->
                    WishCard(wish = wish, onToggle = { viewModel.toggleWishStatus(wish) }, onDelete = { viewModel.deleteWish(wish.id) })
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showDialog) {
        AddWishDialog(
            onDismiss = { showDialog = false },
            onConfirm = { title, progress ->
                viewModel.addWish(title, progress)
                showDialog = false
            }
        )
    }
}

@Composable
fun WishCard(wish: Wish, onToggle: () -> Unit, onDelete: () -> Unit) {
    val (statusLabel, statusBg, statusTextColor) = when (wish.status) {
        "done" -> Triple("已完成", Color(0xFFD1FAE5), Color(0xFF059669))
        "active" -> Triple("进行中", Color(0xFFFFE8DD), StellaPrimary)
        else -> Triple("待完成", StellaBg2, StellaTextLight)
    }
    val barColor = when (wish.status) {
        "done" -> DoneGreen
        "active" -> StellaPrimary
        else -> StellaAccentLight
    }
    val textDecoration = if (wish.status == "done") androidx.compose.ui.text.style.TextDecoration.LineThrough else null

    StellaCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = wish.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = if (wish.status == "done") StellaTextLight else StellaTextMain,
                    textDecoration = textDecoration,
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(statusLabel, fontSize = 10.sp, color = statusTextColor, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "删除",
                        tint = StellaTextLight,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onDelete() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ProgressBar(progress = wish.progress, progressColor = barColor)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${wish.progress}%", fontSize = 11.sp, color = StellaTextLight)
                Text(
                    text = if (wish.status == "done") "重新开启" else "标记完成",
                    fontSize = 11.sp,
                    color = StellaPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onToggle() }
                )
            }
        }
    }
}

@Composable
fun AddWishDialog(onDismiss: () -> Unit, onConfirm: (String, Int) -> Unit) {
    var title by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(0f) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { onDismiss() }, contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color.White)
                    .padding(24.dp)
                    .clickable(enabled = false) { }
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(StellaBg2))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("添加新愿望", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StellaTextMain)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("写下你的心愿...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = StellaSoft,
                        unfocusedContainerColor = StellaSoft,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("当前进度", fontSize = 13.sp, color = StellaTextSub)
                Spacer(modifier = Modifier.height(4.dp))
                Slider(
                    value = progress,
                    onValueChange = { progress = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(thumbColor = StellaPrimary, activeTrackColor = StellaPrimary)
                )
                Text("${progress.toInt()}%", fontSize = 12.sp, color = StellaTextLight, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text("取消", color = StellaTextSub)
                    }
                    Button(
                        onClick = { onConfirm(title, progress.toInt()) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
                    ) {
                        Text("保存", color = Color.White)
                    }
                }
            }
        }
    }
}