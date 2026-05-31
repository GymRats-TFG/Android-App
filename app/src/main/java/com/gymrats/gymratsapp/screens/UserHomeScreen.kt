package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.gymrats.gymratsapp.viewModels.UserHomeViewModel

@Composable
fun UserHomeScreen(
    viewModel: UserHomeViewModel,
    onGymClick: (String) -> Unit
) {

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val activeGyms = viewModel.subscriptions.filter {
        it.status == "active"
    }

    val inactiveGyms = viewModel.subscriptions.filter {
        it.status != "active"
    }

    val subscribedGymIds = viewModel.subscriptions.map {
        it.gym.id
    }

    val availableGyms = viewModel.gyms.filter { gym ->
        gym.id !in subscribedGymIds
    }

    when {

        viewModel.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        viewModel.errorMessage != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
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

                // SUSCRIPCIONES ACTIVAS

                item {

                    Text(
                        text = "Suscripciones Activas",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (activeGyms.isEmpty()) {

                    item {
                        Text("No tienes suscripciones activas")
                    }

                } else {

                    items(activeGyms) { subscription ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    onGymClick(subscription.gym.id)
                                }
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

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Hasta: ${subscription.expiration_date}"
                                )
                            }
                        }
                    }
                }

                // SUSCRIPCIONES CADUCADAS

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

                if (inactiveGyms.isEmpty()) {

                    item {
                        Text("No tienes suscripciones caducadas")
                    }

                } else {

                    items(inactiveGyms) { subscription ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    onGymClick(subscription.gym.id)
                              }
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

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Caducó: ${subscription.expiration_date}"
                                )
                            }
                        }
                    }
                }

                // GIMNASIOS DISPONIBLES

                item {

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Gimnasios Disponibles",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (availableGyms.isEmpty()) {

                    item {
                        Text("No hay gimnasios disponibles")
                    }

                } else {

                    items(availableGyms) { gym ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clickable {
                                    onGymClick(gym.id)
                                }
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = gym.name,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(text = gym.address)

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (gym.is_open)
                                        "Abierto"
                                    else
                                        "Cerrado"
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}