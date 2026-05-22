package com.example.studyflow.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.Lavender
import com.example.studyflow.ui.theme.MutedInk
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.SoftLavender
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint
import com.example.studyflow.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyFlowTopBar(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            if (actionText != null) {
                TextButton(onClick = onActionClick) {
                    Text(actionText, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceTint,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue),
        border = BorderStroke(1.dp, StudyBorder)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StudyCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = SkyBlue.copy(alpha = 0.08f),
                spotColor = SkyBlue.copy(alpha = 0.08f)
            )
            .border(1.dp, StudyBorder.copy(alpha = 0.55f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        color = CardWhite
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = SkyBlue
) {
    StudyCard(modifier = modifier) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = accentColor, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.bodySmall, color = MutedInk)
    }
}

@Composable
fun DeadlineCard(
    title: String,
    dueText: String,
    priority: String,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dueText, style = MaterialTheme.typography.bodySmall, color = MutedInk)
            }
            Pill(text = priority, background = WarningOrange.copy(alpha = 0.14f), color = WarningOrange)
        }
    }
}

@Composable
fun ScheduleCard(
    time: String,
    title: String,
    location: String,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Pill(text = time, background = SoftLavender, color = SkyBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(location, style = MaterialTheme.typography.bodySmall, color = MutedInk)
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${(progress * 100).toInt()}% hoàn thành", style = MaterialTheme.typography.bodySmall, color = MutedInk)
            }
            ProgressRing(progress = progress, size = 58)
        }
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MutedInk, textAlign = TextAlign.Center)
    }
}

@Composable
fun SectionTitle(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun StudySearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Text("Q", color = SkyBlue, fontWeight = FontWeight.Bold) },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SkyBlue,
            unfocusedBorderColor = StudyBorder,
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite
        )
    )
}

@Composable
fun Avatar(
    initials: String,
    modifier: Modifier = Modifier,
    background: Color = Lavender
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Int = 64,
    color: Color = SkyBlue
) {
    Box(modifier = modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val stroke = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            val diameter = this.size.minDimension - stroke.width
            val topLeft = Offset(stroke.width / 2, stroke.width / 2)
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = StudyBorder.copy(alpha = 0.65f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }
        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
fun ToggleSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MutedInk)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = CardWhite,
                    checkedTrackColor = SkyBlue,
                    uncheckedBorderColor = StudyBorder
                )
            )
        }
    }
}

@Composable
fun MessageBubble(
    sender: String,
    message: String,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .background(
                    color = if (isMine) SkyBlue else CardWhite,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = if (isMine) 0.dp else 1.dp,
                    color = StudyBorder,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(sender, color = if (isMine) Color.White else SkyBlue, fontWeight = FontWeight.Bold)
            Text(message, color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun FileItem(
    name: String,
    meta: String,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(SoftLavender, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("F", color = SkyBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(meta, style = MaterialTheme.typography.bodySmall, color = MutedInk)
            }
        }
    }
}

@Composable
fun GroupCard(
    name: String,
    members: String,
    latestActivity: String,
    modifier: Modifier = Modifier
) {
    StudyCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(initials = name.take(1), background = SkyBlue)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(members, style = MaterialTheme.typography.bodySmall, color = MutedInk)
                Text(latestActivity, style = MaterialTheme.typography.bodySmall, color = SkyBlue)
            }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    background: Color,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(text, color = color, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
    }
}
