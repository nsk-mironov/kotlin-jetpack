package com.github.vmironov.jetpack.resources

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.XmlResourceParser
import android.graphics.Movie
import android.graphics.drawable.Drawable
import android.view.View
import kotlin.properties.ReadOnlyProperty

public interface ResourcesAware {
  public val resources: Resources
}

public fun Any.bindAnimationResource(resource: Int): ReadOnlyProperty<Any, XmlResourceParser> = ResourcesVal(this) {
  it.getAnimation(resource)
}

public fun Any.bindBooleanResource(resource: Int): ReadOnlyProperty<Any, Boolean> = ResourcesVal(this) {
  it.getBoolean(resource)
}

public fun Any.bingColorResource(resource: Int): ReadOnlyProperty<Any, Int> = ResourcesVal(this) {
  it.getColor(resource)
}

public fun Any.bindColorStateListResource(resource: Int): ReadOnlyProperty<Any, ColorStateList> = ResourcesVal(this) {
  it.getColorStateList(resource)
}

public fun Any.bindDimensionResource(resource: Int): ReadOnlyProperty<Any, Float> = ResourcesVal(this) {
  it.getDimension(resource)
}

public fun Any.bindDimensionPixelOffsetResource(resource: Int): ReadOnlyProperty<Any, Int> = ResourcesVal(this) {
  it.getDimensionPixelOffset(resource)
}

public fun Any.bindDimensionPixelSizeResource(resource: Int): ReadOnlyProperty<Any, Int> = ResourcesVal(this) {
  it.getDimensionPixelSize(resource)
}

public fun <D : Drawable> Any.bindDrawableResource(resource: Int): ReadOnlyProperty<Any, D> = ResourcesVal(this) {
  it.getDrawable(resource) as D
}

public fun Any.bindIntArrayResource(resource: Int): ReadOnlyProperty<Any, IntArray> = ResourcesVal(this) {
  it.getIntArray(resource)
}

public fun Any.bindIntegerResource(resource: Int): ReadOnlyProperty<Any, Int> = ResourcesVal(this) {
  it.getInteger(resource)
}

public fun Any.bindLayoutResource(resource: Int): ReadOnlyProperty<Any, XmlResourceParser> = ResourcesVal(this) {
  it.getLayout(resource)
}

public fun Any.bindMovieResource(resource: Int): ReadOnlyProperty<Any, Movie> = ResourcesVal(this) {
  it.getMovie(resource)
}

public fun Any.bindStringResource(resource: Int): ReadOnlyProperty<Any, String> = ResourcesVal(this) {
  it.getString(resource)
}

public fun Any.bindStringArrayResource(resource: Int): ReadOnlyProperty<Any, Array<out String>> = ResourcesVal(this) {
  it.getStringArray(resource)
}

public fun Any.bindTextResource(resource: Int): ReadOnlyProperty<Any, CharSequence> = ResourcesVal(this) {
  it.getText(resource)
}

public fun Any.bindTextArrayResource(resource: Int): ReadOnlyProperty<Any, Array<out CharSequence>> = ResourcesVal(this) {
  it.getTextArray(resource)
}

private class ResourcesVal<T, V>(private val source: Any, private val initializer: (Resources) -> V) : ReadOnlyProperty<T, V> {
  private var value: Any? = null

  public override fun get(thisRef: T, property: PropertyMetadata): V {
    if (value == null) {
      value = escape(initializer(getResources(source)))
    }

    return unescape(value) as V
  }

  private fun getResources(target: Any): Resources = when (target) {
    is Context -> target.resources
    is Fragment -> target.activity.resources
    is android.support.v4.app.Fragment -> target.activity.resources
    is android.support.v7.widget.RecyclerView.ViewHolder -> target.itemView.resources
    is View -> target.resources
    is Dialog -> target.context.resources
    is Resources -> target
    is ResourcesAware -> target.resources
    else -> throw IllegalArgumentException("Unable to find resources on type ${target.javaClass.simpleName}")
  }
}

private object NULL_VALUE

private fun escape(value: Any?): Any {
  return value ?: NULL_VALUE
}

private fun unescape(value: Any?): Any? {
  return if (value === NULL_VALUE) null else value
}
