package wu.felix.alarmtimer

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_alarm_timer.*
import java.util.*


class AlarmTimer : AppCompatActivity() {

    private var input = 0
    private var isTimer = true
    private var light: ColorStateList? = null
    private var dark: ColorStateList? = null

    private fun toast(text: String) {
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun format(min: Int): String {
        return if (min < 10) {
            "0$min"
        } else {
            min.toString()
        }
    }

    private fun setAlarmIn(minutes: Int) {
        val rightNow = Calendar.getInstance()
        var currentHour = rightNow.get(Calendar.HOUR)
        val currentMinute = rightNow.get(Calendar.MINUTE)

        // adjust for am/pm
        if (rightNow.get(Calendar.AM_PM) == Calendar.PM) {
            currentHour += 12
        }

        // calculate the hour and minute when the alarm will be set
        // integer division is same as Math.floor
        val targetHour = (currentHour + (currentMinute + minutes) / 60) % 24
        val targetMinute = (currentMinute + minutes) % 60

        // create an automatic label showing the length of the timer
        var label = "Timer"
        if (minutes % 60 > 0) {
            label = "${minutes % 60}m " + label
        }
        if (minutes / 60 > 0) {
            label = "${minutes / 60}h " + label
        }

        setAlarm(label, targetHour, targetMinute)
    }

    // set the actual alarm
    private fun setAlarm(label: String, hour: Int, minute: Int) {
        popup(
                "Alarm for: $hour:${format(minute)}",
                "Alarm label:",
                inputCallback = { inputBox ->
                    input = 0
                    button(-1)
//                    setSwitchState(absoluteSwitch, false)

                    // create the alarm using intent
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, inputBox.text.toString())
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                    startActivity(intent)

                },
                button = "Set Alarm",
                label = label,
                cancelable = false
        )

//        showKeyboard()


        // show all the alarms
//        intent = Intent (AlarmClock.ACTION_SHOW_ALARMS)
//        startActivity(intent)
    }

    // for the custom timer button
    private fun setCustomAlarm() {
        // check for valid input
        if (input == 0) {
            toast("Please input a time")
            return
        }

        // decode the input
        val hours = Integer.parseInt(input.toString()) / 100
        val minutes = Integer.parseInt(input.toString()) % 100

        if (minutes >= 60) {
            toast("Time invalid, minutes exceed 59")
            return
        }
        if (hours >= 24) {
            toast("Alarms can't be set for more than 24h from now")
            return
        }

        setAlarmIn(hours * 60 + minutes)

    }

    private fun button(btn: Int) {
        if (btn == -1) {
            input /= 10
        } else {
            input *= 10
            input += btn
        }
        if (input >= 2400) {
            input /= 10
            return
        }
        setButtonTexts()
    }

    private fun setDisplay(value: Int) {
        val displayText: String
        val endText: String
        if (isTimer) {
            displayText = "${value / 100}h,${format(value % 100)}"
            endText = "m"
        } else {
            displayText = "${value / 100}:${format(value % 100)}"
            endText = if (input < 1200) {
                "am"
            } else {
                "pm"
            }
        }
        display.text = displayText
        display_end.text = endText
    }

    private fun isCustomTimeValid(): Boolean {

        val mins = input % 100

        if (input in 60..99) {
            return true
        }

        if (mins >= 60 || input == 0) {
            return false
        }

        return true
    }

    private fun isAbsTimeValid(): Boolean {

        val mins = input % 100

        if (mins >= 60) {
            return false
        }

        return true

    }

    private fun setButtonTexts() {

        setDisplay(input)

        var mins = input % 100
        var hrs = input / 100

        if (input in 60..99) {
            hrs += 1
            mins -= 60
        }

        if (isTimer) {
            alarmMode.setTextColor(light)
            alarmMode.typeface = Typeface.DEFAULT
            timerMode.setTextColor(dark)
            timerMode.typeface = Typeface.DEFAULT_BOLD
        } else {
            timerMode.setTextColor(light)
            timerMode.typeface = Typeface.DEFAULT
            alarmMode.setTextColor(dark)
            alarmMode.typeface = Typeface.DEFAULT_BOLD
        }



//        if (isCustomTimeValid()) {
//            val customTimeLabel = "Set alarm in ${hrs}h ${mins}m"
//            customTime.text = customTimeLabel
//        } else {
//            val customTimeLabel = "Not a valid time"
//            customTime.text = customTimeLabel
//        }
//
//        if (isAbsTimeValid()) {
//            var absTimeLabel = "Set alarm for $hrs:${format(mins)}"
//            absTimeLabel += if (input < 1200) {
//                " (am)"
//            } else {
//                " (pm)"
//            }
//            absTime.text = absTimeLabel
//        } else {
//            val absTimeLabel = "Not a valid time"
//            absTime.text = absTimeLabel
//        }



    }

    private fun switchMode() {
        isTimer = !isTimer
        setButtonTexts()
    }

    private fun switchToTimer() {
        isTimer = true
        setButtonTexts()
    }

    private fun switchToAlarm() {
        isTimer = false
        setButtonTexts()
    }

    private  fun switchAmPm() {
        if (input < 1200) {
            input += 1200
        } else  {
            input -= 1200
        }
        setButtonTexts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_timer)

        light = alarmMode.textColors
        dark = timerMode.textColors

        b0.setOnClickListener { button(0) }
        b1.setOnClickListener { button(1) }
        b2.setOnClickListener { button(2) }
        b3.setOnClickListener { button(3) }
        b4.setOnClickListener { button(4) }
        b5.setOnClickListener { button(5) }
        b6.setOnClickListener { button(6) }
        b7.setOnClickListener { button(7) }
        b8.setOnClickListener { button(8) }
        b9.setOnClickListener { button(9) }
        delete.setOnClickListener { button(-1) }

        display_end.setOnClickListener { switchAmPm() }
        display.setOnClickListener { switchMode() }

        alarmMode.setOnClickListener { switchToAlarm() }
        timerMode.setOnClickListener { switchToTimer() }

        set.setOnClickListener {
            if (isTimer) {
                if (isCustomTimeValid()) {
                    setCustomAlarm()
                } else {
                    toast("Invalid timer")
                }
            } else {
                if (isAbsTimeValid()) {
                    setAlarm("", input / 100, input % 100)
                } else {
                    toast("Invalid alarm time, minutes exceed 59")
                }
            }
        }

        setButtonTexts()

//        toAlarms.setOnClickListener {
//            intent = Intent(AlarmClock.ACTION_SET_ALARM)
//            startActivity(intent)
//        }

    }






    private fun popup(
            title: String,
            message: String = "",
            cancelable: Boolean = false,
            button: String = "OK",
            callback: () -> (Unit) = {},
            icon: Int? = null,
            inputCallback: ((EditText) -> Unit)? = null,
            label: String = ""
    ) {
        val dialog = AlertDialog.Builder(
                this,
                android.R.style.Theme_Material_Light_Dialog_Alert
        )
        dialog.setTitle(title)
                .setCancelable(cancelable)
                .setNegativeButton("Cancel") { _, _ ->
                    input = 0
                    button(-1)
                }
                .setPositiveButton(button) { _, _ -> callback() }
        if (inputCallback != null) {

            // since the inputCallback is set, show an input and pass the data to the callback
            val inputBox = EditText(this)
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

//    private fun showKeyboard() {
//        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//    }

}