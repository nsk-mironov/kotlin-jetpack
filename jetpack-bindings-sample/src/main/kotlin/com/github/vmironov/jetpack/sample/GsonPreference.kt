package com.github.vmironov.jetpack.sample

import com.github.vmironov.jetpack.preferences.Adapter
import com.github.vmironov.jetpack.preferences.bindPreference
import com.google.gson.Gson
import kotlin.properties.ReadWriteProperty

public class GsonPreferenceAdapter<T>(val clazz: Class<T>, val gson: Gson = GsonPreferenceAdapter.GSON) : Adapter<T, String> {
  public companion object {
    public val GSON = Gson()
  }

  override fun type(): Class<String> {
    return String::class.java
  }

  override fun fromPreference(preference: String): T {
    return gson.fromJson(preference, clazz)
  }

  override fun toPreference(value: T): String {
    return gson.toJson(value)
  }
}

public inline fun <reified E : Any> Any.bindGsonPreference(default: E, key: String? = null): ReadWriteProperty<Any, E> {
  return bindPreference(default, GsonPreferenceAdapter(E::class.java), key)
}

public inline fun <reified E : Any> Any.bindGsonPreference(noinline default: () -> E, key: String? = null): ReadWriteProperty<Any, E> {
  return bindPreference(default, GsonPreferenceAdapter(E::class.java), key)
}

