package com.baltajmn.line.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.PhotoPicker
import com.baltajmn.line.data.Photos
import com.baltajmn.line.i18n.S
import com.baltajmn.line.ui.theme.Styles
import kotlinx.datetime.LocalDate

/**
 * The photo of a day: the width of the column, 4:3 cropped at the centre (pantallas 1.3). A photo
 * whose file is gone draws nothing rather than a hole.
 *
 * ponytail: the first draw decodes on the drawing thread. A 1024 px JPEG takes a few ms and the
 * cache answers every later frame; if a screen ever shows many at once, move it to a producer.
 */
@Composable
fun DayPhoto(name: String, modifier: Modifier = Modifier, onOpen: (() -> Unit)? = null) {
    val image = Photos.get(name) ?: return
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(MaterialTheme.shapes.medium)
            .then(if (onOpen == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onOpen)),
    )
}

/**
 * Asks the system for one image and hangs it on [date]. Named, on the paper colour, so it reads as
 * part of the page; past the free photos the glyph is a padlock, and the tap sells Pro.
 */
@Composable
fun PhotoButton(date: LocalDate, today: LocalDate, modifier: Modifier = Modifier) {
    if (!PhotoPicker.available) return
    val allowed = LineRepository.canAddPhoto(date)
    Row(
        modifier.minimumInteractiveComponentSize()
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background)
            .clickable(role = Role.Button) {
                if (allowed) {
                    PhotoPicker.pick { bytes -> bytes?.let { LineRepository.setPhoto(date, it, today) } }
                } else {
                    Paywall.open = true
                }
            }
            .padding(start = 10.dp, end = 14.dp, top = 7.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlyphIcon(if (allowed) Glyph.PHOTO else Glyph.LOCK, size = 18.dp)
        Spacer(Modifier.width(6.dp))
        Text(S.a11yPhoto, style = Styles.secondary.copy(color = MaterialTheme.colorScheme.onBackground))
    }
}
