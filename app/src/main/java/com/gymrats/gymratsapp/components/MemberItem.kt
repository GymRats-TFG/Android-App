package com.gymrats.gymratsapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.data.MemberInfoResponse

@Composable
fun MemberItem(
    member: MemberInfoResponse,
    showManagementOptions: Boolean = false,
    onEditExpiration: (MemberInfoResponse) -> Unit = {},
    onDeleteMember: (MemberInfoResponse) -> Unit = {}
) {
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    val cleanStartDate = member.start_date.split("T").firstOrNull()?.let { yyyyMmDd ->
        yyyyMmDd.split("-").reversed().joinToString("-")
    } ?: member.start_date

    val cleanEndDate = member.expiration_date.split("T").firstOrNull()?.let { yyyyMmDd ->
        yyyyMmDd.split("-").reversed().joinToString("-")
    } ?: member.expiration_date

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = member.avatar_url ?: R.drawable.gymrats_logo,
                    contentDescription = null,
                    modifier = Modifier.size(45.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(member.name ?: member.username, fontFamily = poppinsBold, fontSize = 14.sp)
                    Text("@${member.username}", fontFamily = poppinsRegular, fontSize = 12.sp, color = Color.Gray)
                }
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        member.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontFamily = poppinsBold,
                        color = if (member.status == "active") Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }

            if (showManagementOptions) {
                HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val StartDateText = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(stringResource(R.string.member_item_start_date)+": ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                append(cleanStartDate)
                            }
                        }
                        Text(text = StartDateText, fontSize = 12.sp, fontFamily = poppinsRegular)

                        val EndDateText = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(stringResource(R.string.member_item_end_date)+": ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                                append(cleanEndDate)
                            }
                        }
                        Text(text = EndDateText, fontSize = 12.sp, fontFamily = poppinsRegular)
                    }

                    Row {
                        IconButton(onClick = { onEditExpiration(member) }) {
                            Icon(Icons.Rounded.EditCalendar, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp))
                        }
                        IconButton(onClick = { onDeleteMember(member) }) {
                            Icon(Icons.Rounded.Delete, null, tint = Color(0xFFC62828), modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}