package com.example.omegatracker.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.example.omegatracker.R
import com.example.omegatracker.entity.task.State
import com.example.omegatracker.entity.task.TaskRun
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AddCustomTask : DialogFragment() {

    private var listener: AddCustomTaskListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(activity, R.style.DialogStyle)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.add_task_layout, null)

        dialogBuilder.setView(dialogView)

        val createTask = dialogView.findViewById<AppCompatButton>(R.id.createTask)

        createTask.setOnClickListener {
            create(dialogView)
        }

        return dialogBuilder.create()
    }

    private fun create(dialogView: View) {
        val name = dialogView.findViewById<EditText>(R.id.editText2).text.toString()
        val description = dialogView.findViewById<EditText>(R.id.description_edit).text.toString()

        val hours = dialogView.findViewById<EditText>(R.id.hours).text.toString().toIntOrNull() ?: 0
        val minutes =
            dialogView.findViewById<EditText>(R.id.minutes).text.toString().toIntOrNull() ?: 0
        val days = dialogView.findViewById<EditText>(R.id.days).text.toString().toIntOrNull() ?: 0

        val hoursInMinutes = hours * 60
        val daysInMinutes = days * 24 * 60


        val totalTimeInMinutes = hoursInMinutes + minutes + daysInMinutes

        val task = TaskRun(
            id = UUID.randomUUID().toString(),
            startTime = Duration.ZERO,
            name = name,
            description = description,
            projectName = getString(R.string.no_project),
            state = State.Open.toString(),
            workedTime = Duration.ZERO,
            requiredTime = totalTimeInMinutes.toDuration(DurationUnit.MINUTES),
            isRunning = false,
            spentTime = Duration.ZERO,
            fullTime = totalTimeInMinutes.toDuration(DurationUnit.MINUTES),
            dataCreate = System.currentTimeMillis(),
            imageUrl = R.drawable.icon_monitor_circle.toString()
        )

        listener?.onTaskAdded(task)

        dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddCustomTaskListener) {
            listener = context
        } else {
            throw RuntimeException("$context must be AddTasksDialogListener")
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onDialogDismiss()
    }
}