package com.example.studyflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studyflow.navigation.BottomNavDestination
import com.example.studyflow.ui.theme.CardWhite
import com.example.studyflow.ui.theme.SkyBlue
import com.example.studyflow.ui.theme.StudyBorder
import com.example.studyflow.ui.theme.SurfaceTint

@Composable
fun StudyFlowBottomBar(
    destinations: List<BottomNavDestination>,
    selectedRoute: String?,
    onDestinationClick: (BottomNavDestination) -> Unit
) {
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 8.dp
    ) {
        destinations.forEach { destination ->
            val selected = selectedRoute == destination.route

            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination) },
                icon = {
                    BottomBarIcon(
                        text = destination.iconText,
                        selected = selected
                    )
                },
                label = {
                    Text(
                        text = destination.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@Composable
private fun BottomBarIcon(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) SkyBlue else SurfaceTint)
            .border(1.dp, if (selected) SkyBlue else StudyBorder, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
