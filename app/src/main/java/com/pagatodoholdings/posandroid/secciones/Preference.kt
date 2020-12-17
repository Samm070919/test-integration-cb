package com.pagatodoholdings.posandroid.secciones

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.preference.PreferenceManager

class Preference : Application {

    var pref : SharedPreferences
    var editor : SharedPreferences.Editor

    constructor(context: Context){
        pref = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        editor = pref?.edit() as SharedPreferences.Editor
    }

    fun setProfileImage(key : String, value : String){
        editor.putString(key, value.toString())
        editor.commit()
    }

    fun getProfileImage(key : String) : String?{
        var resource = pref.getString(key, null)
        return resource
    }

}