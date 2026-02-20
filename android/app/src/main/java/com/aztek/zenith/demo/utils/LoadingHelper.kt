package com.aztek.zenith.demo.utils

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

object LoadingHelper {
    private var dialog: AlertDialog? = null

    fun showLoading(activity: Activity, message: String) {
        activity.runOnUiThread {
            if (dialog != null && dialog?.isShowing == true) {
                // Update message if already showing
                val textView = dialog?.findViewById<TextView>(android.R.id.message)
                textView?.text = message
                return@runOnUiThread
            }

            val llPadding = 30
            val ll =
                    LinearLayout(activity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(llPadding, llPadding, llPadding, llPadding)
                        gravity = Gravity.CENTER
                        val llParam =
                                LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                        llParam.gravity = Gravity.CENTER
                        layoutParams = llParam
                    }

            val progressBar =
                    ProgressBar(activity).apply {
                        isIndeterminate = true
                        setPadding(0, 0, llPadding, 0)
                        layoutParams =
                                LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                    }

            val tvText =
                    TextView(activity).apply {
                        text = message
                        setTextColor(Color.parseColor("#000000"))
                        textSize = 20f
                        layoutParams =
                                LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                    }

            ll.addView(progressBar)
            ll.addView(tvText)

            val builder =
                    AlertDialog.Builder(activity).apply {
                        setCancelable(false)
                        setView(ll)
                    }

            dialog = builder.create()
            dialog?.show()
            val window = dialog?.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog?.window?.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = layoutParams
            }
        }
    }

    fun hideLoading(activity: Activity) {
        activity.runOnUiThread {
            dialog?.dismiss()
            dialog = null
        }
    }
}
