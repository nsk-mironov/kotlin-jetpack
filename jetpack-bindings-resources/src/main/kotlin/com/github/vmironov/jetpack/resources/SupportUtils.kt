package com.github.vmironov.jetpack.resources

import android.content.res.Resources

internal object SupportHelper {
  private val FQNAME_SUPPORT_FRAGMENT = "android.support.v4.app.Fragment"
  private val FQNAME_RECYCLER_HOLDER = "android.support.v7.widget.RecyclerView.ViewHolder"

  private val HAS_SUPPORT_FRAGMENTS = hasClass(FQNAME_SUPPORT_FRAGMENT)
  private val HAS_RECYCLER_HOLDER = hasClass(FQNAME_RECYCLER_HOLDER)

  internal fun isFragment(target: Any): Boolean {
    return HAS_SUPPORT_FRAGMENTS && SupportFragmentHelper.isFragment(target)
  }

  internal fun isHolder(target: Any): Boolean {
    return HAS_RECYCLER_HOLDER && SupportRecyclerHelper.isHolder(target)
  }

  private fun hasClass(fqname: String): Boolean = try {
    Class.forName(fqname) != null
  } catch (exception: Exception) {
    false
  }
}

internal object SupportFragmentHelper {
  internal fun isFragment(target: Any): Boolean {
    return target is android.support.v4.app.Fragment
  }

  internal fun getResources(target: Any): Resources {
    return (target as android.support.v4.app.Fragment).resources
  }
}

internal object SupportRecyclerHelper {
  internal fun isHolder(target: Any): Boolean {
    return target is android.support.v7.widget.RecyclerView.ViewHolder
  }

  internal fun getResources(target: Any): Resources {
    return (target as android.support.v7.widget.RecyclerView.ViewHolder).itemView.resources
  }
}


