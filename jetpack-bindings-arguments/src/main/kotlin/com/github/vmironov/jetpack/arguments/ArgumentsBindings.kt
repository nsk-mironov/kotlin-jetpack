package com.github.vmironov.jetpack.arguments

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import kotlin.properties.ReadOnlyProperty

public interface ArgumentsAware {
  public companion object {
    public fun invoke(factory: () -> Bundle?): ArgumentsAware = object : ArgumentsAware {
      override val arguments: Bundle? by lazy(LazyThreadSafetyMode.NONE) {
        factory()
      }
    }
  }

  public val arguments: Bundle?
}

internal class ArgumentsVal<T, V>(
    private val source: Any,
    private val name: String?,
    private val default: V?,
    private val initializer: (String, Bundle) -> V?
) : LazyVal<T, V>({ desc, property ->
  val value = initializer(name ?: property.name, when (source) {
    is Bundle -> source
    is Intent -> source.extras ?: Bundle.EMPTY
    is ArgumentsAware -> source.arguments ?: Bundle.EMPTY
    is Activity -> source.intent.extras ?: Bundle.EMPTY
    is Fragment -> source.arguments ?: Bundle.EMPTY
    is android.support.v4.app.Fragment -> source.arguments ?: Bundle.EMPTY
    else -> throw IllegalArgumentException("Unable to find arguments on type ${source.javaClass.simpleName}")
  })

  if (value == null && default == null) {
    throw IllegalArgumentException("Key ${name ?: property.name} is missed")
  }

  value ?: default!!
})

internal class OptionalArgumentsVal<T, V>(
    private val source: Any,
    private val name: String?,
    private val default: V?,
    private val initializer: (String, Bundle) -> V?
) : LazyVal<T, V?>({ desc, property ->
  initializer(name ?: property.name, when (source) {
    is Bundle -> source
    is Intent -> source.extras ?: Bundle.EMPTY
    is ArgumentsAware -> source.arguments ?: Bundle.EMPTY
    is Activity -> source.intent.extras ?: Bundle.EMPTY
    is Fragment -> source.arguments ?: Bundle.EMPTY
    is android.support.v4.app.Fragment -> source.arguments ?: Bundle.EMPTY
    else -> throw IllegalArgumentException("Unable to find arguments on type ${source.javaClass.simpleName}")
  }) ?: default
})

private open class LazyVal<T, V>(private val initializer : (T, PropertyMetadata) -> V) : ReadOnlyProperty<T, V> {
  private object EMPTY
  private var value: Any? = EMPTY

  override fun get(thisRef: T, property: PropertyMetadata): V {
    if (value === EMPTY) {
      value = initializer(thisRef, property)
    }
    @Suppress("UNCHECKED_CAST")
    return value as V
  }
}
