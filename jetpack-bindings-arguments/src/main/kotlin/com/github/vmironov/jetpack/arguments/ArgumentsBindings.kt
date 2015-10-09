package com.github.vmironov.jetpack.arguments

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import java.io.Serializable
import kotlin.properties.ReadWriteProperty

public interface ArgumentsAware {
  public var arguments: Bundle?
}

public inline fun <reified T : Any> Bundle.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Intent.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> ArgumentsAware.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Activity.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Fragment.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> android.support.v4.app.Fragment.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> RecyclerView.ViewHolder.bindArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T> = ArgumentsVar(T::class.java, this, name, default)

public inline fun <reified T : Any> Bundle.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Intent.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> ArgumentsAware.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Activity.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> Fragment.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> android.support.v4.app.Fragment.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)
public inline fun <reified T : Any> RecyclerView.ViewHolder.bindOptionalArgument(name: String? = null, default: T? = null): ReadWriteProperty<Any, T?> = OptionalArgumentsVar(T::class.java, this, name, default)

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
public class ArgumentsVar<T, V>(
    private val clazz: Class<V>,
    private val source: Any,
    private val name: String?,
    private val default: V?
) : ReadWriteProperty<T, V> {
  private var value: Any? = null
  private var dirty = true

  override operator fun get(thisRef: T, property: PropertyMetadata): V {
    if (dirty) {
      val extra = name ?: property.name
      val bundle = onGetArgumentsFromSource(source) ?: Bundle.EMPTY

      value = if (bundle.containsKey(extra)) {
        onGetExtraFromBundle(bundle, extra, clazz)
      } else {
        null
      }

      dirty = false
    }

    @Suppress("UNCHECKED_CAST")
    return (value ?: default) as V? ?: throw IllegalArgumentException("Key ${name ?: property.name} is missed")
  }

  override operator fun set(thisRef: T, property: PropertyMetadata, value: V) {
    val bundle = onGetArgumentsFromSource(source)
    val target = bundle ?: Bundle()
    val extra = name ?: property.name

    if (bundle == null) {
      onSetArgumentsToSource(source, target)
    }

    if (value != null) {
      onSetExtraToBundle(target, extra, clazz, value)
    } else {
      target.remove(extra)
    }

    dirty = true
  }
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
public class OptionalArgumentsVar<T, V>(
    private val clazz: Class<V>,
    private val source: Any,
    private val name: String?,
    private val default: V?
) : ReadWriteProperty<T, V?> {
  private var value: Any? = null
  private var dirty = true

  override operator fun get(thisRef: T, property: PropertyMetadata): V? {
    if (dirty) {
      val extra = name ?: property.name
      val bundle = onGetArgumentsFromSource(source) ?: Bundle.EMPTY

      value = if (bundle.containsKey(extra)) {
        onGetExtraFromBundle(bundle, extra, clazz)
      } else {
        null
      }

      dirty = false
    }

    @Suppress("UNCHECKED_CAST")
    return (value ?: default) as V?
  }

  override operator fun set(thisRef: T, property: PropertyMetadata, value: V?) {
    val bundle = onGetArgumentsFromSource(source)
    val target = bundle ?: Bundle()
    val extra = name ?: property.name

    if (bundle == null) {
      onSetArgumentsToSource(source, target)
    }

    if (value != null) {
      onSetExtraToBundle(target, extra, clazz, value)
    } else {
      target.remove(extra)
    }

    dirty = true
  }
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private fun <T> onGetExtraFromBundle(bundle: Bundle, extra: String, clazz: Class<T>): Any? {
  return when {
    clazz == kotlin.Boolean::class.java -> bundle.getBoolean(extra)
    clazz == kotlin.Double::class.java -> bundle.getDouble(extra)
    clazz == kotlin.Int::class.java -> bundle.getInt(extra)
    clazz == kotlin.Long::class.java -> bundle.getLong(extra)
    clazz == kotlin.String::class.java -> bundle.getString(extra)
    clazz == kotlin.CharSequence::class.java -> bundle.getCharSequence(extra)
    clazz == kotlin.Float::class.java -> bundle.getFloat(extra)

    clazz == java.lang.Boolean::class.java -> bundle.getBoolean(extra)
    clazz == java.lang.Double::class.java -> bundle.getDouble(extra)
    clazz == java.lang.Integer::class.java -> bundle.getInt(extra)
    clazz == java.lang.Long::class.java -> bundle.getLong(extra)
    clazz == java.lang.String::class.java -> bundle.getString(extra)
    clazz == java.lang.CharSequence::class.java -> bundle.getCharSequence(extra)
    clazz == java.lang.Float::class.java -> bundle.getFloat(extra)

    clazz == Parcelable::class.java -> bundle.getParcelable(extra)
    clazz == Serializable::class.java -> bundle.getSerializable(extra)

    kotlin.Enum::class.java.isAssignableFrom(clazz) -> bundle.getEnum(extra, clazz)
    java.lang.Enum::class.java.isAssignableFrom(clazz) -> bundle.getEnum(extra, clazz)

    else -> throw UnsupportedOperationException()
  }
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
private fun <T> onSetExtraToBundle(bundle: Bundle, extra: String, clazz: Class<T>, value: T) {
  when {
    clazz == kotlin.Boolean::class.java -> bundle.putBoolean(extra, value as Boolean)
    clazz == kotlin.Double::class.java -> bundle.putDouble(extra, value as Double)
    clazz == kotlin.Int::class.java -> bundle.putInt(extra, value as Int)
    clazz == kotlin.Long::class.java -> bundle.putLong(extra, value as Long)
    clazz == kotlin.String::class.java -> bundle.putString(extra, value as String)
    clazz == kotlin.CharSequence::class.java -> bundle.putCharSequence(extra, value as CharSequence)
    clazz == kotlin.Float::class.java -> bundle.putFloat(extra, value as Float)
    clazz == kotlin.Enum::class.java -> bundle.putEnum(extra, value as Enum<*>)

    clazz == java.lang.Boolean::class.java -> bundle.putBoolean(extra, value as Boolean)
    clazz == java.lang.Double::class.java -> bundle.putDouble(extra, value as Double)
    clazz == java.lang.Integer::class.java -> bundle.putInt(extra, value as Int)
    clazz == java.lang.Long::class.java -> bundle.putLong(extra, value as Long)
    clazz == java.lang.String::class.java -> bundle.putString(extra, value as String)
    clazz == java.lang.CharSequence::class.java -> bundle.putCharSequence(extra, value as CharSequence)
    clazz == java.lang.Float::class.java -> bundle.putFloat(extra, value as Float)
    clazz == java.lang.Enum::class.java -> bundle.putEnum(extra, value as Enum<*>)

    clazz == Parcelable::class.java -> bundle.putParcelable(extra, value as Parcelable)
    clazz == Serializable::class.java -> bundle.putSerializable(extra, value as Serializable)

    kotlin.Enum::class.java.isAssignableFrom(clazz) -> bundle.putEnum(extra, value as Enum<*>)
    java.lang.Enum::class.java.isAssignableFrom(clazz) -> bundle.putEnum(extra, value as Enum<*>)

    else -> throw UnsupportedOperationException()
  }
}

private fun onGetArgumentsFromSource(source: Any): Bundle? {
  return when (source) {
    is Bundle -> source
    is Intent -> source.extras
    is ArgumentsAware -> source.arguments
    is Activity -> source.intent.extras
    is Fragment -> source.arguments
    is android.support.v4.app.Fragment -> source.arguments
    else -> throw IllegalArgumentException("Unable to get arguments on type ${source.javaClass.simpleName}")
  }
}

private fun onSetArgumentsToSource(source: Any, bundle: Bundle) {
  when (source) {
    is Bundle -> source.replaceExtras(bundle)
    is Intent -> source.replaceExtras(bundle)
    is ArgumentsAware -> source.arguments = bundle
    is Activity -> source.intent.replaceExtras(bundle)
    is Fragment -> source.arguments = bundle
    is android.support.v4.app.Fragment -> source.arguments = bundle
    else -> throw IllegalArgumentException("Unable to set arguments on type ${source.javaClass.simpleName}")
  }
}

private fun Bundle.replaceExtras(extras: Bundle) {
  clear()
  putAll(extras)
}

private fun <E : Any?> Bundle.getEnum(name: String, clazz: Class<E>): E {
  val string = getString(name, "")

  if (!TextUtils.isEmpty(string)) {
    return clazz.enumConstants.firstOrNull {
      string == (it as Enum<*>).name()
    } ?: clazz.enumConstants[0]
  }

  return clazz.enumConstants[0]
}

private fun Bundle.putEnum(name: String, value: Enum<*>) {
  putString(name, value.name())
}
