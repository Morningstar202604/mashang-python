package com.pyneon.academy.screens

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Code
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.asFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyneon.academy.R
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.components.NeonColors
import com.pyneon.academy.ui.components.NeonTextStyles
import com.pyneon.academy.data.MistakeViewModel
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.components.NeonButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistakeScreen(
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit = {},
    lessonId: String? = null,
    viewModel: MistakeViewModel = viewModel()
) {
    val mistakes by if (lessonId != null) {
        viewModel.getByLesson(lessonId).collectAsState(emptyList())
    } else {
        viewModel.allMistakes.collectAsState(emptyList())
    }
    var weakConcepts by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        weakConcepts = viewModel.getWeakConcepts()
    }

    var filterConcept by remember { mutableStateOf<String?>(null) }
    val displayMistakes = remember(mistakes, filterConcept) {
        mistakes.filter { m ->
            filterConcept == null || m.conceptTags.contains(filterConcept ?: "")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = NeonColors.Surface
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { GlitchText(lessonId?.let { "错题本·$it" } ?: "错题本", style = NeonTextStyles.NeonTitle, color = NeonColors.Primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = NeonColors.Primary)
                    }
                },
                actions = {
                    if (displayMistakes.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAll() }) {
                            Icon(Icons.Default.Delete, contentDescription = "清空", tint = NeonColors.Error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeonColors.Surface,
                    titleContentColor = NeonColors.Primary
                )
            )

            // Filter chips
            if (weakConcepts.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                androidx.compose.material3.ScrollableTabRow(
                    selectedTabIndex = weakConcepts.indexOfFirst { it.first == filterConcept } - 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    indicator = { tabPositions ->
                        androidx.compose.foundation.layout.Box(
                            Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .graphicsLayer { translationX = tabPositions.firstOrNull()?.left?.toPx() ?: 0f }
                                .background(NeonColors.Primary)
                        )
                    },
                    divider = {},
                    edgePadding = 8.dp
                ) {
                    androidx.compose.material3.Tab(
                        text = { Text("全部") },
                        selected = filterConcept == null,
                        onClick = { filterConcept = null },
                        selectedContentColor = NeonColors.Primary,
                        unselectedContentColor = NeonColors.TextSecondary
                    )
                    weakConcepts.forEach { (concept, count) ->
                        androidx.compose.material3.Tab(
                            text = { Text("$concept ($count)") },
                            selected = filterConcept == concept,
                            onClick = { filterConcept = concept },
                            selectedContentColor = NeonColors.Primary,
                            unselectedContentColor = NeonColors.TextSecondary
                        )
                    }
                }
            }

            if (displayMistakes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NeonCard(elevation = 0.dp) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.BugReport, contentDescription = "", tint = NeonColors.TextDim, modifier = Modifier.size(48.dp))
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        if (lessonId != null) "该课暂无错题" else "太棒了，暂无错题！",
                        style = NeonTextStyles.NeonBody,
                        color = NeonColors.TextSecondary
                    )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayMistakes) { mistake ->
                        MistakeItem(mistake, onRetry = { onOpenLesson(mistake.lessonId) }, onDelete = { viewModel.deleteMistake(mistake.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun MistakeItem(
    mistake: com.pyneon.academy.data.MistakeRecord,
    onRetry: () -> Unit,
    onDelete: () -> Unit
) {
    NeonCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val blockLabel = if (mistake.blockIndex >= 0) "l${mistake.blockIndex + 1}" else "练习"
                Text(
                    "$blockLabel · ${mistake.blockType.uppercase()}",
                    style = NeonTextStyles.NeonSubtitle,
                    color = NeonColors.Primary
                )
                Text(
                    android.text.format.DateFormat.format("MM-dd HH:mm", mistake.timestamp).toString(),
                    style = NeonTextStyles.NeonCaption,
                    color = NeonColors.TextDim
                )
            }

            if (mistake.conceptTags.isNotEmpty()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "标签: ${mistake.conceptTags.joinToString(", ")}",
                    style = NeonTextStyles.NeonCaption,
                    color = NeonColors.TextDim
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // User code
            NeonCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = 1.dp,
                backgroundColor = NeonColors.Surface.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row {
                        Icon(Icons.Default.Code, contentDescription = "", tint = NeonColors.Error, modifier = Modifier.padding(end = 8.dp))
                        Text("你的代码", style = NeonTextStyles.NeonCaption, color = NeonColors.Error)
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                    Text(mistake.userCode, style = NeonTextStyles.NeonCode, color = NeonColors.TextPrimary)
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

            // Expected vs Actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    NeonCard(elevation = 1.dp) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("期望输出", style = NeonTextStyles.NeonCaption, color = NeonColors.Success)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                            Text(mistake.expectedOutput, style = NeonTextStyles.NeonCode, color = NeonColors.TextPrimary)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    NeonCard(elevation = 1.dp) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("实际输出", style = NeonTextStyles.NeonCaption, color = NeonColors.Error)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                            Text(mistake.actualOutput, style = NeonTextStyles.NeonCode, color = NeonColors.TextPrimary)
                        }
                    }
                }
            }

            if (mistake.errorMessage.isNotBlank()) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                NeonCard(elevation = 1.dp, backgroundColor = NeonColors.Error.copy(alpha = 0.1f)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row {
                            Icon(Icons.Default.BugReport, contentDescription = "", tint = NeonColors.Error, modifier = Modifier.padding(end = 8.dp))
                            Text("错误信息", style = NeonTextStyles.NeonCaption, color = NeonColors.Error)
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
                        Text(mistake.errorMessage, style = NeonTextStyles.NeonCode, color = NeonColors.Error)
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                NeonButton(label = "删除", accent = NeonColors.TextDim, onClick = onDelete)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                NeonButton(label = "重做此题", accent = NeonColors.Primary, onClick = onRetry)
            }
        }
    }
}