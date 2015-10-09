package com.github.vmironov.jetpack.arguments

import android.os.Bundle

internal object SupportHelper {
  private val HAS_SUPPORT_FRAGMENTS = try {
    Class.forName("android.support.v4.app.Fragment") != null
  } catch (exception: Exception) {
    false
  }

  internal fun isFragment(target: Any): Boolean {
    return HAS_SUPPORT_FRAGMENTS && SupportFragmentHelper.isFragment(target)
  }
}

internal object SupportFragmentHelper {
  internal fun isFragment(target: Any): Boolean {
    return target is android.support.v4.app.Fragment
  }

  internal fun getArguments(target: Any): Bundle? {
    return (target as android.support.v4.app.Fragment).arguments
  }

  internal fun setArguments(target: Any, bundle: Bundle) {
    (target as android.support.v4.app.Fragment).arguments = bundle
  }
}
