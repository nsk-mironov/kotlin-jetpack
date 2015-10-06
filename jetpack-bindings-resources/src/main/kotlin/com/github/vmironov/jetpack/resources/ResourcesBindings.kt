package com.github.vmironov.jetpack.resources

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlin.properties.ReadOnlyProperty

public interface ResourcesAware {
  public companion object {
    public operator fun invoke(factory: () -> Resources): ResourcesAware = object : ResourcesAware {
      override val resources: Resources by lazy(LazyThreadSafetyMode.NONE) {
        factory()
      }
    }
  }

  public val resources: Resources
}

public inline fun <reified T : Any> ResourcesAware.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> Context.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> Fragment.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> android.support.v4.app.Fragment.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> RecyclerView.ViewHolder.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> View.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

public inline fun <reified T : Any> Resources.bindResource(id: Int): ReadOnlyProperty<Any, T> {
  return ResourcesVal(T::class.java, this, id)
}

@Suppress("UNCHECKED_CAST")
public class ResourcesVal<T : Any, V : Any>(
    private val clazz: Class<V>,
    private val source: Any,
    private val id: Int
) : LazyVal<T, V>({ desc, property ->
  val resources = when (source) {
    is ResourcesAware -> source.resources
    is Resources -> source as Resources
    is Context -> source.resources
    is Fragment -> source.activity.resources
    is android.support.v4.app.Fragment -> source.activity.resources
    is android.support.v7.widget.RecyclerView.ViewHolder -> source.itemView.resources
    is View -> source.resources
    is Dialog -> source.context.resources
    else -> throw IllegalArgumentException("Unable to find resources on type ${source.javaClass.simpleName}")
  }

  fun unsupported(id: Int, clazz: Class<*>): UnsupportedOperationException {
    return UnsupportedOperationException("Unsupported resource (name = \"${resources.getResourceName(id)}\", type = \"${clazz}\")")
  }

  @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "DEPRECATED_SYMBOL_WITH_MESSAGE")
  val value: Any = when (resources.getResourceTypeName(id)) {
    "drawable" -> when {
      Drawable::class.java.isAssignableFrom(clazz) -> {
        resources.getDrawable(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "bool" -> when {
      clazz === kotlin.Boolean::class.java, clazz === java.lang.Boolean::class.java -> {
        resources.getBoolean(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "integer" -> when {
      clazz === kotlin.Int::class.java, clazz === java.lang.Integer::class.java -> {
        resources.getInteger(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "color" -> when {
      clazz === kotlin.Int::class.java, clazz === java.lang.Integer::class.java -> {
        resources.getColor(id)
      }

      clazz === ColorStateList::class.java -> {
        resources.getColorStateList(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "dimen" -> when {
      clazz === kotlin.Float::class.java, clazz === java.lang.Float::class.java -> {
        resources.getDimension(id)
      }

      clazz === kotlin.Int::class.java, clazz === java.lang.Integer::class.java -> {
        resources.getDimensionPixelSize(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "string" -> when {
      clazz === kotlin.String::class.java, clazz === java.lang.String::class.java -> {
        resources.getString(id)
      }

      clazz === kotlin.CharSequence::class.java, clazz === java.lang.CharSequence::class.java -> {
        resources.getText(id)
      }

      else -> throw unsupported(id, clazz)
    }

    "array" -> when {
      clazz === IntArray::class.java, clazz === Array<kotlin.Int>::class.java, clazz === Array<java.lang.Integer>::class.java -> {
        resources.getIntArray(id)
      }

      clazz === Array<kotlin.CharSequence>::class.java, clazz === Array<java.lang.CharSequence>::class.java -> {
        resources.getTextArray(id)
      }

      clazz === Array<kotlin.String>::class.java, clazz === Array<java.lang.String>::class.java -> {
        resources.getStringArray(id)
      }

      else -> throw unsupported(id, clazz)
    }

    else -> throw unsupported(id, clazz)
  }

  value as V
})

private open class LazyVal<T, V>(private val initializer : (T, PropertyMetadata) -> V) : ReadOnlyProperty<T, V> {
  private object EMPTY
  private var value: Any? = EMPTY

  override operator fun get(thisRef: T, property: PropertyMetadata): V {
    if (value === EMPTY) {
      value = initializer(thisRef, property)
    }
    @Suppress("UNCHECKED_CAST")
    return value as V
  }
}
