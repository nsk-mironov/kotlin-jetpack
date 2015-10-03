package com.github.vmironov.jetpack.arguments

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import kotlin.properties.ReadWriteProperty

public interface ArgumentsAware {
  public var arguments: Bundle?
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
internal class ArgumentsVar<T, V>(
    private val source: Any,
    private val name: String?,
    private val default: V?,
    private val getter: (String, Bundle) -> V?,
    private val setter: (String, Bundle, V?) -> Unit
) : ReadWriteProperty<T, V> {
  private var value: Any? = null
  private var dirty = true

  override operator fun get(thisRef: T, property: PropertyMetadata): V {
    if (dirty) {
      value = getter(name ?: property.name, getArgumentsFromSource(source) ?: Bundle.EMPTY)
      dirty = false
    }

    @Suppress("UNCHECKED_CAST")
    return (value ?: default) as V? ?: throw IllegalArgumentException("Key ${name ?: property.name} is missed")
  }

  override operator fun set(thisRef: T, property: PropertyMetadata, value: V) {
    val bundle = getArgumentsFromSource(source)
    val result = bundle ?: Bundle()

    if (bundle == null) {
      setArgumentsToSource(source, result)
    }

    setter(name ?: property.name, result, value)
    dirty = true
  }
}

@Suppress("BASE_WITH_NULLABLE_UPPER_BOUND")
internal class OptionalArgumentsVar<T, V>(
    private val source: Any,
    private val name: String?,
    private val default: V?,
    private val getter: (String, Bundle) -> V?,
    private val setter: (String, Bundle, V?) -> Unit
) : ReadWriteProperty<T, V?> {
  private var value: Any? = null
  private var dirty = true

  override operator fun get(thisRef: T, property: PropertyMetadata): V? {
    if (dirty) {
      value = getter(name ?: property.name, getArgumentsFromSource(source) ?: Bundle.EMPTY)
      dirty = false
    }

    @Suppress("UNCHECKED_CAST")
    return (value ?: default) as V?
  }

  override operator fun set(thisRef: T, property: PropertyMetadata, value: V?) {
    val bundle = getArgumentsFromSource(source)
    val target = bundle ?: Bundle()

    if (bundle == null) {
      setArgumentsToSource(source, target)
    }

    setter(name ?: property.name, target, value)
    dirty = true
  }
}

private fun getArgumentsFromSource(source: Any): Bundle? {
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

private fun setArgumentsToSource(source: Any, bundle: Bundle) {
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
