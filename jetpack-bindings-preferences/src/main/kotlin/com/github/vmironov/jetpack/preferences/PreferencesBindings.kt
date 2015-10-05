package com.github.vmironov.jetpack.preferences

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlin.properties.ReadWriteProperty

public interface PreferencesAware {
  public companion object {
    public operator fun invoke(factory: () -> SharedPreferences): PreferencesAware = object : PreferencesAware {
      override val preferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        factory()
      }
    }
  }

  public val preferences: SharedPreferences
}

public inline fun <reified T : Any> PreferencesAware.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> SharedPreferences.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> Fragment.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> android.support.v4.app.Fragment.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> RecyclerView.ViewHolder.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> Context.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> View.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> Dialog.preference(default: T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, { default })
}

public inline fun <reified T : Any> PreferencesAware.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> SharedPreferences.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> Fragment.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> android.support.v4.app.Fragment.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> RecyclerView.ViewHolder.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> Context.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> View.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public inline fun <reified T : Any> Dialog.preference(noinline default: () -> T, name: String? = null): ReadWriteProperty<Any, T> {
  return PreferencesVar(T::class.java, this, name, default)
}

public class PreferencesVar<T : Any, V : Any>(
    private val clazz: Class<V>,
    private val source: Any,
    private val name: String?,
    private val default: () -> V
) : ReadWriteProperty<T, V> {
  private val preferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
    @Suppress("USELESS_CAST")
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

  override final operator fun get(thisRef: T, property: PropertyMetadata): V {
    return onGet(preferences, name ?: property.name, default)
  }

  override final operator fun set(thisRef: T, property: PropertyMetadata, value: V) {
    preferences.edit().apply {
      onSet(this, name ?: property.name, value)
      apply()
    }
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
  private fun onSet(preferences: SharedPreferences.Editor, name: String, value: V) {
    when {
      clazz === kotlin.Boolean::class.java, clazz === java.lang.Boolean::class.java -> {
        preferences.putBoolean(name, value as Boolean)
      }

      clazz === kotlin.Float::class.java, clazz === java.lang.Float::class.java -> {
        preferences.putFloat(name, value as Float)
      }

      clazz === kotlin.Int::class.java, clazz === java.lang.Integer::class.java -> {
        preferences.putInt(name, value as Int)
      }

      clazz === kotlin.Long::class.java, clazz === java.lang.Long::class.java -> {
        preferences.putLong(name, value as Long)
      }

      clazz === kotlin.String::class.java, clazz === java.lang.String::class.java -> {
        preferences.putString(name, value as String)
      }

      kotlin.Set::class.java.isAssignableFrom(clazz), java.util.Set::class.java.isAssignableFrom(clazz) -> {
        preferences.putStringSet(name, value as Set<String>)
      }

      kotlin.Enum::class.java.isAssignableFrom(clazz), java.lang.Enum::class.java.isAssignableFrom(clazz) -> {
        preferences.putEnum(name, value as Enum<*>)
      }

      else -> throw UnsupportedOperationException("Unsupported preference (name = $name, type = $clazz)")
    }
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST", "IMPLICIT_CAST_TO_UNIT_OR_ANY")
  private fun onGet(preferences: SharedPreferences, name: String, default: () -> V): V {
    return when {
      clazz === kotlin.Boolean::class.java, clazz === java.lang.Boolean::class.java -> {
        preferences.getBoolean(name, default() as Boolean)
      }

      clazz === kotlin.Float::class.java, clazz === java.lang.Float::class.java -> {
        preferences.getFloat(name, default() as Float)
      }

      clazz === kotlin.Int::class.java, clazz === java.lang.Integer::class.java -> {
        preferences.getInt(name, default() as Int)
      }

      clazz === kotlin.Long::class.java, clazz === java.lang.Long::class.java -> {
        preferences.getLong(name, default() as Long)
      }

      clazz === kotlin.String::class.java, clazz === java.lang.String::class.java -> {
        preferences.getString(name, default() as String)
      }

      kotlin.Set::class.java.isAssignableFrom(clazz), java.util.Set::class.java.isAssignableFrom(clazz) -> {
        preferences.getStringSet(name, default() as Set<String>)
      }

      kotlin.Enum::class.java.isAssignableFrom(clazz), java.lang.Enum::class.java.isAssignableFrom(clazz) -> {
        preferences.getEnum(clazz, name, default())
      }

      else -> throw UnsupportedOperationException("Unsupported preference (name = $name, type = $clazz)")
    } as V
  }

  private fun <E : Any> SharedPreferences.getEnum(clazz: Class<E>, name: String, default: E): E {
    val values = clazz.enumConstants
    val ordinal = getInt(name, -1)

    if (ordinal < 0 || ordinal >= values.size()) {
      return default
    }

    return values[ordinal]
  }

  private fun SharedPreferences.Editor.putEnum(name: String, value: Enum<*>) {
    putInt(name, value.ordinal())
  }
}
