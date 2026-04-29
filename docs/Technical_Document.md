# MyApplication 技术分析与架构设计文档

**项目名称**: Stella - 身心健康记录应用  
**技术栈**: Android + Kotlin + Jetpack Compose + Room  
**GitHub**: https://github.com/yuhuanaizhangcui/MyApplication  
**文档版本**: 1.0  
**更新日期**: 2026-04-29

---

## 一、项目概述

### 1.1 项目简介

Stella 是一款面向追求身心健康用户的生活记录应用，融合了情绪日记、愿望清单和正念冥想三大核心功能。应用名称 "Stella" 源自拉丁语，意为"星星"，寓意用户内心的光芒与期许。

### 1.2 核心功能

| 功能模块 | 描述 | 数据存储 |
|---------|------|---------|
| **情绪日记** | 记录每日心情，支持情绪标签（平静、开心、低落、焦虑） | DiaryEntry |
| **星愿清单** | 设定并追踪个人愿望，支持进度管理 | Wish |
| **正念冥想** | 内置冥想计时器，支持白噪音/轻音乐播放 | MeditationSession |

### 1.3 技术指标

- **最低 SDK**: 24 (Android 7.0)
- **目标 SDK**: 34 (Android 14)
- **编译 SDK**: 34
- **Java 版本**: 17
- **Kotlin 版本**: 采用版本目录管理

---

## 二、技术架构

### 2.1 架构模式：MVVM

本项目采用 **MVVM (Model-View-ViewModel)** 架构模式，遵循单向数据流原则：

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    View     │ ←── │  ViewModel  │ ←── │ Repository  │
│  (Compose)  │     │ (StateFlow) │     │  (Room)     │
└─────────────┘     └─────────────┘     └─────────────┘
```

### 2.2 分层架构

```
┌─────────────────────────────────────────────────────┐
│                    UI Layer                          │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │
│  │  Home   │ │  Diary  │ │  Wish   │ │  Mind   │  │
│  │ Screen  │ │ Screen  │ │ Screen  │ │ Screen  │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘  │
│                     ↓                                │
├─────────────────────────────────────────────────────┤
│                 ViewModel Layer                      │
│              StellaViewModel                          │
│   - StateFlow 数据管理                                │
│   - 协程处理 (viewModelScope)                        │
│                     ↓                                │
├─────────────────────────────────────────────────────┤
│                  Data Layer                          │
│              StellaRepository                        │
│                     ↓                                │
├─────────────────────────────────────────────────────┤
│               Persistence Layer                      │
│     ┌─────────────────────────────────────────┐     │
│     │           StellaDatabase (Room)          │     │
│     │  DiaryEntryDao │ WishDao │ MeditationDao│     │
│     └─────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────┘
```

### 2.3 关键技术选型

| 技术 | 版本/用途 | 说明 |
|------|----------|------|
| **Jetpack Compose** | BOM 2024.01.00 | 现代声明式 UI 框架 |
| **Room** | 2.6.1 | 本地 SQLite 数据库抽象 |
| **Navigation Compose** | 2.7.6 | 页面导航管理 |
| **Lifecycle ViewModel** | 2.7.0 | 生命周期感知组件 |
| **Kotlin Coroutines** | 1.7.3 | 异步编程 |
| **Material 3** | 1.2.0 | Material Design 3 组件库 |

---

## 三、数据模型设计

### 3.1 数据库 Schema

```kotlin
@Database(
    entities = [DiaryEntry::class, Wish::class, MeditationSession::class],
    version = 1,
    exportSchema = false
)
abstract class StellaDatabase : RoomDatabase() {
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun wishDao(): WishDao
    abstract fun meditationSessionDao(): MeditationSessionDao
}
```

### 3.2 核心实体

#### DiaryEntry (日记条目)
```kotlin
data class DiaryEntry(
    val id: Long = 0,
    val date: String,           // 格式: "yyyy-MM-dd"
    val time: String,           // 格式: "HH:mm"
    val mood: String,           // calm | happy | sad | anxious
    val content: String         // 日记内容
)
```

#### Wish (愿望)
```kotlin
data class Wish(
    val id: Long = 0,
    val title: String,          // 愿望标题
    val status: String,         // pending | active | done
    val progress: Int           // 进度 0-100
)
```

#### MeditationSession (冥想记录)
```kotlin
data class MeditationSession(
    val id: Long = 0,
    val date: String,           // 日期
    val durationMinutes: Int    // 时长(分钟)
)
```

### 3.3 情绪系统设计

应用采用四象限情绪模型：

| 情绪 | 英文标识 | 颜色 | 图标 | 含义 |
|-----|---------|------|------|------|
| 平静 | calm | #22C55E (绿色) | 😊 | 内心安宁 |
| 开心 | happy | #F97316 (橙色) | 😄 | 愉悦快乐 |
| 低落 | sad | #3B82F6 (蓝色) | 😢 | 情绪低沉 |
| 焦虑 | anxious | #A855F7 (紫色) | 😰 | 不安紧张 |

---

## 四、核心模块分析

### 4.1 首页 (HomeScreen)

**功能职责**:
- 显示问候语（根据时间段自动切换）
- 今日心情快速记录
- 本周日记统计图表
- 快捷入口（冥想/愿望）
- 最近日记预览

**核心代码片段**:
```kotlin
@Composable
fun GreetingHeader() {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "早上好"
        hour < 18 -> "下午好"
        else -> "晚上好"
    }
    // ...
}
```

**技术创新点**:
- 动态问候语基于系统时间
- 周统计使用柱状图可视化
- Flow 实时数据订阅

### 4.2 情绪日记 (DiaryScreen)

**功能职责**:
- 日期选择器（支持前后3天浏览）
- 日记撰写与保存
- 历史日记列表
- 日记删除

**技术创新点**:
- 日期滑动选择器
- 多情绪标签支持
- 实时数据过滤

### 4.3 星愿清单 (WishScreen)

**功能职责**:
- 愿望列表展示
- 添加新愿望（含进度设置）
- 进度滑块控制
- 状态切换（待完成↔进行中↔已完成）

**愿望状态机**:
```
                    progress = 0
    ┌─────────────────────────────────┐
    ↓                                 │
pending ──→ 点击开始 ──→ active ──→ 进度 100% ──→ done
    ↑                                    │
    └─────── 点击重新开启 ←───────────────┘
```

### 4.4 正念冥想 (MindScreen)

**功能职责**:
- 冥想计时器（3/5/10 分钟可选）
- 呼吸动画引导
- 白噪音/轻音乐播放
- 冥想记录统计

**技术创新点**:
```kotlin
// 呼吸动画
val infiniteTransition = rememberInfiniteTransition(label = "breathe")
val breatheProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(8000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Restart
    )
)

// 呼吸提示文字
val breatheText = when {
    breatheProgress < 0.375f -> "吸气"
    breatheProgress < 0.5f -> "屏息"
    breatheProgress < 0.875f -> "呼气"
    else -> "停息"
}
```

**音频播放**:
- 使用 MediaPlayer 实现流媒体播放
- 支持在线音频 URL
- 播放/暂停状态管理

---

## 五、UI/UX 设计

### 5.1 设计语言

**主题色系**:
```kotlin
val StellaPrimary = Color(0xFFFF7F50)      // 珊瑚橙 - 主色
val StellaAccent = Color(0xFF8B4513)        // 棕色 - 强调
val StellaBg = Color(0xFFF7EEDD)             // 米黄色 - 背景
```

**设计风格**:
- 柔和圆角 (12-16dp)
- 温暖的配色方案
- 简洁的卡片布局
- 大量留白

### 5.2 导航设计

采用 **底部导航栏 (Bottom Navigation)** 布局：

```
┌────┬────┬────┬────┬────┐
│ 首页 │ 日记 │ 愿望 │ 冥想 │ 我的 │
└────┴────┴────┴────┴────┘
```

使用 Jetpack Navigation Compose 实现：

```kotlin
NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") { HomeScreen(viewModel, navController) }
    composable("diary") { DiaryScreen(viewModel) }
    composable("wish") { WishScreen(viewModel) }
    composable("mind") { MindScreen(viewModel) }
    composable("me") { ProfileScreen(viewModel) }
}
```

### 5.3 响应式设计

- 使用 `fillMaxSize()` 和 `fillMaxWidth()` 自适应布局
- `padding()` 和 `spacer()` 控制间距
- `weight()` 实现弹性布局

### 5.4 界面截图

以下是 Stella 应用的四个主要页面设计：

**首页 (HomeScreen)**
- 顶部问候语区域，显示当前时间段
- 本周情绪趋势柱状图
- 快捷入口按钮（日记、愿望、冥想）
- 最新日记预览卡片

**日记 (DiaryScreen)**
- 月历日期选择器
- 日记列表展示（包含心情图标、日期、内容摘要）
- 新建日记入口

**心愿单 (WishScreen)**
- 愿望完成进度环形统计
- 愿望列表（已完成/进行中/未开始状态）
- 添加新愿望入口

**心理 (MindScreen)**
- 心理健康指数仪表盘
- 情绪分布可视化
- 今日建议卡片
- 冥想音频资源入口

![Stella 应用界面设计](./docs/stella_screenshots.png)

> 注：以上为应用界面 Mockup 设计稿，实际界面可能有所差异。

---

## 六、数据流与状态管理

### 6.1 ViewModel 状态流

```kotlin
class StellaViewModel(application: Application) : AndroidViewModel(application) {
    
    // 数据库 Flow
    val allDiaries = repository.allDiaries.stateIn(
        viewModelScope, 
        SharingStarted.WhileSubscribed(5000), 
        emptyList()
    )
    
    // 派生状态
    val diariesForSelectedDate = combine(
        allDiaries, 
        _selectedDate
    ) { diaries, date ->
        diaries.filter { it.date == date }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // 周统计数据
    val weekChartData = allDiaries.map { diaries ->
        // 计算每周各天的日记数量
    }.stateIn(viewModelScope, WhileSubscribed(5000), List(7) { 0 })
}
```

### 6.2 单向数据流

```
用户操作 → ViewModel.method() → Repository → Room DAO
    ↓
UI recompose ← StateFlow 更新 ← emit
```

### 6.3 协程作用域

```kotlin
fun addDiary(content: String, mood: String, date: String) {
    viewModelScope.launch {
        repository.addDiary(DiaryEntry(...))
    }
}
```

---

## 七、项目结构

```
MyApplication/
├── app/
│   ├── src/main/
│   │   ├── java/com/wham/moo/
│   │   │   ├── MainActivity.kt              # 应用入口
│   │   │   ├── StellaApplication.kt         # Application 类
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── DiaryEntryDao.kt     # 日记 DAO
│   │   │   │   │   ├── WishDao.kt            # 愿望 DAO
│   │   │   │   │   ├── MeditationSessionDao.kt
│   │   │   │   │   └── StellaDatabase.kt     # 数据库
│   │   │   │   ├── entity/
│   │   │   │   │   ├── DiaryEntry.kt
│   │   │   │   │   ├── Wish.kt
│   │   │   │   │   └── MeditationSession.kt
│   │   │   │   └── repository/
│   │   │   │       └── StellaRepository.kt   # 数据仓库
│   │   │   └── ui/
│   │   │       ├── home/HomeScreen.kt
│   │   │       ├── diary/DiaryScreen.kt
│   │   │       ├── wish/WishScreen.kt
│   │   │       ├── mind/MindScreen.kt
│   │   │       ├── profile/ProfileScreen.kt
│   │   │       ├── navigation/BottomNav.kt
│   │   │       ├── theme/
│   │   │       │   ├── Color.kt             # 颜色定义
│   │   │       │   ├── Theme.kt             # 主题配置
│   │   │       │   └── Type.kt              # 字体排版
│   │   │       ├── components/               # 公共组件
│   │   │       └── viewmodel/
│   │   │           └── StellaViewModel.kt
│   │   └── res/                              # 资源文件
│   └── build.gradle.kts
├── build.gradle.kts                          # 根构建配置
├── settings.gradle.kts
├── gradle.properties
└── .gitignore
```

---

## 八、构建与部署

### 8.1 构建要求

- **Gradle**: 8.4+
- **Android Gradle Plugin**: 8.2.0+
- **Kotlin**: 1.9.21+

### 8.2 调试构建

```bash
./gradlew assembleDebug
```

### 8.3 发布构建

```bash
./gradlew assembleRelease
```

### 8.4 依赖管理

使用 Gradle Version Catalog (`gradle/libs.versions.toml`) 统一管理依赖版本：

```toml
[versions]
androidx-core = "1.12.0"
compose-bom = "2024.01.00"
room = "2.6.1"
navigation = "2.7.6"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "androidx-core" }
# ...
```

---

## 九、后续优化建议

### 9.1 功能扩展

1. **数据导出/导入**: 支持日记数据备份到云端
2. **情绪趋势分析**: 基于历史数据的情绪波动图表
3. **冥想课程**: 预设冥想引导音频
4. **愿望提醒**: 定期提醒用户跟进愿望进度
5. **夜间模式**: 支持深色主题

### 9.2 性能优化

1. **数据库优化**: 添加索引提升查询性能
2. **图片缓存**: 使用 Coil 进行图片加载
3. **列表优化**: 使用 `LazyColumn` 优化长列表

### 9.3 安全加固

1. **数据加密**: Room 数据库加密
2. **隐私保护**: 敏感数据脱敏处理
3. **权限控制**: 运行时权限申请

---

## 十、参考资料

- [Jetpack Compose 官方文档](https://developer.android.com/compose)
- [Room 数据库指南](https://developer.android.com/training/data-storage/room)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

*文档由 WorkBuddy 自动生成*
