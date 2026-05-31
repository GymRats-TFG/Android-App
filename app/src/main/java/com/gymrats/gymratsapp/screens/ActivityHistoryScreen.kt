package com.gymrats.gymratsapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.ActivityCard
import com.gymrats.gymratsapp.viewModels.UserHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityHistoryScreen(
    viewModel: UserHomeViewModel,
    onBack: () -> Unit
) {
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))
    val sessions = viewModel.getGroupedActivity()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.history),
                        fontFamily = poppinsBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            items(sessions) { session ->
                ActivityCard(
                    gymName = session.gymName,
                    recordedAt = session.date,
                    entryTime = session.entryTime,
                    exitTime = session.exitTime,
                    poppinsBold = poppinsBold,
                    poppinsRegular = poppinsRegular
                )
                Spacer(Modifier.height(12.dp))
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}