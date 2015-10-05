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
  val booleanOrThrow: Boolean by bindBooleanArgument("extra_boolean")

  // Required binding with default value
  val booleanOrDefault: Boolean by bindBooleanArgument("extra_boolean", false)

  // Optional binding
  val booleanOrNull: Boolean? by bindOptionalBooleanArgument("extra_boolean")
}
```
These methods can be used with `Activity`, `Fragment`, and support library `Fragment` subclasses. You can also implement `ArgumentsAware` interface to provide a custom arguments source. Full list of supported bindings:
- `bindBooleanArgument` / `bindOptionalBooleanArgument`
- `bindDoubleArgument` / `bindOptionalDoubleArgument`
- `bindIntArgument` / `bindOptionalIntArgument`
- `bindLongArgument` / `bindOptionalLongArgument`
- `bindStringArgument` / `bindOptionalStringArgument`
- `bindCharSequenceArgument` / `bindOptionalCharSequenceArgument`
- `bindFloatArgument` / `bindOptionalFloatArgument`
- `bindParcelableArgument` / `bindOptionalParcelableArgument`
- `bindSerializableArgument` / `bindOptionalSerializableArgument`
- `bindStringArrayListArgument` / `bindOptionalStringArrayListArgument`
- `bindIntegerArrayListArgument` / `bindOptionalIntegerArrayListArgument`
- `bindParcelableArrayListArgument` / `bindOptionalParcelableArrayListArgument`

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
  var firstName by bindStringArgument()

  // you can also provide a default value using "default" named argument
  var lastName by bindStringArgument(default = "")
}
```

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-arguments:0.12.0"
```

# Preferences Bindings
```kotlin
public class PreferencesFragment : Fragment() {
  // Boolean preference
  var boolean: Boolean by bindBooleanPreference("boolean", false)

  // Float preference
  var float: Float by bindFloatPreference("float", 0.0f)

  // Integer preference
  var integer: Int by bindIntPreference("integer", 1)

  // Long preference
  var long: Long by bindLongPreference("long", 1L)

  // String preference
  var string: String by bindStringPreference("string", "default")

  // String Set preference
  var set: Set<String> by bindStringSetPreference("string set")
}
```

These methods can be used with `Context`, `Fragment`,support library `Fragment`, `View`, and `ViewHolder` subclasses. The example above uses a default `SharedPreferences` instance. You can always provide a custom one by implementing `PreferencesAware` interface:
```kotlin
public class PreferencesFragment : Fragment() {
  val preferences = PreferencesAware {
    activity.getSharedPreferences("CustomSharedPreferences", Context.MODE_PRIVATE)
  }
  
  var boolean: Boolean by preferences.bindBooleanPreference("boolean", false)
  var float: Float by preferences.bindFloatPreference("float", 0.0f)
  var integer: Int by preferences.bindIntPreference("integer", 1)
  var long: Long by preferences.bindLongPreference("long", 1L)
  var string: String by preferences.bindStringPreference("string", "default")
}
```

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-preferences:0.12.0"
```

# Resources Bindings
```kotlin
public class ResourcesFragment : Fragment() {
  // Boolean resource binding
  val boolean: Boolean by bindBooleanResource(R.boolean.boolean_resource)

  // Color resource binding
  val color: Int by bingColorResource(R.color.color_resource)

  // Drawable resource binding #1
  val bitmap: BitmapDrawable by bindDrawableResource(R.drawable.drawable_bitmap)
  
  // Drawable resource binding #2
  val vector: VectorDrawable by bindDrawableResource(R.drawable.drawable_vector)

  // Dimension resource binding
  val dimension: Int by bindDimensionPixelSizeResource(R.dimen.dimen_resource)

  // String resource binding
  val string: String by bindStringResource(R.string.string_resource)
}
```
These methods can be used with `Activity`, `Context`, `Fragment`, support library `Fragment`, `View`, and `ViewHolder` subclasses. You can also implement `ResourcesAware` interface to provide a custom resources source. Full list of supported bindings:
- `bindBooleanResource`
- `bingColorResource`
- `bindColorStateListResource`
- `bindDimensionResource`
- `bindDimensionPixelOffsetResource`
- `bindDimensionPixelSizeResource`
- `bindDrawableResource`
- `bindIntArrayResource`
- `bindIntegerResource`
- `bindStringResource`
- `bindStringArrayResource`
- `bindTextResource`
- `bindTextArrayResource`

Gradle dependency:
```gradle
compile "com.github.vmironov.jetpack:jetpack-bindings-resources:0.12.0"
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
