package de.andicodes.vergissnix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.andicodes.vergissnix.R
import de.andicodes.vergissnix.Notifications

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolBar))
        Notifications.createNotificationChannel(applicationContext)
    }
}