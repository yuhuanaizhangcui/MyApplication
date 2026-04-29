package com.wham.moo.ui.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import com.wham.moo.ui.components.StellaCard
import com.wham.moo.ui.theme.StellaAccent
import com.wham.moo.ui.theme.StellaPrimary
import com.wham.moo.ui.theme.StellaPrimaryContainer
import com.wham.moo.ui.theme.StellaSoft
import com.wham.moo.ui.theme.StellaTextLight
import com.wham.moo.ui.theme.StellaTextMain
import com.wham.moo.ui.theme.StellaTextSub
import com.wham.moo.ui.viewmodel.StellaViewModel

@Composable
fun ProfileScreen(viewModel: StellaViewModel) {
    val diaryCount by viewModel.diaryCount.collectAsState()
    val meditationCount by viewModel.meditationCount.collectAsState()
    val completedWishCount by viewModel.completedWishCount.collectAsState()

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
                    .background(StellaPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = StellaPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "星愿用户",
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
                        text = "连续打卡 21 天",
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
            MenuItem(icon = Icons.Default.Settings, label = "设置", onClick = {})
            MenuItem(icon = Icons.Default.Notifications, label = "提醒管理", onClick = {})
            MenuItem(icon = Icons.Default.Favorite, label = "关于我们", onClick = {})
        }
        Spacer(modifier = Modifier.height(80.dp))
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