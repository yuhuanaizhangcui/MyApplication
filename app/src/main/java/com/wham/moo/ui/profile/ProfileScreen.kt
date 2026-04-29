package com.wham.moo.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.StellaAccent
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaPrimaryContainer
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel

data class AvatarOption(
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

val avatarOptions = listOf(
    AvatarOption(Icons.Default.Person, StellaPrimary, StellaPrimaryContainer),
    AvatarOption(Icons.Default.Star, Color(0xFFF97316), Color(0xFFFFF7ED)),
    AvatarOption(Icons.Default.Favorite, Color(0xFFEC4899), Color(0xFFFDF2F8)),
    AvatarOption(Icons.Default.Eco, Color(0xFF10B981), Color(0xFFECFDF5)),
    AvatarOption(Icons.Default.Face, Color(0xFF3B82F6), Color(0xFFEFF6FF))
)

@Composable
fun ProfileScreen(viewModel: StellaViewModel) {
    val diaryCount by viewModel.diaryCount.collectAsState()
    val meditationCount by viewModel.meditationCount.collectAsState()
    val completedWishCount by viewModel.completedWishCount.collectAsState()
    val nickname by viewModel.userNickname.collectAsState()
    val avatarIdx by viewModel.avatarIndex.collectAsState()

    var showAvatarPicker by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showReminders by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    val avatar = avatarOptions.getOrElse(avatarIdx) { avatarOptions[0] }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(avatar.bgColor)
                    .clickable { showAvatarPicker = true }
                    .border(2.dp, avatar.iconColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = avatar.icon,
                    contentDescription = null,
                    tint = avatar.iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.clickable { showNicknameDialog = true }) {
                Text(
                    text = nickname,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = StellaTextMain
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = StellaPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "点击编辑资料",
                        fontSize = 13.sp,
                        color = StellaTextSub
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        StellaCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(diaryCount.toString(), "日记总数", StellaPrimary)
                StatItem(meditationCount.toString(), "正念次数", StellaAccent)
                StatItem(completedWishCount.toString(), "完成愿望", Color(0xFFF97316))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MenuItem(icon = Icons.Default.Settings, label = "设置", onClick = { showSettings = true })
            MenuItem(icon = Icons.Default.Notifications, label = "提醒管理", onClick = { showReminders = true })
            MenuItem(icon = Icons.Default.Info, label = "关于我们", onClick = { showAbout = true })
        }
        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            selectedIndex = avatarIdx,
            onSelect = {
                viewModel.setAvatarIndex(it)
                showAvatarPicker = false
            },
            onDismiss = { showAvatarPicker = false }
        )
    }

    if (showNicknameDialog) {
        NicknameDialog(
            current = nickname,
            onConfirm = {
                viewModel.setNickname(it)
                showNicknameDialog = false
            },
            onDismiss = { showNicknameDialog = false }
        )
    }

    if (showSettings) {
        SettingsDialog(onDismiss = { showSettings = false })
    }

    if (showReminders) {
        ReminderDialog(onDismiss = { showReminders = false })
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
fun AvatarPickerDialog(selectedIndex: Int, onSelect: (Int) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        StellaCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("选择头像", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = StellaTextMain)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    avatarOptions.forEachIndexed { index, option ->
                        val selected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(option.bgColor)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) option.iconColor else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onSelect(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(option.icon, contentDescription = null, tint = option.iconColor, modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
                ) {
                    Text("取消", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun NicknameDialog(current: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(current) }
    Dialog(onDismissRequest = onDismiss) {
        StellaCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("修改昵称", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = StellaTextMain)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StellaPrimary,
                        unfocusedBorderColor = StellaSoft
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StellaSoft)
                    ) {
                        Text("取消", color = StellaTextSub)
                    }
                    Button(
                        onClick = { if (text.isNotBlank()) onConfirm(text.trim()) },
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

@Composable
fun SettingsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        StellaCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("设置", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = StellaTextMain)
                Spacer(modifier = Modifier.height(16.dp))
                var darkMode by remember { mutableStateOf(false) }
                SettingsSwitchItem("深色模式", darkMode) { darkMode = it }
                Spacer(modifier = Modifier.height(8.dp))
                var soundEnabled by remember { mutableStateOf(true) }
                SettingsSwitchItem("音效", soundEnabled) { soundEnabled = it }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
                ) {
                    Text("完成", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = StellaTextMain)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = StellaPrimary, checkedTrackColor = StellaPrimary.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun ReminderDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        StellaCard {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("提醒管理", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = StellaTextMain)
                Spacer(modifier = Modifier.height(16.dp))
                var reminderOn by remember { mutableStateOf(false) }
                SettingsSwitchItem("每日正念提醒", reminderOn) { reminderOn = it }
                Spacer(modifier = Modifier.height(12.dp))
                if (reminderOn) {
                    Text("提醒时间", fontSize = 13.sp, color = StellaTextSub)
                    Spacer(modifier = Modifier.height(4.dp))
                    var hour by remember { mutableStateOf(21f) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${hour.toInt()}:00", fontSize = 15.sp, color = StellaTextMain, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = hour,
                            onValueChange = { hour = it },
                            valueRange = 6f..23f,
                            modifier = Modifier.weight(1f),
                            colors = androidx.compose.material3.SliderDefaults.colors(thumbColor = StellaPrimary, activeTrackColor = StellaPrimary)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
                ) {
                    Text("完成", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        StellaCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StellaPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = StellaPrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("星愿 Stella", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StellaTextMain)
                Text("版本 1.0.0", fontSize = 13.sp, color = StellaTextSub, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "星愿是一款温暖治愈的心灵伴侣应用，帮助你记录心情、许下愿望、练习正念冥想。愿每一颗星星都照亮你的心愿。",
                    fontSize = 14.sp,
                    color = StellaTextSub,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StellaPrimary)
                ) {
                    Text("知道了", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = StellaTextSub,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    StellaCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = StellaTextSub, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = StellaTextMain)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = StellaTextLight,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}