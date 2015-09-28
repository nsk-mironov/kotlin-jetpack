package com.github.vmironov.jetpack.bundles

import android.os.Bundle

public inline fun <reified E : Enum<E>> Bundle.putEnumOrIgnore(key: String, value: E?) {
  if (value != null) {
    putInt(key, value.ordinal())
  }
}

public inline fun <reified E : Enum<E>> Bundle.getEnumOrNull(key: String): E? {
  return if (containsKey(key)) {
    return E::class.java.enumConstants[getInt(key, 0)]
  } else {
    null
  }
}

public inline fun <reified E : Enum<E>> Bundle.getEnumOrDefault(key: String, value: E): E {
  return getEnumOrNull<E>(key) ?: value
}

public inline fun <reified E : Enum<E>> Bundle.getEnumOrThrow(key: String): E {
  return getEnumOrNull<E>(key) ?: throw IllegalStateException("Key \"$key\" is missed")
}
