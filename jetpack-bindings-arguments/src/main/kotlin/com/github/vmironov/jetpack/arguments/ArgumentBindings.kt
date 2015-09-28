package com.github.vmironov.jetpack.arguments

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import com.github.vmironov.jetpack.core.LazyVal

public interface ArgumentsAware {
  public val arguments: Bundle?
}

private class ArgumentsVal<T, V>(private val source: Any, private val initializer: (Bundle) -> V) : LazyVal<T, V>({ desc, property ->
  initializer(when (source) {
    is Activity -> source.intent.extras
    is Fragment -> source.arguments
    is android.support.v4.app.Fragment -> source.arguments
    is ArgumentsAware -> source.arguments
    else -> throw IllegalArgumentException("Unable to find arguments on type ${source.javaClass.simpleName}")
  })
})
