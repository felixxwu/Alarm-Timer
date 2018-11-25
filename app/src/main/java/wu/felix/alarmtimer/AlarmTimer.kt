package wu.felix.alarmtimer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm_timer.*
import android.provider.AlarmClock
import android.support.v7.app.AlertDialog
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import java.util.*
import android.view.inputmethod.InputMethodManager
import android.text.Editable
import android.widget.Switch

class AlarmTimer : AppCompatActivity() {

    lateinit var mainActivity: AlarmTimer

    private fun toast(text: String) {
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        toast.show()
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

        // overwrite the automatic label with the custom one if it is specified
        if ("${customLabel.text}" != "") {
            label = customLabel.text.toString()
        }

        setAlarm(label, targetHour, targetMinute)
    }

    // set the actual alarm
    private fun setAlarm(label: String, hour: Int, minute: Int) {
        popup(
                label,
                "Alarm set for: ${hour}:${minute}",
                callback = {
                    customLabel.text = null
                    setSwitchState(labelSwitch, false)
                    numberInput.text = null
//                    setSwitchState(absoluteSwitch, false)

                    // create the alarm using intent
                    val intent = Intent (AlarmClock.ACTION_SET_ALARM)
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, label)
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                    startActivity(intent)

                    numberInput.requestFocus()

                },
                button = "Set Alarm"
        )



        // show all the alarms
//        intent = Intent (AlarmClock.ACTION_SHOW_ALARMS)
//        startActivity(intent)
    }

    // for the custom timer button
    private fun setCustomAlarm(view: View, imm: InputMethodManager) {
        // check for valid input
        if ("${numberInput.text}" == "") {
            toast("Please input a time")
            return
        }

        // decode the input
        val input = numberInput.text
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

        // hide soft keyboard
        imm.hideSoftInputFromWindow(view.windowToken, 0)

//        if (absoluteSwitch.isChecked) {


//            // get current time for label
//            val rightNow = Calendar.getInstance()
//            var currentHour = rightNow.get(Calendar.HOUR)
//            val currentMinute = rightNow.get(Calendar.MINUTE)
//
//            // adjust for am/pm
//            if (rightNow.get(Calendar.AM_PM) == Calendar.PM) {
//                currentHour += 12
//            }
//
//            val label = "Alarm set at $currentHour:$currentMinute"
//            setAlarm(label, hours, minutes)


//        } else {
//            // convert hours into minutes and set a relative alarm
            setAlarmIn(hours * 60 + minutes)
//        }

    }

    private fun setSwitchState(switch: Switch, state: Boolean) {
        if (switch.isChecked != state) {
            switch.toggle()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_timer)

        mainActivity = this

        // show soft keyboard on start
        customLabel.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        // quick timers
        set5.setOnClickListener   {setAlarmIn(5)}
        set10.setOnClickListener  {setAlarmIn(10)}
        set15.setOnClickListener  {setAlarmIn(15)}
        set20.setOnClickListener  {setAlarmIn(20)}
        set30.setOnClickListener  {setAlarmIn(30)}
        set45.setOnClickListener  {setAlarmIn(45)}
        set60.setOnClickListener  {setAlarmIn(60)}
        set120.setOnClickListener {setAlarmIn(120)}
        set180.setOnClickListener {setAlarmIn(180)}

        // custom length timer
        customTime.setOnClickListener {view ->
            // view and imm are passed to hide the soft keyboard
            setCustomAlarm(view, imm)
        }

        toAlarms.setOnClickListener {
            intent = Intent (AlarmClock.ACTION_SET_ALARM)
            startActivity(intent)
        }

        // set the switch state on if there is text and off if there is none
        customLabel.addTextChangedListener ( object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if ("${customLabel.text}" == "") {
                    setSwitchState(labelSwitch, false)
                } else {
                    setSwitchState(labelSwitch, true)
                }
            }
        })

        numberInput.addTextChangedListener( object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // decode the input
                if (numberInput.text.toString() == "") {
                    customTime.text = "Set Custom Timer"
                    return
                }
                val input = Integer.parseInt(numberInput.text.toString())

                var label = "Set alarm in"
                if (input / 100 > 0) {
                    label += " ${input / 100}h"
                }
                if (input % 100 > 0) {
                    label += " ${input % 100}m"
                }
                customTime.text = label
            }
        })

        // on switch check, focus the label
        // on switch uncheck, remove label text
        labelSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked && "${customLabel.text}" != "") {
                customLabel.setText("")
            }
            if (isChecked && "${customLabel.text}" == "") {
                customLabel.requestFocus()
            }
        }

        // change the hint depending on custom timer function
//        absoluteSwitch.setOnCheckedChangeListener { _, isChecked ->
//            numberInput.setText("")
//            numberInput.requestFocus()
//
//            // get hints from resource
//            val relativeHint = resources.getString(R.string.relativeHint)
//            val absoluteHint = resources.getString(R.string.absoluteHint)
//
//            // set hints
//            if (isChecked) {
//                numberInput.hint = absoluteHint
//            } else {
//                numberInput.hint = relativeHint
//            }
//        }

    }

    private fun popup(
            title: String,
            message: String = "",
            cancelable: Boolean = false,
            button: String = "OK",
            callback: () -> (Unit) = {},
            icon: Int? = null
    ) {
        val dialog = AlertDialog.Builder(
                mainActivity,
                android.R.style.Theme_Material_Light_Dialog_Alert
        )
        dialog.setTitle(title)
                .setCancelable(cancelable)
                .setNegativeButton("Cancel") {_,_->}
                .setPositiveButton(button) {_, _-> callback()}
        if (message != "") {
            dialog.setMessage(message)
        }
        if (icon != null) {
            dialog.setIcon(icon)
        }
        dialog.show()
    }
}
