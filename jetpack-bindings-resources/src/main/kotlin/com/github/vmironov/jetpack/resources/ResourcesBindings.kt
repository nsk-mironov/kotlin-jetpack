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
import com.github.vmironov.jetpack.core.LazyVal
import kotlin.properties.ReadOnlyProperty

public interface ResourcesAware {
  public companion object {
    public fun invoke(factory: () -> Resources): ResourcesAware {
      return object : ResourcesAware {
        override val resources: Resources by lazy(LazyThreadSafetyMode.NONE) {
          factory()
        }
      }
    }
  }

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

private class ResourcesVal<T, V>(private val source: Any, private val initializer: (Resources) -> V) : LazyVal<T, V>({ desc, property ->
  initializer(when (source) {
    is ResourcesAware -> source.resources
    is Context -> source.resources
    is Fragment -> source.activity.resources
    is android.support.v4.app.Fragment -> source.activity.resources
    is android.support.v7.widget.RecyclerView.ViewHolder -> source.itemView.resources
    is View -> source.resources
    is Dialog -> source.context.resources
    is Resources -> source
    else -> throw IllegalArgumentException("Unable to find resources on type ${source.javaClass.simpleName}")
  })
})
