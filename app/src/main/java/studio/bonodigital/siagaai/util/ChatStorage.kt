package studio.bonodigital.siagaai.util

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import studio.bonodigital.siagaai.data.model.ChatMessage

class ChatStorage(context: Context) {

    private val prefs = context.getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(messages: List<ChatMessage>) {
        prefs.edit {
            putString("history", gson.toJson(messages))
        }
    }

    fun load(): MutableList<ChatMessage> {
        val json = prefs.getString("history", null) ?: return mutableListOf()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return gson.fromJson(json, type)
    }

    fun clear() {
        prefs.edit { clear() }
    }

}