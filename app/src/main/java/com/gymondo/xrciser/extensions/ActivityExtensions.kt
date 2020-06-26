package com.gymondo.xrciser.extensions

import android.app.Activity
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun Activity.makeSnackbar(@StringRes message : Int) : Snackbar {
    return Snackbar.make(this.window.decorView.rootView, message, 2)
}

fun Activity.makeSnackbar(@StringRes message: Int, @StringRes promptText : Int,
    action: View.OnClickListener) : Snackbar {
    return this.makeSnackbar(message)
        .setAction(promptText, action)
}