package com.github.vmironov.jetpack.arguments

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment as SupportFragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface ArgumentsAware {
  var arguments: Bundle?
}

inline fun <reified T : Any> Any.bindArgument(name: String? = null, default: T? = null, adapter: Adapter<T>? = null): ReadWriteProperty<Any, T> {
  return ArgumentsVar(T::class.java, adapter, this, name, default)
}

inline fun <reified T : Any> Any.bindOptionalArgument(name: String? = null, default: T? = null, adapter: Adapter<T>? = null): ReadWriteProperty<Any, T?> {
  return OptionalArgumentsVar(T::class.java, adapter, this, name, default)
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
class ArgumentsVar<T, V>(
    private val clazz: Class<V>,
    private val adapter: Adapter<V>?,
    private val source: Any,
    private val name: String?,
    private val default: V?
) : ReadWriteProperty<T, V> {
  private val delegate = ArgumentsVarDelegate<T, V>(adapter ?: createTypeAdapterFor(clazz), source, name, default)

  override operator fun getValue(thisRef: T, property: KProperty<*>): V {
    return delegate.getValue(thisRef, property) ?: throw IllegalArgumentException("Key ${name ?: property.name} is missed")
  }

  override operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
    delegate.setValue(thisRef, property, value)
  }
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
class OptionalArgumentsVar<T, V>(
    private val clazz: Class<V>,
    private val adapter: Adapter<V>?,
    private val source: Any,
    private val name: String?,
    private val default: V?
) : ReadWriteProperty<T, V?> {
  private val delegate = ArgumentsVarDelegate<T, V>(adapter ?: createTypeAdapterFor(clazz), source, name, default)

  override operator fun getValue(thisRef: T, property: KProperty<*>): V? {
    return delegate.getValue(thisRef, property)
  }

  override operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
    delegate.setValue(thisRef, property, value)
  }
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
private class ArgumentsVarDelegate<T, V>(
    private val adapter: Adapter<V>,
    private val source: Any,
    private val name: String?,
    private val default: V?
) : ReadWriteProperty<T, V?> {

  override operator fun getValue(thisRef: T, property: KProperty<*>): V? {
    val extra = name ?: property.name
    val bundle = getArgumentsFromSource(source) ?: Bundle.EMPTY

    val value = if (bundle.containsKey(extra)) {
      adapter[bundle, extra]
    } else {
      null
    }

    @Suppress("UNCHECKED_CAST")
    return value ?: default
  }

  override operator fun setValue(thisRef: T, property: KProperty<*>, value: V?) {
    val bundle = getArgumentsFromSource(source)
    val target = bundle ?: Bundle()
    val extra = name ?: property.name

    if (bundle == null) {
      setArgumentsToSource(source, target)
    }

    if (value != null) {
      adapter[target, extra] = value
    } else {
      target.remove(extra)
    }
  }

  private fun getArgumentsFromSource(source: Any): Bundle? {
    return when (source) {
      is ArgumentsAware -> source.arguments
      is SupportFragment -> source.arguments
      is Activity -> source.intent.extras
      is Bundle -> source
      is Intent -> source.extras
      is Fragment -> source.arguments
      else -> throw IllegalArgumentException("Unable to get arguments on type ${source.javaClass.simpleName}")
    }
  }

  private fun setArgumentsToSource(source: Any, bundle: Bundle) {
    when (source) {
      is ArgumentsAware -> source.arguments = bundle
      is SupportFragment -> source.arguments = bundle
      is Activity -> source.intent.replaceExtras(bundle)
      is Bundle -> source.replaceExtras(bundle)
      is Intent -> source.replaceExtras(bundle)
      is Fragment -> source.arguments = bundle
      else -> throw IllegalArgumentException("Unable to set arguments on type ${source.javaClass.simpleName}")
    }
  }
}

private fun Bundle.replaceExtras(extras: Bundle) {
  clear()
  putAll(extras)
}

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
private fun <T> createTypeAdapterFor(clazz: Class<T>): Adapter<T> {
  fun typed(desiredClass: Class<*>): Boolean {
    return desiredClass.isAssignableFrom(clazz)
  }

  return when {
    typed(kotlin.Boolean::class.java) -> BooleanAdapter
    typed(kotlin.Double::class.java) -> DoubleAdapter
    typed(kotlin.Int::class.java) -> IntAdapter
    typed(kotlin.Long::class.java) -> LongAdapter
    typed(kotlin.String::class.java) -> StringAdapter
    typed(kotlin.CharSequence::class.java) -> CharSequenceAdapter
    typed(kotlin.Float::class.java) -> FloatAdapter
    typed(kotlin.Enum::class.java) -> EnumAdapter(clazz as Class<Enum<*>>)

    typed(java.lang.Boolean::class.java) -> BooleanAdapter
    typed(java.lang.Double::class.java) -> DoubleAdapter
    typed(java.lang.Integer::class.java) -> IntAdapter
    typed(java.lang.Long::class.java) -> LongAdapter
    typed(java.lang.String::class.java) -> StringAdapter
    typed(java.lang.CharSequence::class.java) -> CharSequenceAdapter
    typed(java.lang.Float::class.java) -> FloatAdapter
    typed(java.lang.Enum::class.java) -> EnumAdapter(clazz as Class<Enum<*>>)

    typed(Parcelable::class.java) -> ParcelableAdapter
    typed(Serializable::class.java) -> SerializableAdapter

    else -> throw UnsupportedOperationException("Unable to create a type Adapter for \"${clazz.name}\"")
  } as Adapter<T>
}

interface Adapter<T> {
  operator fun set(bundle: Bundle, name: String, value: T): Unit
  operator fun get(bundle: Bundle, name: String): T
}

private object BooleanAdapter : Adapter<Boolean> {
  override operator fun set(bundle: Bundle, name: String, value: Boolean) {
    bundle.putBoolean(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Boolean {
    return bundle.getBoolean(name)
  }
}

private object DoubleAdapter : Adapter<Double> {
  override operator fun set(bundle: Bundle, name: String, value: Double) {
    bundle.putDouble(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Double {
    return bundle.getDouble(name)
  }
}

private object IntAdapter : Adapter<Int> {
  override operator fun set(bundle: Bundle, name: String, value: Int) {
    bundle.putInt(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Int {
    return bundle.getInt(name)
  }
}

private object LongAdapter : Adapter<Long> {
  override operator fun set(bundle: Bundle, name: String, value: Long) {
    bundle.putLong(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Long {
    return bundle.getLong(name)
  }
}

private object StringAdapter : Adapter<String> {
  override operator fun set(bundle: Bundle, name: String, value: String) {
    bundle.putString(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): String {
    return bundle.getString(name)
  }
}

private object CharSequenceAdapter : Adapter<CharSequence> {
  override operator fun set(bundle: Bundle, name: String, value: CharSequence) {
    bundle.putCharSequence(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): CharSequence {
    return bundle.getCharSequence(name)
  }
}

private object FloatAdapter : Adapter<Float> {
  override operator fun set(bundle: Bundle, name: String, value: Float) {
    bundle.putFloat(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Float {
    return bundle.getFloat(name)
  }
}

private class EnumAdapter(val clazz: Class<Enum<*>>) : Adapter<Enum<*>> {
  override operator fun set(bundle: Bundle, name: String, value: Enum<*>) {
    bundle.putString(name, value.name)
  }

  override operator fun get(bundle: Bundle, name: String): Enum<*> {
    return clazz.enumConstants.firstOrNull {
      it.name == bundle.getString(name)
    } ?: throw IllegalArgumentException("\"$name\" is not a constant in \"${clazz.name}\"")
  }
}

private object ParcelableAdapter : Adapter<Parcelable> {
  override operator fun set(bundle: Bundle, name: String, value: Parcelable) {
    bundle.putParcelable(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Parcelable {
    return bundle.getParcelable(name)
  }
}

private object SerializableAdapter : Adapter<Serializable> {
  override operator fun set(bundle: Bundle, name: String, value: Serializable) {
    bundle.putSerializable(name, value)
  }

  override operator fun get(bundle: Bundle, name: String): Serializable {
    return bundle.getSerializable(name)
  }
}
