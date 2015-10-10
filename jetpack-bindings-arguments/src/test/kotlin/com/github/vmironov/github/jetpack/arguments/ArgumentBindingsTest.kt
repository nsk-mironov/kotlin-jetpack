package com.github.vmironov.github.jetpack.arguments

import android.app.Fragment
import android.os.Bundle
import android.test.AndroidTestCase
import com.github.vmironov.jetpack.arguments.*
import junit.framework.Assert

public class ArgumentBindingsTest : AndroidTestCase() {
  public fun testExplicitRequiredBindings() {
    class ArgumentsFragment : Fragment() {
      var integer by bindArgument(default = 12)
      var boolean by bindArgument<Boolean>()
      var string by bindArgument<String>()
      var long by bindArgument<Long>()
      var double by bindArgument<Double>()
      var float by bindArgument<Float>()
    }

    val fragment = ArgumentsFragment().apply {
      integer = 12
      boolean = true
      string = "rainbow dash"
      long = 37L
      double = 1.25
      float = 2.5f
    }

    Assert.assertEquals(12, fragment.integer)
    Assert.assertEquals(true, fragment.boolean)
    Assert.assertEquals("rainbow dash", fragment.string)
    Assert.assertEquals(37L, fragment.long)
    Assert.assertEquals(1.25, fragment.double)
    Assert.assertEquals(2.5f, fragment.float)
  }

  public fun testImplicitRequiredBindings() {
    class ArgumentsFragment : Fragment() {
      val integer by bindArgument<Int>()
      val boolean by bindArgument<Boolean>()
      val string by bindArgument<String>()
      val long by bindArgument<Long>()
      val double by bindArgument<Double>()
      val float by bindArgument<Float>()
    }

    val fragment = ArgumentsFragment().apply {
      arguments = Bundle().apply {
        putInt("integer", 33)
        putBoolean("boolean", false)
        putString("string", "pinkie pie")
        putLong("long", 14L)
        putDouble("double", 3.14)
        putFloat("float", 2.71f)
      }
    }

    Assert.assertEquals(33, fragment.integer)
    Assert.assertEquals(false, fragment.boolean)
    Assert.assertEquals("pinkie pie", fragment.string)
    Assert.assertEquals(14L, fragment.long)
    Assert.assertEquals(3.14, fragment.double)
    Assert.assertEquals(2.71f, fragment.float)
  }

  public fun testRequiredBindingsWithDefaultsWhenArgumentsMissedWithoutBundle() {
    class ArgumentsFragment : Fragment() {
      val integer by bindArgument("integer", 4)
      val boolean by bindArgument("boolean", true)
      val string by bindArgument("string", "flutter shy")
      val long by bindArgument("long", 7L)
      val double by bindArgument("double", 1.23)
      val float by bindArgument("float", 4.56f)
    }

    val fragment = ArgumentsFragment().apply {
      arguments = null
    }

    Assert.assertEquals(4, fragment.integer)
    Assert.assertEquals(true, fragment.boolean)
    Assert.assertEquals("flutter shy", fragment.string)
    Assert.assertEquals(7L, fragment.long)
    Assert.assertEquals(1.23, fragment.double)
    Assert.assertEquals(4.56f, fragment.float)
  }

  public fun testRequiredBindingsWithDefaultsWhenArgumentsMissedWithEmptyBundle() {
    class ArgumentsFragment : Fragment() {
      val integer by bindArgument("integer", 4)
      val boolean by bindArgument("boolean", true)
      val string by bindArgument("string", "flutter shy")
      val long by bindArgument("long", 7L)
      val double by bindArgument("double", 1.23)
      val float by bindArgument("float", 4.56f)
    }

    val fragment = ArgumentsFragment().apply {
      arguments = Bundle.EMPTY
    }

    Assert.assertEquals(4, fragment.integer)
    Assert.assertEquals(true, fragment.boolean)
    Assert.assertEquals("flutter shy", fragment.string)
    Assert.assertEquals(7L, fragment.long)
    Assert.assertEquals(1.23, fragment.double)
    Assert.assertEquals(4.56f, fragment.float)
  }

  public fun testRequiredBindingsWithDefaultsWhenArgumentsSet() {
    class ArgumentsFragment : Fragment() {
      val integer by bindArgument("integer", 4)
      val boolean by bindArgument("boolean", true)
      val string by bindArgument("string", "flutter shy")
      val long by bindArgument("long", 7L)
      val double by bindArgument("double", 1.23)
      val float by bindArgument("float", 4.56f)
    }

    val fragment = ArgumentsFragment().apply {
      arguments = Bundle().apply {
        putInt("integer", 33)
        putBoolean("boolean", false)
        putString("string", "pinkie pie")
        putLong("long", 14L)
        putDouble("double", 3.14)
        putFloat("float", 2.71f)
      }
    }

    Assert.assertEquals(33, fragment.integer)
    Assert.assertEquals(false, fragment.boolean)
    Assert.assertEquals("pinkie pie", fragment.string)
    Assert.assertEquals(14L, fragment.long)
    Assert.assertEquals(3.14, fragment.double)
    Assert.assertEquals(2.71f, fragment.float)
  }

  public fun testOptionalBindingsWithoutArguments() {
    class ArgumentsFragment : Fragment() {
      val integer by bindOptionalArgument<Int>("integer")
      val boolean by bindOptionalArgument<Boolean>("boolean")
      val string by bindOptionalArgument<String>("string")
      val long by bindOptionalArgument<Long>("long")
      val double by bindOptionalArgument<Double>("double")
      val float by bindOptionalArgument<Float>("float")
    }

    val fragment = ArgumentsFragment().apply {
      arguments = Bundle()
    }

    Assert.assertEquals(null, fragment.integer)
    Assert.assertEquals(null, fragment.boolean)
    Assert.assertEquals(null, fragment.string)
    Assert.assertEquals(null, fragment.long)
    Assert.assertEquals(null, fragment.double)
    Assert.assertEquals(null, fragment.float)
  }

  public fun testOptionalBindingsWithArguments() {
    class ArgumentsFragment : Fragment() {
      val integer by bindOptionalArgument<Int>("integer")
      val boolean by bindOptionalArgument<Boolean>("boolean")
      val string by bindOptionalArgument<String>("string")
      val long by bindOptionalArgument<Long>("long")
      val double by bindOptionalArgument<Double>("double")
      val float by bindOptionalArgument<Float>("float")
    }

    val fragment = ArgumentsFragment().apply {
      arguments = Bundle().apply {
        putInt("integer", 33)
        putBoolean("boolean", false)
        putString("string", "pinkie pie")
        putLong("long", 14L)
        putDouble("double", 3.14)
        putFloat("float", 2.71f)
      }
    }

    Assert.assertEquals(33, fragment.integer)
    Assert.assertEquals(false, fragment.boolean)
    Assert.assertEquals("pinkie pie", fragment.string)
    Assert.assertEquals(14L, fragment.long)
    Assert.assertEquals(3.14, fragment.double)
    Assert.assertEquals(2.71f, fragment.float)
  }
}
