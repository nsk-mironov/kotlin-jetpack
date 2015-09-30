package com.github.vmironov.jetpack.intents

import android.content.Intent

public inline fun <reified E : Enum<E>> Intent.putExtraOrIgnore(key: String, value: E?) {
  if (value != null) {
    putExtra(key, value.ordinal())
  }
}

public inline fun <reified E : Enum<E>> Intent.getEnumExtraOrNull(key: String): E? {
  return if (hasExtra(key)) {
    return E::class.java.enumConstants[getIntExtra(key, 0)]
  } else {
    null
  }
}

public inline fun <reified E : Enum<E>> Intent.getEnumExtraOrDefault(key: String, value: E): E {
  return getEnumExtraOrNull<E>(key) ?: value
}

public inline fun <reified E : Enum<E>> Intent.getEnumExtraOrThrow(key: String): E {
  return getEnumExtraOrNull<E>(key) ?: throw IllegalStateException("Key \"$key\" is missed")
}
