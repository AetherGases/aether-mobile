package com.aether.application.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.application.R

@Composable
fun RequirementItem(text: String, isMet: Boolean) {
    val iconColor by animateColorAsState(
        targetValue = if (isMet) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant,
        label = "requirementIconColor",
    )
    val textColor by animateColorAsState(
        targetValue = if (isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
        label = "requirementTextColor",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics(mergeDescendants = true) {
            stateDescription = if (isMet) "atendido" else "pendente"
        },
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_checked_circle),
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(text = text, color = textColor, fontSize = 16.sp)
    }
}