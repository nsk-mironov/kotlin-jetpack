package com.github.vmironov.jetpack.preferences

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlin.properties.ReadWriteProperty

public interface PreferencesAware {
  public val preferences: SharedPreferences
}

public fun Any.bindBooleanPreference(name: String? = null, default: Boolean = false): ReadWriteProperty<Any, Boolean> = BooleanPreferenceVar(this, name, default)
public fun Any.bindFloatPreference(name: String? = null, default: Float = 0.0f): ReadWriteProperty<Any, Float> = FloatPreferenceVar(this, name, default)
public fun Any.bindIntPreference(name: String? = null, default: Int = 0): ReadWriteProperty<Any, Int> = IntPreferenceVar(this, name, default)
public fun Any.bindLongPreference(name: String? = null, default: Long = 0L): ReadWriteProperty<Any, Long> = LongPreferenceVar(this, name, default)
public fun Any.bindStringPreference(name: String? = null, default: String = ""): ReadWriteProperty<Any, String> = StringPreferenceVar(this, name, default)
public fun Any.bindStringSetPreference(name: String? = null, default: Set<String> = emptySet()): ReadWriteProperty<Any, Set<String>> = StringSetPreferenceVar(this, name, default)

private class BooleanPreferenceVar<T>(source: Any, name: String?, default: Boolean) : PreferencesVar<T, Boolean>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: Boolean) {
    preferences.putBoolean(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: Boolean): Boolean {
    return preferences.getBoolean(name, default)
  }
}

private class FloatPreferenceVar<T>(source: Any, name: String?, default: Float) : PreferencesVar<T, Float>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: Float) {
    preferences.putFloat(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: Float): Float {
    return preferences.getFloat(name, default)
  }
}

private class IntPreferenceVar<T>(source: Any, name: String?, default: Int) : PreferencesVar<T, Int>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: Int) {
    preferences.putInt(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: Int): Int {
    return preferences.getInt(name, default)
  }
}

private class LongPreferenceVar<T>(source: Any, name: String?, default: Long) : PreferencesVar<T, Long>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: Long) {
    preferences.putLong(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: Long): Long {
    return preferences.getLong(name, default)
  }
}

private class StringPreferenceVar<T>(source: Any, name: String?, default: String) : PreferencesVar<T, String>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: String) {
    preferences.putString(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: String): String {
    return preferences.getString(name, default)
  }
}

private class StringSetPreferenceVar<T>(source: Any, name: String?, default: Set<String>) : PreferencesVar<T, Set<String>>(source, name, default) {
  override fun onSet(preferences: SharedPreferences.Editor, name: String, value: Set<String>) {
    preferences.putStringSet(name, value)
  }

  override fun onGet(preferences: SharedPreferences, name: String, default: Set<String>): Set<String> {
    return preferences.getStringSet(name, default)
  }
}

private abstract class PreferencesVar<T, V>(
    private val source: Any,
    private val name: String?,
    private val default: V
) : ReadWriteProperty<T, V> {
  private val preferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
    when (source) {
      is PreferencesAware -> source.preferences
      is SharedPreferences -> source as SharedPreferences
      is Fragment -> PreferenceManager.getDefaultSharedPreferences(source.activity)
      is android.support.v4.app.Fragment -> PreferenceManager.getDefaultSharedPreferences(source.activity)
      is Context -> PreferenceManager.getDefaultSharedPreferences(source)
      is View -> PreferenceManager.getDefaultSharedPreferences(source.context)
      is RecyclerView.ViewHolder -> PreferenceManager.getDefaultSharedPreferences(source.itemView.context)
      else -> throw IllegalArgumentException("Unable to find preferences on type ${source.javaClass.simpleName}")
    }
  }

  override final fun get(thisRef: T, property: PropertyMetadata): V {
    return onGet(preferences, name ?: property.name, default)
  }

  override final fun set(thisRef: T, property: PropertyMetadata, value: V) {
    preferences.edit().apply {
      onSet(this, name ?: property.name, value)
      apply()
    }
  }

  public abstract fun onSet(preferences: SharedPreferences.Editor, name: String, value: V)
  public abstract fun onGet(preferences: SharedPreferences, name: String, default: V): V
}
