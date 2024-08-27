package com.example.omegatracker.ui.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.example.omegatracker.R
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TasksTracking : DialogFragment() {

    private lateinit var selectedHour: String
    private lateinit var selectedMinutes: String
    private lateinit var startTask: AppCompatButton
    private var listener: TasksTrackingListener? = null
    private var timeLimit: Duration = Duration.ZERO

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(activity, R.style.DialogStyle)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.time_tracking, null)

        dialogBuilder.setView(dialogView)
        selectedHour = "0"
        selectedMinutes = "0"
        startTask = dialogView.findViewById(R.id.startTask)

        startTask.setOnClickListener {
            selectedHour = dialogView.findViewById<EditText>(R.id.hours).text.toString()
            selectedMinutes = dialogView.findViewById<EditText>(R.id.minutes).text.toString()
            if (selectedHour.isEmpty()) selectedHour = "0"
            if (selectedMinutes.isEmpty()) selectedMinutes = "0"
            timeLimit = calculateTimeLimit(selectedHour, selectedMinutes)
            dismiss()
        }

        return dialogBuilder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TasksTrackingListener) {
            listener = context
        } else {
            throw RuntimeException("$context must be TasksTrackingListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDialogDismiss(timeLimit)
    }

    private fun calculateTimeLimit(selectedHour: String, selectedMinutes: String): Duration {
        val hours = selectedHour.toIntOrNull() ?: 0
        val minutes = selectedMinutes.toIntOrNull() ?: 0

        val totalSeconds = (hours * 60 * 60) + (minutes * 60)

        return totalSeconds.toDuration(DurationUnit.SECONDS)
    }
}