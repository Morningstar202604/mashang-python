package com.pyneon.academy.screens

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.map
import com.pyneon.academy.data.StreakViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.em
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pyneon.academy.R
import com.pyneon.academy.ui.effects.NeonCard
import com.pyneon.academy.ui.components.NeonColors
import com.pyneon.academy.ui.components.NeonTextStyles
import com.pyneon.academy.ui.effects.GlitchText
import com.pyneon.academy.ui.components.NeonButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(
    onBack: () -> Unit,
    onOpenReview: () -> Unit = {},
    viewModel: StreakViewModel = viewModel()
) {
    val streak by viewModel.streak.collectAsState()
    var dueCount by remember { mutableStateOf(0) }

    // Load due count
    LaunchedEffect(Unit) {
        dueCount = viewModel.getDueReviewCount()
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
                title = { GlitchText("连击台", style = NeonTextStyles.NeonTitle, color = NeonColors.Primary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回", tint = NeonColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeonColors.Surface,
                    titleContentColor = NeonColors.Primary
                )
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

            // Main streak card
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "火焰",
                        modifier = Modifier.size(64.dp),
                        tint = NeonColors.Accent
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "${streak.currentStreak}",
                        style = NeonTextStyles.NeonTitle.copy(fontSize = 72.sp, fontWeight = FontWeight.Bold),
                        color = NeonColors.Accent
                    )
                    Text(
                        "天连击",
                        style = NeonTextStyles.NeonBody,
                        color = NeonColors.TextSecondary
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem("最长", "${streak.longestStreak} 天", Icons.Default.EmojiEvents)
                        StatItem("累计", "${streak.totalActiveDays} 天", Icons.Default.CalendarToday)
                        StatItem("待复习", "$dueCount 道", Icons.Default.Refresh)
                    }
                    if (dueCount > 0) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))
                        NeonButton(
                            label = "开始复习 $dueCount 张",
                            accent = NeonColors.Primary,
                            onClick = onOpenReview,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            // Badges
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                elevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("已解锁勋章", style = NeonTextStyles.NeonSubtitle, color = NeonColors.TextSecondary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    BadgeGrid(badges = streak.badgesUnlocked.split(",").filter { it.isNotBlank() })
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

            // Locked badges preview
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("未解锁勋章", style = NeonTextStyles.NeonSubtitle, color = NeonColors.TextSecondary)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))
                    BadgeGrid(
                        badges = BADGE_DEFS.filter { it.id !in streak.badgesUnlocked.split(",") }.map { it.id },
                        locked = true
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = "", tint = NeonColors.Primary, modifier = Modifier.size(20.dp))
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = NeonTextStyles.NeonBody.copy(fontWeight = FontWeight.Bold), color = NeonColors.Primary)
        Text(label, style = NeonTextStyles.NeonCaption, color = NeonColors.TextDim)
    }
}

@Composable
fun BadgeGrid(badges: List<String>, locked: Boolean = false) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(badges) { badgeId ->
            BADGE_DEFS.firstOrNull { it.id == badgeId }?.let { def ->
                BadgeItem(def, locked)
            }
        }
    }
}

@Composable
fun BadgeItem(def: BadgeDef, locked: Boolean) {
    val (icon, color) = if (locked) {
        Icons.Default.Lock to NeonColors.TextDim.copy(alpha = 0.4f)
    } else {
        def.icon to def.color
    }
    NeonCard(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(8.dp),
        elevation = if (locked) 0.dp else 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = "", tint = color, modifier = Modifier.size(32.dp))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(def.name, style = NeonTextStyles.NeonCaption, color = color, textAlign = TextAlign.Center, maxLines = 2)
        }
    }
}

data class BadgeDef(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val BADGE_DEFS = listOf(
    BadgeDef("first_day", "初登基地", Icons.Default.LocalFireDepartment, NeonColors.Accent),
    BadgeDef("three_days", "三日不绝", Icons.Default.TrendingUp, NeonColors.Primary),
    BadgeDef("week_warrior", "周末战士", Icons.Default.EmojiEvents, NeonColors.Secondary),
    BadgeDef("fortnight", "半月常驻", Icons.Default.Star, NeonColors.Cyan),
    BadgeDef("month_master", "月度大师", Icons.Default.Diamond, NeonColors.Magenta),
    BadgeDef("centurion", "百日统领", Icons.Default.MilitaryTech, NeonColors.Gold)
)

// Drawable resource for fire icon
// Create app/src/main/res/drawable/ic_fire.xml if needed