package com.github.vmironov.jetpack.resources

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> Any.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

interface ResourcesAware {
  companion object {
    operator fun invoke(factory: () -> Pair<Context, Resources>): ResourcesAware = object : ResourcesAware {
      override val resources: Pair<Context, Resources> by lazy(LazyThreadSafetyMode.NONE) {
        factory()
      }
    }
  }

  val resources: Pair<Context, Resources>
}

@Suppress("UNCHECKED_CAST", "USELESS_CAST") class ResourcesVal<T : Any, V : Any>(
        private val clazz: Class<V>,
        private val source: Any,
        private val id: Int
) : ReadOnlyProperty<T, V> {
  private var value: Any? = Unit

  override operator fun getValue(thisRef: T, property: KProperty<*>): V {
    if (value === Unit) {
      value = onLazyGetValue(when {
        source is ResourcesAware -> source.resources
        source is Context -> source to source.resources
        source is Fragment -> source.activity to source.activity.resources
        source is View -> source.context to source.resources
        SupportHelper.isFragment(source) -> SupportFragmentHelper.getResources(source)
        SupportHelper.isHolder(source) -> SupportRecyclerHelper.getResources(source)
        source is Dialog -> source.context to source.context.resources
        else -> throw IllegalArgumentException("Unable to find resources on type ${source.javaClass.simpleName}")
      })
    }

    return value as V
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "DEPRECATED_SYMBOL_WITH_MESSAGE", "IMPLICIT_CAST_TO_ANY", "DEPRECATION")
  private fun onLazyGetValue(resources: Pair<Context, Resources>): V {
    val res = resources.second
    val context = resources.first
    val type = res.getResourceTypeName(id)
    val typed = { desiredType: String, desiredClass: Class<*> ->
      desiredType == type && desiredClass.isAssignableFrom(clazz)
    }

    return when {
      typed("drawable", Drawable::class.java) -> ContextCompat.getDrawable(context, id)

      typed("bool", kotlin.Boolean::class.java) -> res.getBoolean(id)
      typed("bool", java.lang.Boolean::class.java) -> res.getBoolean(id)

      typed("integer", kotlin.Int::class.java) -> res.getInteger(id)
      typed("integer", java.lang.Integer::class.java) -> res.getInteger(id)

      typed("color", kotlin.Int::class.java) -> ContextCompat.getColor(context, id)
      typed("color", java.lang.Integer::class.java) -> ContextCompat.getColor(context, id)
      typed("color", ColorStateList::class.java) -> ContextCompat.getColorStateList(context, id)

      typed("dimen", kotlin.Float::class.java) -> res.getDimension(id)
      typed("dimen", java.lang.Float::class.java) -> res.getDimension(id)

      typed("dimen", kotlin.Int::class.java) -> res.getDimension(id)
      typed("dimen", java.lang.Integer::class.java) -> res.getDimension(id)

      typed("string", kotlin.String::class.java) -> res.getString(id)
      typed("string", java.lang.String::class.java) -> res.getString(id)

      typed("string", kotlin.CharSequence::class.java) -> res.getText(id)
      typed("string", java.lang.CharSequence::class.java) -> res.getText(id)

      typed("array", IntArray::class.java) -> res.getIntArray(id)
      typed("array", Array<kotlin.Int>::class.java) -> res.getIntArray(id)
      typed("array", Array<java.lang.Integer>::class.java) -> res.getIntArray(id)

      typed("array", Array<kotlin.String>::class.java) -> res.getStringArray(id)
      typed("array", Array<java.lang.String>::class.java) -> res.getStringArray(id)

      typed("array", Array<kotlin.CharSequence>::class.java) -> res.getTextArray(id)
      typed("array", Array<java.lang.CharSequence>::class.java) -> res.getTextArray(id)

      typed("font", Typeface::class.java) -> ResourcesCompat.getFont(context, id)

      else -> throw UnsupportedOperationException("Unsupported resource (name = \"${res.getResourceName(id)}\", type = \"${clazz.canonicalName}\")")
    } as V
  }
}
