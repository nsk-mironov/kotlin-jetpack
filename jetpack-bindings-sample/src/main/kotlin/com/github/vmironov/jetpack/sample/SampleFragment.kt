package com.github.vmironov.jetpack.sample

import android.app.Fragment
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import com.github.vmironov.jetpack.preferences.bindOptionalPreference
import com.github.vmironov.jetpack.resources.bindResource

import kotlinx.android.synthetic.main.fragment_sample.*

class SampleFragment : Fragment() {
  private var firstNameValue by bindOptionalPreference<String>()
  private var lastNameValue by bindOptionalPreference<String>()

  private val firstNameLabel by bindResource<String>(R.string.first_name_label)
  private val firstNameHint by bindResource<String>(R.string.first_name_hint)

  private val lastNameLabel by bindResource<String>(R.string.last_name_label)
  private val lastNameHint by bindResource<String>(R.string.last_name_hint)

  private val paddingTiny by bindResource<Int>(R.dimen.padding_tiny)
  private val paddingSmall by bindResource<Int>(R.dimen.padding_small)
  private val paddingLarge by bindResource<Int>(R.dimen.padding_large)

  private val colorPrimary by bindResource<Int>(R.color.primary)
  private val colorBackground by bindResource<Int>(R.color.color_background)
  private val colorText by bindResource<Int>(R.color.color_text)
  private val colorHint by bindResource<Int>(R.color.color_hint)

  private val fontSmall by bindResource<Float>(R.dimen.font_small)
  private val fontNormal by bindResource<Float>(R.dimen.font_normal)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.fragment_sample, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.background = ColorDrawable(colorBackground)
    view.setPadding(paddingLarge, paddingLarge, paddingLarge, paddingLarge)

    first_name_label.text = firstNameLabel
    first_name_label.setPadding(paddingSmall, paddingTiny, paddingSmall, paddingTiny)
    first_name_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSmall)
    first_name_label.setTextColor(colorPrimary)

    last_name_label.text = lastNameLabel
    last_name_label.setPadding(paddingSmall, paddingTiny, paddingSmall, paddingTiny)
    last_name_label.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSmall)
    last_name_label.setTextColor(colorPrimary)

    first_name_input.hint = firstNameHint
    first_name_input.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontNormal)
    first_name_input.setHintTextColor(colorHint)
    first_name_input.setText(firstNameValue.orEmpty())
    first_name_input.setTextColor(colorText)

    last_name_input.hint = lastNameHint
    last_name_input.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontNormal)
    last_name_input.setTextColor(colorText)
    last_name_input.setText(lastNameValue.orEmpty())
    last_name_input.setHintTextColor(colorHint)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_sample, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_done -> {
        firstNameValue = first_name_input.text?.toString()
        lastNameValue = last_name_input.text?.toString()
      }
    }

    return super.onOptionsItemSelected(item)
  }
}
