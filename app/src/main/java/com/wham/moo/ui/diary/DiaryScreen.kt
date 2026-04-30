package com.wham.moo.ui.diary

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import android.widget.Toast
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.navigation.NavController
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryScreen(viewModel: StellaViewModel, navController: NavController) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val diaries by viewModel.diariesForSelectedDate.collectAsState()
    var selectedMood by remember { mutableStateOf("calm") }
    var content by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null) {
            val remaining = 9 - selectedImageUris.size
            if (remaining > 0) {
                val newUris = uris.take(remaining).map { it.toString() }
                selectedImageUris = selectedImageUris + newUris
            }
        }
    }

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
            imageUris = selectedImageUris,
            onAddImage = {
                if (selectedImageUris.size >= 9) {
                    Toast.makeText(context, "最多只能选择9张图片", Toast.LENGTH_SHORT).show()
                    return@WriteCard
                }
                photoPickerLauncher.launch("image/*")
            },
            onRemoveImage = { index ->
                selectedImageUris = selectedImageUris.toMutableList().apply { removeAt(index) }
            },
            onReorderImages = { fromIndex, toIndex ->
                val newList = selectedImageUris.toMutableList()
                val item = newList.removeAt(fromIndex)
                newList.add(toIndex, item)
                selectedImageUris = newList
            },
            onSave = {
                if (content.isNotBlank()) {
                    viewModel.addDiary(content, selectedMood, selectedDate, selectedImageUris)
                    content = ""
                    selectedImageUris = emptyList()
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
                    DiaryListItem(
                        diary = diary,
                        onDelete = { viewModel.deleteDiary(diary.id) },
                        onClick = {
                            val encodedUris = android.net.Uri.encode(diary.imageUris)
                            val encodedContent = android.net.Uri.encode(diary.content)
                            navController.navigate(
                                "diary_detail/${diary.id}/$encodedContent/${diary.mood}/${diary.time}/${diary.date}/$encodedUris"
                            )
                        }
                    )
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
    imageUris: List<String>,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    onReorderImages: (Int, Int) -> Unit,
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
            if (imageUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                SelectedImagePreview(
                    imageUris = imageUris,
                    onRemove = onRemoveImage,
                    onAdd = onAddImage,
                    onReorder = onReorderImages,
                    canAddMore = imageUris.size < 9
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.clickable { onAddImage() }) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "插入图片",
                            tint = if (imageUris.size < 9) StellaTextSub else StellaTextLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedImagePreview(
    imageUris: List<String>,
    onRemove: (Int) -> Unit,
    onAdd: () -> Unit,
    onReorder: (Int, Int) -> Unit,
    canAddMore: Boolean
) {
    var reorderMode by remember { mutableStateOf(false) }
    var selectedForReorder by remember { mutableIntStateOf(-1) }

    if (reorderMode) {
        // 拖拽排序模式 - 显示已选中状态和提示
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedForReorder >= 0) "点击目标位置进行交换" else "长按图片开始排序",
                    fontSize = 12.sp,
                    color = StellaTextLight
                )
                Text(
                    text = "完成",
                    fontSize = 12.sp,
                    color = StellaPrimary,
                    modifier = Modifier.clickable {
                        reorderMode = false
                        selectedForReorder = -1
                    }
                )
            }
            DragReorderGrid(
                imageUris = imageUris,
                selectedForReorder = selectedForReorder,
                onImageClick = { index ->
                    if (selectedForReorder < 0) {
                        selectedForReorder = index
                    } else if (selectedForReorder != index) {
                        onReorder(selectedForReorder, index)
                        selectedForReorder = -1
                    } else {
                        selectedForReorder = -1
                    }
                },
                onRemove = { index ->
                    onRemove(index)
                    if (selectedForReorder >= imageUris.size - 1) selectedForReorder = -1
                }
            )
        }
    } else {
        // 正常模式
        val columns = 3
        val spacing = 4.dp

        val allItems = mutableListOf<Pair<String?, Int>>()
        imageUris.forEachIndexed { index, uri ->
            allItems.add(Pair(uri, index))
        }
        if (canAddMore) {
            allItems.add(Pair(null, -1))
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            allItems.chunked(columns).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    row.forEach { (uri, originalIndex) ->
                        if (uri != null) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = { reorderMode = true }
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onRemove(originalIndex) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "删除",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(StellaSoft)
                                    .clickable { onAdd() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "添加图片",
                                    tint = StellaTextLight,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
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
fun DiaryListItem(diary: DiaryEntry, onDelete: () -> Unit, onClick: () -> Unit) {
    val (icon, iconColor, bgColor) = when (diary.mood) {
        "happy" -> Triple(Icons.Default.SentimentVerySatisfied, OrangeMood, OrangeMoodBg)
        "sad" -> Triple(Icons.Default.SentimentDissatisfied, BlueMood, BlueMoodBg)
        "anxious" -> Triple(Icons.Default.MoodBad, PurpleMood, PurpleMoodBg)
        else -> Triple(Icons.Default.SentimentSatisfied, GreenMood, GreenMoodBg)
    }
    val images = remember(diary.imageUris) {
        diary.imageUris.split(",").filter { it.isNotBlank() }
    }
    StellaCard(
        onClick = onClick
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
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onDelete() }
                    )
                }
                Text(
                    text = diary.content,
                    fontSize = 14.sp,
                    color = StellaTextMain,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (images.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    WechatImageGrid(imageUris = images)
                }
            }
        }
    }
}

@Composable
fun WechatImageGrid(imageUris: List<String>, modifier: Modifier = Modifier) {
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
                .fillMaxWidth(0.65f)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
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
            modifier = modifier.fillMaxWidth(0.75f),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            imageUris.withIndex().chunked(columns).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { (index, uri) ->
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
                    repeat(columns - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * 拖拽排序模式的图片网格
 * 选中一张图片后点击另一张图片完成交换
 */
@Composable
fun DragReorderGrid(
    imageUris: List<String>,
    selectedForReorder: Int,
    onImageClick: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    val columns = 3
    val spacing = 4.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        imageUris.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                row.forEachIndexed { colIndex, uri ->
                    val globalIndex = (imageUris.indexOf(uri).coerceAtLeast(0))
                    val isSelected = selectedForReorder == globalIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isSelected) Modifier.border(
                                    2.dp, StellaPrimary, RoundedCornerShape(8.dp)
                                ) else Modifier
                            )
                            .clickable { onImageClick(globalIndex) }
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // 序号标记
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(2.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) StellaPrimary else Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${globalIndex + 1}",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        // 删除按钮
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onRemove(globalIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "删除",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
                repeat(columns - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}