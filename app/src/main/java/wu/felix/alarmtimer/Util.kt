package wu.felix.alarmtimer

import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText

fun isCustomTimeValid(): Boolean {

    val mins = input % 100

    if (input in 60..99) {
        return true
    }

    if (mins >= 60 || input == 0) {
        return false
    }

    return true
}

fun isAbsTimeValid(): Boolean {

    val mins = input % 100

    if (mins >= 60) {
        return false
    }

    return true

}

fun format(min: Int): String {
    return if (min < 10) {
        "0$min"
    } else {
        min.toString()
    }
}

fun popup(
        title: String,
        message: String = "",
        cancelable: Boolean = false,
        button: String = "OK",
        callback: () -> (Unit) = {},
        icon: Int? = null,
        inputCallback: ((EditText) -> Unit)? = null,
        label: String = "",
        contexts: Contexts
) {
    val dialog = AlertDialog.Builder(
            contexts.main,
            android.R.style.Theme_Material_Light_Dialog_Alert
    )
    dialog.setTitle(title)
            .setCancelable(cancelable)
            .setNegativeButton("Cancel") { _, _ ->
                input = 0
                button(-1, contexts)
            }
            .setPositiveButton(button) { _, _ -> callback() }
    if (inputCallback != null) {

        // since the inputCallback is set, show an input and pass the data to the callback
        val inputBox = EditText(contexts.main)
        // set the text type and box hint
        inputBox.inputType = InputType.TYPE_CLASS_TEXT
        inputBox.setText(label)
        inputBox.setSelectAllOnFocus(true)

        // add the inputbox and callback to the dialog
        dialog.setView(inputBox)
        dialog.setPositiveButton(button) { _, _ -> inputCallback(inputBox) }

        // show the keyboard so the user can start typing immediately
//            showKeyboard()
    } else {

        // otherwise just use the regular callback
        dialog.setPositiveButton(button) { _, _ -> callback() }
    }
    if (message != "") {
        dialog.setMessage(message)
    }
    if (icon != null) {
        dialog.setIcon(icon)
    }
    dialog.show()
}