package wu.felix.alarmtimer

import android.content.Context
import android.widget.TextView

class Contexts(
        val main: Context,
        val applicationContext: Context,
        val alarmMode: TextView,
        val timerMode: TextView,
        val display: TextView,
        val displayEnd: TextView
)