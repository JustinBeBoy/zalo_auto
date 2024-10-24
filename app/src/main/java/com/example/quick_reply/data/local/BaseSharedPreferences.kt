package com.example.quick_reply.data.local

import android.content.SharedPreferences

abstract class BaseSharedPreferences {

    abstract val sharedPreferences: SharedPreferences

    open fun contain(key: String) = sharedPreferences.contains(key)

    open fun remove(vararg keys: String) {
        val editor = editor()
        for (key in keys) {
            editor.remove(key)
        }
        editor.apply()
    }

    private fun editor(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    open operator fun set(key: String, value: Boolean) {
        editor().putBoolean(key, value).apply()
    }

    open operator fun set(key: String, value: Int) {
        editor().putInt(key, value).apply()
    }

    open operator fun set(key: String, value: Long) {
        editor().putLong(key, value).apply()
    }

    open operator fun set(key: String, value: Float) {
        editor().putFloat(key, value).apply()
    }

    open operator fun set(key: String, value: String) {
        editor().putString(key, value).apply()
    }

    @JvmOverloads
    open fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    @JvmOverloads
    open fun getInt(key: String, defValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    @JvmOverloads
    open fun getLong(key: String, defValue: Long = 0): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    @JvmOverloads
    open fun getFloat(key: String, defValue: Float = 0F): Float {
        return sharedPreferences.getFloat(key, defValue)
    }

    @JvmOverloads
    open fun getString(key: String, defValue: String = ""): String {
        return sharedPreferences.getString(key, defValue) ?: ""
    }

    fun beenDone(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun markDone(key: String) {
        this[key] = true
    }
}