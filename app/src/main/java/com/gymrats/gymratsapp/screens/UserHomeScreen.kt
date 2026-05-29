package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gymrats.gymratsapp.viewModels.UserHomeViewModel

@Composable
fun UserHomeScreen(
    viewModel: UserHomeViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.loadSubscriptions()
    }

    val activeGyms = viewModel.subscriptions.filter {
        it.status == "active"
    }

    val inactiveGyms = viewModel.subscriptions.filter {
        it.status != "active"
    }

    when {

        viewModel.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }

        }

        viewModel.errorMessage != null -> {

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = viewModel.errorMessage!!)
            }

        }

        else -> {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                item {
                    Text(
                        text = "Suscripciones Activas",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(activeGyms) { subscription ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = subscription.gym.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = subscription.gym.address)

                            Text(
                                text = "Hasta: ${subscription.expiration_date}"
                            )
                        }
                    }
                }

                item {

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Suscripciones Caducadas",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(inactiveGyms) { subscription ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = subscription.gym.name,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = subscription.gym.address)

                            Text(
                                text = "Caducó: ${subscription.expiration_date}"
                            )
                        }
                    }
                }
            }
        }
    }
}