# Kotlin Jetpack [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Kotlin%20Jetpack-green.svg?style=flat)](https://android-arsenal.com/details/1/2588)
A collection of useful extension methods for Android

* [Arguments Bindings](#arguments-bindings)
* [Preferences Bindings](#preferences-bindings)
* [Resources Bindings](#resources-bindings)

# Arguments Bindings
```kotlin
public class ArgumentsFragment : Fragment() {
  public companion object {
    public fun newInstance(): ArgumentsFragment = ArgumentsFragment().apply {
      arguments = Bundle().apply {
        putBoolean("extra_boolean", true)
      }
    }
  }
  
  // Required binding without default value
  val booleanOrThrow by bindArgument<Boolean>("extra_boolean")

  // Required binding with default value
  val booleanOrDefault by bindArgument<Boolean>("extra_boolean", false)

  // Optional binding
  val booleanOrNull by bindOptionalArgument<Boolean>("extra_boolean")
}
```
These methods can be used with `Activity`, `Fragment`, and support library `Fragment` subclasses. You can also implement `ArgumentsAware` interface to provide a custom arguments source. Full list of supported bindings:
- `bindArgument<Boolean>` / `bindOptionalArgument<Boolean>`
- `bindArgument<Double>` / `bindOptionalArgument<Double>`
- `bindArgument<Int>` / `bindOptionalArgument<Int>`
- `bindArgument<Long>` / `bindOptionalArgument<Long>`
- `bindArgument<String>` / `bindOptionalArgument<String>`
- `bindArgument<CharSequence>` / `bindOptionalArgument<CharSequence>`
- `bindArgument<Float>` / `bindOptionalArgument<Float>`
- `bindArgument<Enum>` / `bindOptionalArgument<Enum>`
- `bindArgument<Parcelable>` / `bindOptionalArgument<Parcelable>`
- `bindArgument<Serializable>` / `bindOptionalArgument<Serializable>`

Every `bindXXXArgument` returns a `ReadWriteProperty` so they can be used with `var`'s as well. In this case you don't have to deal with `Bundle` at all. No explicit `Bundle` creation, no silly `EXTRA_XXX` constants, no annoying `Bundle.putXXX` and `Bundle.getXXX` calls. Everything just works:
```kotlin
public class UserProfileFragment : Fragment() {
  public companion object {
    public fun newInstance(): UserProfileFragment = UserProfileFragment().apply {
      this.firstName = "Vladimir"
      this.lastName = "Mironov"
    }
  }
  
  // extra name is automatically inferred from property name ("firstName" in this case)
  var firstName by bindArgument<String>()

  // you can also provide a default value using "default" named argument
  var lastName by bindArgument<String>(default = "")
}
```

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-arguments:0.14.1"
```

# Preferences Bindings
```kotlin
public class PreferencesFragment : Fragment() {
  // Boolean preference
  var boolean by bindPreference<Boolean>("boolean", false)

  // Float preference
  var float by bindPreference<Float>("float", 0.0f)

  // Integer preference
  var integer by bindPreference<Int>("integer", 1)

  // Long preference
  var long by bindPreference<Long>("long", 1L)

  // String preference
  var string by bindPreference<String>("string", "default")
}
```

These methods can be used with `Context`, `Fragment`, support library `Fragment`, `View`, and `ViewHolder` subclasses. The example above uses a default `SharedPreferences` instance. You can always provide a custom one by implementing `PreferencesAware` interface:
```kotlin
public class PreferencesFragment : Fragment() {
  val preferences = PreferencesAware {
    activity.getSharedPreferences("CustomSharedPreferences", Context.MODE_PRIVATE)
  }
  
  var boolean by preferences.bindPreference<Boolean>("boolean", false)
  var float by preferences.bindPreference<Float>("float", 0.0f)
  var integer by preferences.bindPreference<Int>("integer", 1)
  var long by preferences.bindPreference<Long>("long", 1L)
  var string by preferences.bindPreference<String>("string", "default")
  
  // Optional preferences are supported as well
  var optionalLong by preferences.bindOptionalPreference<Long>()
  var optionalString by preferences.bindOptionalPreference<String>()
}
```
Although only `Boolean`, `Float`, `Int`, `Long` and `String` preferences are supported by default, the library can be easily extented to support custom type of preference. `Adapter` interface can be implemented in order to convert any type to a supported one. Here is an example how to imlement `json`-based preferences using `Gson`:
```kotlin
public inline fun <reified E : Any> Any.bindGsonPreference(default: E, key: String? = null): ReadWriteProperty<Any, E> {
  return bindPreference(default, GsonPreferenceAdapter(E::class.java), key)
}

public inline fun <reified E : Any> Any.bindGsonPreference(noinline default: () -> E, key: String? = null): ReadWriteProperty<Any, E> {
  return bindPreference(default, GsonPreferenceAdapter(E::class.java), key)
}

public class GsonPreferenceAdapter<T>(val clazz: Class<T>, val gson: Gson = GsonPreferenceAdapter.GSON) : Adapter<T, String> {
  override fun type(): Class<String> = String::class.java
  override fun fromPreference(preference: String): T = gson.fromJson(preference, clazz)
  override fun toPreference(value: T): String = gson.toJson(value)

  public companion object {
    public val GSON = Gson()
  }
}

```
Usage: 
```kotlin
public data class Profile(val firstName: String? = null, val lastName: String? = null)

public class ProfileManager(val context: Context) {
  public var profile by bindGsonPreference(Profile())
}
```

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-preferences:0.14.1"
```

# Resources Bindings
```kotlin
public class ResourcesFragment : Fragment() {
  // Boolean resource binding
  val boolean by bindResource<Boolean>(R.boolean.boolean_resource)

  // Color resource binding
  val color by bindResource<Int>(R.color.color_resource)

  // Drawable resource binding #1
  val bitmap by bindResource<BitmapDrawable>(R.drawable.drawable_bitmap)
  
  // Drawable resource binding #2
  val vector by bindResource<VectorDrawable>(R.drawable.drawable_vector)

  // Dimension resource binding
  val dimension by bindResource<Int>(R.dimen.dimen_resource)

  // String resource binding
  val string by bindResource<String>(R.string.string_resource)
}
```
These methods can be used with `Activity`, `Context`, `Fragment`, support library `Fragment`, `View`, and `ViewHolder` subclasses. You can also implement `ResourcesAware` interface to provide a custom resources source. Full list of supported bindings:
- `bindResource<Boolean>(R.boolean.boolean_resource)`
- `bindResource<Int>(R.integer.integer_resource)`
- `bindResource<Int>(R.color.color_resource)`
- `bindResource<ColorStateList>(R.color.color_resource)`
- `bindResource<Drawable>(R.drawable.drawable_resource)`
- `bindResource<Int>(R.dimen.dimen_resource)`
- `bindResource<Float>(R.dimen.dimen_resource)`
- `bindResource<String>(R.string.string_resource)`
- `bindResource<CharSequence>(R.string.string_resource)`
- `bindResource<IntArray>(R.array.array_resource)`
- `bindResource<Array<String>>(R.array.array_resource)`
- `bindResource<Array<CharSequence>>(R.array.array_resource)`

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-resources:0.14.1"
```

# License

    Copyright 2015 Vladimir Mironov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
