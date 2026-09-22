package com.baltajmn.line.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import com.baltajmn.line.data.LineRepository
import com.baltajmn.line.data.PhotoPicker
import com.baltajmn.line.data.Photos
import com.baltajmn.line.i18n.S
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

/** Asks the system for one image and hangs it on [date]. */
@Composable
fun PhotoButton(date: LocalDate, today: LocalDate, modifier: Modifier = Modifier) {
    if (!PhotoPicker.available) return
    GlyphButton(
        glyph = Glyph.PHOTO,
        label = S.a11yPhoto,
        modifier = modifier,
        onClick = {
            // ponytail: without Pro the fourth photo opens the ProDialog of #23; for now it stays put.
            if (LineRepository.canAddPhoto(date)) {
                PhotoPicker.pick { bytes -> bytes?.let { LineRepository.setPhoto(date, it, today) } }
            }
        },
    )
}
