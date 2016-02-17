package com.github.vmironov.jetpack.resources

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.support.v4.app.Fragment as SupportFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> Any.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

interface ResourcesAware {
  companion object {
    operator fun invoke(factory: () -> Resources): ResourcesAware = object : ResourcesAware {
      override val resources by lazy(LazyThreadSafetyMode.NONE) {
        factory()
      }
    }
  }

  val resources: Resources
}

@Suppress("UNCHECKED_CAST", "USELESS_CAST")
class ResourcesVal<T : Any, V : Any>(
    private val clazz: Class<V>,
    private val source: Any,
    private val id: Int
) : ReadOnlyProperty<T, V> {
  private var value: Any? = Unit

  override operator fun getValue(thisRef: T, property: KProperty<*>): V {
    if (value === Unit) {
      value = onLazyGetValue(when (source) {
        is ResourcesAware -> source.resources
        is Context -> source.resources
        is Fragment -> source.activity.resources
        is Resources -> source as Resources
        is View -> source.resources
        is SupportFragment -> source.activity.resources
        is Dialog -> source.context.resources
        else -> throw IllegalArgumentException("Unable to find resources on type ${source.javaClass.simpleName}")
      })
    }

    return value as V
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "DEPRECATED_SYMBOL_WITH_MESSAGE", "IMPLICIT_CAST_TO_ANY", "DEPRECATION")
  private fun onLazyGetValue(resources: Resources): V {
    val type = resources.getResourceTypeName(id)

    val typed = fun (desiredType: String, desiredClass: Class<*>): Boolean {
      return desiredType == type && desiredClass.isAssignableFrom(clazz)
    }

    return when {
      typed("drawable", Drawable::class.java) -> resources.getDrawable(id)

      typed("bool", kotlin.Boolean::class.java) -> resources.getBoolean(id)
      typed("bool", java.lang.Boolean::class.java) -> resources.getBoolean(id)

      typed("integer", kotlin.Int::class.java) -> resources.getInteger(id)
      typed("integer", java.lang.Integer::class.java) -> resources.getInteger(id)

      typed("color", kotlin.Int::class.java) -> resources.getColor(id)
      typed("color", java.lang.Integer::class.java) -> resources.getColor(id)
      typed("color", ColorStateList::class.java) -> resources.getColorStateList(id)

      typed("dimen", kotlin.Float::class.java) -> resources.getDimension(id)
      typed("dimen", java.lang.Float::class.java) -> resources.getDimension(id)

      typed("dimen", kotlin.Int::class.java) -> resources.getDimension(id)
      typed("dimen", java.lang.Integer::class.java) -> resources.getDimension(id)

      typed("string", kotlin.String::class.java) -> resources.getString(id)
      typed("string", java.lang.String::class.java) -> resources.getString(id)

      typed("string", kotlin.CharSequence::class.java) -> resources.getText(id)
      typed("string", java.lang.CharSequence::class.java) -> resources.getText(id)

      typed("array", IntArray::class.java) -> resources.getIntArray(id)
      typed("array", Array<kotlin.Int>::class.java) -> resources.getIntArray(id)
      typed("array", Array<java.lang.Integer>::class.java) -> resources.getIntArray(id)

      typed("array", Array<kotlin.String>::class.java) -> resources.getStringArray(id)
      typed("array", Array<java.lang.String>::class.java) -> resources.getStringArray(id)

      typed("array", Array<kotlin.CharSequence>::class.java) -> resources.getTextArray(id)
      typed("array", Array<java.lang.CharSequence>::class.java) -> resources.getTextArray(id)

      else -> throw UnsupportedOperationException("Unsupported resource (name = \"${resources.getResourceName(id)}\", type = \"${clazz.canonicalName}\")")
    } as V
  }
}
