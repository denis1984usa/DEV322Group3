package com.hfad.movemore

import android.app.Application
import com.hfad.movemore.db.MainDB

class MainApp : Application() {
    val database by lazy { MainDB.getDatabase(this) }
}