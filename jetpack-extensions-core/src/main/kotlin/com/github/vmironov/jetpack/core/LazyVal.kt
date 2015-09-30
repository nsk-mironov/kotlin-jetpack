package com.github.vmironov.jetpack.core

import kotlin.properties.ReadOnlyProperty

public open class LazyVal<T, V>(private val initializer: (thisRef: T, property: PropertyMetadata) -> V) : ReadOnlyProperty<T, V> {
  private var value: Any? = null

  public override fun get(thisRef: T, property: PropertyMetadata): V {
    if (value == null) {
      value = escape(initializer(thisRef, property))
    }

    return unescape(value) as V
  }
}

private object NULL_VALUE

private fun escape(value: Any?): Any {
  return value ?: NULL_VALUE
}

private fun unescape(value: Any?): Any? {
  return if (value === NULL_VALUE) null else value
}
