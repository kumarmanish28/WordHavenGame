package com.manish.wordhaven.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.wordhaven.presentation.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameToolbar(
    title: String,
    onNavigationClick: () -> Unit,
    navigationIcon: ImageVector,
    onActionIconClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null,
    isAction: Boolean? = false,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = "Navigation",
                    tint = Color.White
                )
            }
        },
        actions = {
            isAction?.let {
                if (isAction) {
                    IconButton(onClick = onActionIconClick!!) {
                        Icon(
                            imageVector = actionIcon!!,
                            contentDescription = "action",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Primary,
            scrolledContainerColor = Primary,
            navigationIconContentColor = Primary,
            titleContentColor = Primary,
            actionIconContentColor = Primary
        ),
        modifier = modifier
    )
}
