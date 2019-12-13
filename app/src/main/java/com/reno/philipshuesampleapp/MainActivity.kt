package com.reno.philipshuesampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.reno.philipshue.BulbManager
import com.reno.philipshue.model.Bulb

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = BulbManager()
        manager.getUserInfo()
        val disposable = manager.getBulbService()
            .subscribe {
                Bulb()
            }
    }
}
