package com.github.vmironov.jetpack.preferences

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public inline fun <reified V : Any> Any.bindPreference(default: V, key: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(IdentityAdapter(V::class.java), this, key, { default })
}

public inline fun <reified V : Any> Any.bindPreference(noinline default: () -> V, key: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(IdentityAdapter(V::class.java), this, key, default)
}

public inline fun <reified V : Any, reified P : Any> Any.bindPreference(default: V, adapter: Adapter<V, P>, key: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(adapter, this, key, { default })
}

public inline fun <reified V : Any, reified P : Any> Any.bindPreference(noinline default: () -> V, adapter: Adapter<V, P>, key: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(adapter, this, key, default)
}

public inline fun <reified E : Enum<E>> Any.bindEnumPreference(default: E, key: String? = null): ReadWriteProperty<Any, E> {
  return PreferencesVar(EnumAdapter(E::class.java), this, key, { default })
}

public inline fun <reified E : Enum<E>> Any.bindEnumPreference(noinline default: () -> E, key: String? = null): ReadWriteProperty<Any, E> {
  return PreferencesVar(EnumAdapter(E::class.java), this, key, default)
}

public inline fun <reified V : Any> Any.bindOptionalPreference(key: String? = null): ReadWriteProperty<Any, V?> {
  return OptionalPreferencesVar(IdentityAdapter(V::class.java), this, key)
}

public inline fun <reified V : Any, reified P : Any> Any.bindOptionalPreference(adapter: Adapter<V, P>, key: String? = null): ReadWriteProperty<Any, V?> {
  return OptionalPreferencesVar(adapter, this, key)
}

public inline fun <reified E : Enum<E>> Any.bindOptionalEnumPreference(key: String? = null): ReadWriteProperty<Any, E?> {
  return OptionalPreferencesVar(EnumAdapter(E::class.java), this, key)
}

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

public interface Adapter<V, P> {
  public fun type(): Class<P>
  public fun fromPreference(preference: P): V
  public fun toPreference(value: V): P
}

public class IdentityAdapter<T>(val clazz: Class<T>) : Adapter<T, T> {
  override fun type(): Class<T> = clazz
  override fun fromPreference(preference: T): T = preference
  override fun toPreference(value: T): T = value
}

public class EnumAdapter<E : Enum<E>>(val clazz: Class<E>) : Adapter<E, String> {
  override fun type(): Class<String> = String::class.java
  override fun fromPreference(preference: String): E = java.lang.Enum.valueOf(clazz, preference)
  override fun toPreference(value: E): String = value.name
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
public class PreferencesVar<T : Any, V : Any, P : Any>(
    private val adapter: Adapter<V, P>,
    private val source: Any,
    private val key: String?,
    private val default: () -> V
) : ReadWriteProperty<T, V> {
  private val preference = onGetPropertyFromClass(adapter.type())

  private val preferences by lazy(LazyThreadSafetyMode.NONE) {
    onGetPreferencesFromSource(source)
  }

  override final operator fun getValue(thisRef: T, property: KProperty<*>): V {
    val name = key ?: property.name

    if (!preferences.contains(name)) {
      setValue(thisRef, property, default())
    }

    return adapter.fromPreference(preference[preferences, name] as P)
  }

  override final operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    preferences.edit().apply {
      preference.set(this, key ?: property.name, adapter.toPreference(value))
      apply()
    }
  }
}

@Suppress("UNCHECKED_CAST")
public class OptionalPreferencesVar<T : Any, V : Any, P : Any>(
    private val adapter: Adapter<V, P>,
    private val source: Any,
    private val key: String?
) : ReadWriteProperty<T, V?> {
  private val preference = onGetPropertyFromClass(adapter.type())

  private val preferences by lazy(LazyThreadSafetyMode.NONE) {
    onGetPreferencesFromSource(source)
  }

  override operator fun getValue(thisRef: T, property: KProperty<*>): V? {
    val name = key ?: property.name

    return if (preferences.contains(name)) {
      adapter.fromPreference(preference[preferences, name] as P)
    } else {
      null
    }
  }

  override operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
    preferences.edit().apply {
      val name = key ?: property.name

      if (value != null) {
        preference.set(this, name, adapter.toPreference(value))
      } else {
        remove(name)
      }

      apply()
    }
  }
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
private fun onGetPropertyFromClass(clazz: Class<*>): Preference<Any> {
  return when (clazz) {
    kotlin.Boolean::class.java -> BooleanPreference
    kotlin.Float::class.java -> FloatPreference
    kotlin.Int::class.java -> IntPreference
    kotlin.Long::class.java -> LongPreference
    kotlin.String::class.java -> StringPreference

    java.lang.Boolean::class.java -> BooleanPreference
    java.lang.Float::class.java -> FloatPreference
    java.lang.Integer::class.java -> IntPreference
    java.lang.Long::class.java -> LongPreference
    java.lang.String::class.java -> StringPreference

    else -> throw UnsupportedOperationException("Unsupported preference type \"${clazz.canonicalName}\"")
  } as Preference<Any>
}

private fun onGetPreferencesFromSource(source: Any): SharedPreferences {
  return when {
    source is SharedPreferences -> source
    source is PreferencesAware -> source.preferences
    source is Fragment -> PreferenceManager.getDefaultSharedPreferences(source.activity)
    source is Context -> PreferenceManager.getDefaultSharedPreferences(source)

    SupportHelper.isFragment(source) -> SupportFragmentHelper.getPreferences(source)
    SupportHelper.isHolder(source) -> SupportRecyclerHelper.getPreferences(source)

    source is View -> PreferenceManager.getDefaultSharedPreferences(source.context)
    source is Dialog -> PreferenceManager.getDefaultSharedPreferences(source.context)

    else -> throw IllegalArgumentException("Unable to find \"SharedPreferences\" instance on type \"${source.javaClass.simpleName}\"")
  }
}

private interface Preference<T> {
  public operator fun set(editor: SharedPreferences.Editor, name: String, value: T): Unit
  public operator fun get(preferences: SharedPreferences, name: String): T
}

private object BooleanPreference : Preference<Boolean> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Boolean): Unit {
    editor.putBoolean(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Boolean {
    return preferences.getBoolean(name, false)
  }
}

private object FloatPreference : Preference<Float> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Float): Unit {
    editor.putFloat(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Float {
    return preferences.getFloat(name, 0.0f)
  }
}

private object IntPreference : Preference<Int> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Int): Unit {
    editor.putInt(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Int {
    return preferences.getInt(name, 0)
  }
}

private object LongPreference : Preference<Long> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Long): Unit {
    editor.putLong(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Long {
    return preferences.getLong(name, 0L)
  }
}

private object StringPreference : Preference<String> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: String): Unit {
    editor.putString(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): String {
    return preferences.getString(name, "")
  }
}
