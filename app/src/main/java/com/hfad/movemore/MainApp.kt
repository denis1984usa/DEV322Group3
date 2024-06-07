package com.hfad.movemore.db

import android.app.Application
import com.hfad.movemore.db.MainDB

class MainApp : Application() {
    val db by lazy { MainDB.getDatabase(this) }


}

