package wu.felix.alarmtimer

import android.content.Intent
import android.graphics.Typeface
import android.provider.AlarmClock
import android.widget.Toast

fun toast(text: String, contexts: Contexts) {
    val toast = Toast.makeText(contexts.applicationContext, text, Toast.LENGTH_SHORT)
    toast.show()
}

fun button(btn: Int, contexts: Contexts) {
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
    setButtonTexts(contexts)
}

fun setButtonTexts(contexts: Contexts) {

    setDisplay(input, contexts)

    var mins = input % 100
    var hrs = input / 100

    if (input in 60..99) {
        hrs += 1
        mins -= 60
    }

    if (isTimer) {
        contexts.alarmMode.setTextColor(light)
        contexts.alarmMode.typeface = Typeface.DEFAULT
        contexts.timerMode.setTextColor(dark)
        contexts.timerMode.typeface = Typeface.DEFAULT_BOLD
    } else {
        contexts.timerMode.setTextColor(light)
        contexts.timerMode.typeface = Typeface.DEFAULT
        contexts.alarmMode.setTextColor(dark)
        contexts.alarmMode.typeface = Typeface.DEFAULT_BOLD
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

fun setDisplay(value: Int, contexts: Contexts) {
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
    contexts.display.text = displayText
    contexts.displayEnd.text = endText
}

fun switchMode(contexts: Contexts) {
    isTimer = !isTimer
    setButtonTexts(contexts)
}

fun switchToTimer(contexts: Contexts) {
    isTimer = true
    setButtonTexts(contexts)
}

fun switchToAlarm(contexts: Contexts) {
    isTimer = false
    setButtonTexts(contexts)
}

fun switchAmPm(contexts: Contexts) {
    if (input < 1200) {
        input += 1200
    } else  {
        input -= 1200
    }
    setButtonTexts(contexts)
}

fun confirmation(time: String, contexts: Contexts) {
    popup(
            "",
            "Alarm successfully set for $time",
            callback = {
                val showAlarms = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                contexts.main.startActivity(showAlarms)
            },
            button = "View Alarms",
            cancelable = true,
            contexts = contexts
    )
}