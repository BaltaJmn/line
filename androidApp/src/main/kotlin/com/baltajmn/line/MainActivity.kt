package com.baltajmn.line

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import com.baltajmn.line.data.AndroidContext
import com.baltajmn.line.data.FilePicker
import com.baltajmn.line.data.Lock
import com.baltajmn.line.data.PhotoPicker
import com.baltajmn.line.data.Reminder
import com.baltajmn.line.data.Route

// FragmentActivity and not ComponentActivity: BiometricPrompt needs a fragment host.
class MainActivity : FragmentActivity() {

    private val askNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    // The Activity only carries the answer across: what is waiting for it lives in FilePicker, which
    // outlives this instance when the system recreates it behind the picker.
    private val createBackup =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/zip"), FilePicker::onPicked)

    private val openBackup =
        registerForActivityResult(ActivityResultContracts.OpenDocument(), FilePicker::onPicked)

    private val pickPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia(), PhotoPicker::onPicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidContext.init(this)
        // The permission is asked the moment the reminder is switched on and never before.
        Reminder.onNeedsPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                askNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        Lock.host = WeakReference(this)
        FilePicker.createDocument = { name -> createBackup.launch(name) }
        FilePicker.openDocument = { openBackup.launch(arrayOf("application/zip", "application/json", "*/*")) }
        // The photo picker asks for no permission: the user hands over one image and nothing else.
        PhotoPicker.pickImage = {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        Route.pending = intent?.getStringExtra("screen")
        setContent { App() }
    }

    // launchMode is singleTask: a widget tapped while the app is already up arrives here.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Route.pending = intent.getStringExtra("screen")
    }

    // The launchers belong to this instance's registry: leaving them in a process wide object would
    // hold the dead Activity and then throw when something tried to launch them.
    override fun onDestroy() {
        FilePicker.createDocument = null
        FilePicker.openDocument = null
        PhotoPicker.pickImage = null
        Reminder.onNeedsPermission = null
        Lock.host = null
        super.onDestroy()
    }
}
