package org.proninyaroslav.opencomicvine.ui.components

import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import org.proninyaroslav.opencomicvine.ui.LocalActivity

const val tag = "DateRangePickerDialog"

@Composable
fun DateRangePickerDialog(
    show: Boolean,
    @StringRes titleText: Int,
    initRange: Pair<Long, Long>? = null,
    onHide: () -> Unit,
    onPositiveClicked: ((Pair<Long, Long>) -> Unit)? = null,
    onNegativeClicked: (() -> Unit)? = null,
) {
    val activity = LocalActivity.current
    val onPositiveButtonClickListener by rememberUpdatedState(
        object : MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> {
            override fun onPositiveButtonClick(selection: Pair<Long, Long>?) {
                selection?.run { onPositiveClicked?.invoke(selection) }
                onHide()
            }
        }
    )
    val onNegativeButtonClickListener by rememberUpdatedState(
        object : View.OnClickListener {
            override fun onClick(v: View?) {
                onNegativeClicked?.invoke()
                onHide()
            }
        }
    )
    val onDismissListener by rememberUpdatedState(
        object : DialogInterface.OnDismissListener {
            override fun onDismiss(dialog: DialogInterface?) {
                onHide()
            }
        }
    )
    val onCancelListener by rememberUpdatedState(
        object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                onHide()
            }
        }
    )


    DisposableEffect(LocalLifecycleOwner.current) {
        activity.supportFragmentManager.findFragmentByTag(tag)?.let {
            @Suppress("UNCHECKED_CAST")
            (it as MaterialDatePicker<Pair<Long, Long>>).run {
                addOnPositiveButtonClickListener(onPositiveButtonClickListener)
                addOnNegativeButtonClickListener(onNegativeButtonClickListener)
                addOnDismissListener(onDismissListener)
                addOnCancelListener(onCancelListener)
            }
        }
        onDispose {
            activity.supportFragmentManager.findFragmentByTag(tag)?.let {
                @Suppress("UNCHECKED_CAST")
                (it as MaterialDatePicker<Pair<Long, Long>>).run {
                    removeOnPositiveButtonClickListener(onPositiveButtonClickListener)
                    removeOnNegativeButtonClickListener(onNegativeButtonClickListener)
                    removeOnDismissListener(onDismissListener)
                    removeOnCancelListener(onCancelListener)
                }
            }
        }
    }

    LaunchedEffect(show, initRange) {
        if (show) {
            activity.supportFragmentManager.run {
                findFragmentByTag(tag)?.let { fragment ->
                    beginTransaction().run {
                        remove(fragment)
                        commitAllowingStateLoss()
                    }
                }
            }
            val picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(titleText)
                .setSelection(initRange)
                .build()
                .apply {
                    addOnPositiveButtonClickListener(onPositiveButtonClickListener)
                    addOnNegativeButtonClickListener(onNegativeButtonClickListener)
                    addOnDismissListener(onDismissListener)
                    addOnCancelListener(onCancelListener)
                }
            picker.show(activity.supportFragmentManager, tag)
        }
    }
}