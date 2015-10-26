package com.github.vmironov.jetpack.sample

import android.app.Activity
import android.os.Bundle

public class SampleActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sample)
  }
}
