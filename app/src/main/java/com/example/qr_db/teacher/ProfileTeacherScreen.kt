package com.example.qr_db.teacher

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.qr_db.data.ContactPerson
import androidx.compose.ui.unit.sp
import com.example.qr_db.R
import com.example.qr_db.data.TeacherProfileResponse

@Composable
fun ProfileTeacherScreen(
    profile: TeacherProfileResponse,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD0D0D0))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        fun getX(px: Float) = screenWidth * (px / 1080f)
        fun getY(px: Float) = screenHeight * (px / 2388f)
        fun getSp(px: Float) = (px / 3f).sp * (screenWidth.value / 360f)

        val curatedGroup = profile.curatedGroup?.takeIf { it.isNotBlank() }
        val groupLeader = profile.groupLeader
        val departmentHead = profile.departmentHead

        // Собираем список секций которые нужно показать
        val sections = mutableListOf<Pair<String, ContactPerson>>()
        if (groupLeader != null) sections.add("Староста" to groupLeader)
        if (departmentHead != null) sections.add("Зав. отделения" to departmentHead)

        // Кнопка выхода — всегда +1
        val totalSections = sections.size + 1

        Image(
            painter = painterResource(id = R.drawable.wavy_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(modifier = Modifier.fillMaxSize()) {

            // КРЕСТИК
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .offset(x = getX(64f), y = getY(158f))
                    .size(getX(75f))
                    .clip(CircleShape)
                    .clickable { onBack() },
                tint = Color.Black
            )

            // --- ПЕРВАЯ КАРТОЧКА ---
            Box(
                modifier = Modifier
                    .offset(x = getX(155f), y = getY(185f))
                    .size(width = getX(770f), height = getY(646f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.42f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(getY(56f)))
                    Image(
                        painter = painterResource(id = R.drawable.avater),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(getX(253f))
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(getY(40f)))
                    Text(
                        text = profile.teacher.fullName,
                        style = TextStyle(
                            fontSize = getSp(63f),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Курируемая группа — только если есть
                    if (curatedGroup != null) {
                        DividerLine()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(getY(140f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = curatedGroup,
                                    style = TextStyle(
                                        fontSize = getSp(45f),
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = "Курируемая группа",
                                    style = TextStyle(
                                        fontSize = getSp(32f),
                                        color = Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ВТОРАЯ КАРТОЧКА
            val card2Height = getY(1048f)
            val sectionHeight = card2Height / totalSections

            Box(
                modifier = Modifier
                    .offset(x = getX(155f), y = getY(931f))
                    .size(width = getX(770f), height = card2Height)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.42f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Показываем все доступные секции
                    sections.forEach { (title, person) ->
                        PersonSection(
                            title = title,
                            person = person,
                            sectionHeight = sectionHeight,
                            getSp = ::getSp
                        )
                        DividerLine()
                    }

                    // КНОПКА ВЫХОДА — всегда последняя
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(sectionHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Выйти из профиля",
                            color = Color(0xFFB71B1B),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onLogout() }
                                .padding(8.dp),
                            style = TextStyle(
                                fontSize = getSp(50f),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // КНОПКА НАЗАД
            Box(
                modifier = Modifier
                    .offset(x = getX(233f), y = getY(2080f))
                    .size(width = getX(620f), height = getY(160f))
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xFFD9D9D9).copy(alpha = 0.5f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Назад",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = getSp(50f),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun PersonSection(
    title: String,
    person: ContactPerson,
    sectionHeight: Dp,
    getSp: (Float) -> TextUnit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sectionHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontSize = getSp(32f),
                color = Color.Black.copy(alpha = 0.65f)
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = person.fullName,
            style = TextStyle(
                fontSize = getSp(45f) * 1.1f,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        )

        Text(
            text = person.phone ?: "Номер не указан",
            style = TextStyle(
                fontSize = getSp(45f),
                color = Color.Black.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Black.copy(alpha = 0.1f))
    )
}