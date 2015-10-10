package com.github.vmironov.jetpack.preferences

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import kotlin.properties.ReadWriteProperty

public inline fun <reified V : Any> Any.bindPreference(default: V, name: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(IdentityConverter(V::class.java), this, name, { default })
}

public inline fun <reified V : Any> Any.bindPreference(noinline default: () -> V, name: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(IdentityConverter(V::class.java), this, name, default)
}

public inline fun <reified V : Any, reified P : Any> Any.bindPreference(default: V, converter: Converter<V, P>, name: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(converter, this, name, { default })
}

public inline fun <reified V : Any, reified P : Any> Any.bindPreference(noinline default: () -> V, converter: Converter<V, P>, name: String? = null): ReadWriteProperty<Any, V> {
  return PreferencesVar(converter, this, name, default)
}

public inline fun <reified E : Enum<E>> Any.bindEnumPreference(default: E, name: String? = null): ReadWriteProperty<Any, E> {
  return PreferencesVar(EnumConverter(E::class.java), this, name, { default })
}

public inline fun <reified E : Enum<E>> Any.bindEnumPreference(noinline default: () -> E, name: String? = null): ReadWriteProperty<Any, E> {
  return PreferencesVar(EnumConverter(E::class.java), this, name, default)
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

public interface Converter<V, P> {
  public fun type(): Class<P>
  public fun fromPreference(preference: P): V
  public fun toPreference(value: V): P
}

public class EnumConverter<E : Enum<E>>(val clazz: Class<E>) : Converter<E, String> {
  override fun type(): Class<String> = String::class.java
  override fun fromPreference(preference: String): E = java.lang.Enum.valueOf(clazz, preference)
  override fun toPreference(value: E): String = value.name()
}

public class IdentityConverter<T>(val clazz: Class<T>) : Converter<T, T> {
  override fun type(): Class<T> = clazz
  override fun fromPreference(preference: T): T = preference
  override fun toPreference(value: T): T = value
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
public open class PreferencesVar<T : Any, V : Any, P : Any>(
    private val converter: Converter<V, P>,
    private val source: Any,
    private val name: String?,
    private val default: () -> V
) : ReadWriteProperty<T, V> {
  private val property = onGetPropertyFromClass(converter.type())
  private val preferences by lazy(LazyThreadSafetyMode.NONE) {
    onGetPreferencesFromSource(source)
  }

  override final operator fun get(thisRef: T, property: PropertyMetadata): V {
    if (!preferences.contains(name ?: property.name)) {
      set(thisRef, property, default())
    }

    return onGet(preferences, name ?: property.name)
  }

  override final operator fun set(thisRef: T, property: PropertyMetadata, value: V) {
    preferences.edit().apply {
      onSet(this, name ?: property.name, value)
      apply()
    }
  }

  private fun onSet(editor: SharedPreferences.Editor, name: String, value: V) {
    property.set(editor, name, converter.toPreference(value))
  }

  private fun onGet(preferences: SharedPreferences, name: String): V {
    return converter.fromPreference(property.get(preferences, name) as P)
  }

  protected open fun onGetPropertyFromClass(clazz: Class<*>): PreferenceProperty<Any> {
    return when (clazz) {
      kotlin.Boolean::class.java -> BooleanProperty
      kotlin.Float::class.java -> FloatProperty
      kotlin.Int::class.java -> IntProperty
      kotlin.Long::class.java -> LongProperty
      kotlin.String::class.java -> StringProperty
      kotlin.Set::class.java -> SetProperty

      java.lang.Boolean::class.java -> BooleanProperty
      java.lang.Float::class.java -> FloatProperty
      java.lang.Integer::class.java -> IntProperty
      java.lang.Long::class.java -> LongProperty
      java.lang.String::class.java -> StringProperty
      java.util.Set::class.java -> SetProperty

      else -> throw UnsupportedOperationException("Unsupported preference type \"${clazz.canonicalName}\"")
    } as PreferenceProperty<Any>
  }

  protected open fun onGetPreferencesFromSource(source: Any): SharedPreferences {
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
}

private interface PreferenceProperty<T> {
  public operator fun set(editor: SharedPreferences.Editor, name: String, value: T): Unit
  public operator fun get(preferences: SharedPreferences, name: String): T
}

private object BooleanProperty : PreferenceProperty<Boolean> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Boolean): Unit {
    editor.putBoolean(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Boolean {
    return preferences.getBoolean(name, false)
  }
}

private object FloatProperty : PreferenceProperty<Float> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Float): Unit {
    editor.putFloat(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Float {
    return preferences.getFloat(name, 0.0f)
  }
}

private object IntProperty : PreferenceProperty<Int> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Int): Unit {
    editor.putInt(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Int {
    return preferences.getInt(name, 0)
  }
}

private object LongProperty : PreferenceProperty<Long> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Long): Unit {
    editor.putLong(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): Long {
    return preferences.getLong(name, 0L)
  }
}

private object StringProperty : PreferenceProperty<String> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: String): Unit {
    editor.putString(name, value)
  }

  override fun get(preferences: SharedPreferences, name: String): String {
    return preferences.getString(name, "")
  }
}

@Suppress("UNCHECKED_CAST")
private object SetProperty : PreferenceProperty<Set<*>> {
  override fun set(editor: SharedPreferences.Editor, name: String, value: Set<*>): Unit {
    editor.putStringSet(name, value as Set<String>)
  }

  override fun get(preferences: SharedPreferences, name: String): Set<*> {
    return preferences.getStringSet(name, emptySet())
  }
}
