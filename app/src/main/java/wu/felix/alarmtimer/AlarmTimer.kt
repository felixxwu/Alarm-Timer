package wu.felix.alarmtimer

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.AlarmClock
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alarm_timer.*
import java.util.*

var input = 0
var isTimer = true
var light: ColorStateList? = null
var dark: ColorStateList? = null

class AlarmTimer : AppCompatActivity() {

    private fun setAlarmIn(minutes: Int, contexts: Contexts) {
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

        setAlarm(label, targetHour, targetMinute, contexts)
    }

    // set the actual alarm
    private fun setAlarm(label: String, hour: Int, minute: Int, contexts: Contexts) {
        popup(
                "Alarm for: $hour:${format(minute)}",
                "Alarm label:",
                inputCallback = { inputBox ->
                    input = 0
                    button(-1, contexts)
//                    setSwitchState(absoluteSwitch, false)

                    // create the alarm using intent
                    val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                    intent.putExtra(AlarmClock.EXTRA_MESSAGE, inputBox.text.toString())
                    intent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                    intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                    startActivity(intent)

                    // see all alarms
                    val time = "$hour:${format(minute)}"
                    confirmation(time, contexts)

                },
                button = "Set Alarm",
                label = label,
                cancelable = false,
                contexts = contexts
        )

//        showKeyboard()


        // show all the alarms
//        intent = Intent (AlarmClock.ACTION_SHOW_ALARMS)
//        startActivity(intent)
    }

    // for the custom timer button
    private fun setCustomAlarm(contexts: Contexts) {
        // check for valid input
        if (input == 0) {
            toast("Please input a time", contexts)
            return
        }

        // decode the input
        val hours = Integer.parseInt(input.toString()) / 100
        val minutes = Integer.parseInt(input.toString()) % 100

        if (minutes >= 60) {
            toast("Time invalid, minutes exceed 59", contexts)
            return
        }
        if (hours >= 24) {
            toast("Alarms can't be set for more than 24h from now", contexts)
            return
        }

        setAlarmIn(hours * 60 + minutes, contexts)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_timer)

        val contexts = Contexts(this, applicationContext, alarmMode, timerMode, display, displayEnd)

        light = contexts.alarmMode.textColors
        dark = contexts.timerMode.textColors

        b0.setOnClickListener { button(0, contexts) }
        b1.setOnClickListener { button(1, contexts) }
        b2.setOnClickListener { button(2, contexts) }
        b3.setOnClickListener { button(3, contexts) }
        b4.setOnClickListener { button(4, contexts) }
        b5.setOnClickListener { button(5, contexts) }
        b6.setOnClickListener { button(6, contexts) }
        b7.setOnClickListener { button(7, contexts) }
        b8.setOnClickListener { button(8, contexts) }
        b9.setOnClickListener { button(9, contexts) }
        delete.setOnClickListener { button(-1, contexts) }

        displayEnd.setOnClickListener { switchAmPm(contexts) }
        display.setOnClickListener { switchMode(contexts) }

        alarmMode.setOnClickListener { switchToAlarm(contexts) }
        timerMode.setOnClickListener { switchToTimer(contexts) }

        set.setOnClickListener {
            if (isTimer) {
                if (isCustomTimeValid()) {
                    setCustomAlarm(contexts)
                } else {
                    toast("Invalid timer", contexts)
                }
            } else {
                if (isAbsTimeValid()) {
                    setAlarm("", input / 100, input % 100, contexts)
                } else {
                    toast("Invalid alarm time, minutes exceed 59", contexts)
                }
            }
        }

        setButtonTexts(contexts)

    }


//    private fun showKeyboard() {
//        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
//    }

}