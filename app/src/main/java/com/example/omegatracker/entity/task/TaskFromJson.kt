package com.example.omegatracker.entity.task

import com.example.omegatracker.entity.State
import com.example.omegatracker.entity.Value
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration


data class TaskFromJson (
    val id: String,
    val summary: String,
    val description: String?,
    val project:  ProjectTask,
    val customFields: List<CustomFields>
): Task {
    override val name: String = summary
    override val projectName: String? = project.shortName
    override val state: String = customFields.map { it.value }.filterIsInstance<Value.StateValue>().firstOrNull()?.name.toString()

    override val workedTime: Duration = customFields.filter { it.id == "Затраченное время" }.map { it.value }
                .filterIsInstance<Value.PeriodValue>()
                .firstOrNull()?.minutes?.toDuration(DurationUnit.MINUTES) ?: ZERO

    override val requiredTime: Duration = customFields.filter { it.name == "Оценка" }.map { it.value }
        .filterIsInstance<Value.PeriodValue>()
        .firstOrNull()?.minutes?.toDuration(DurationUnit.MINUTES) ?: ZERO

    override var isRunning: Boolean? = null
        get() = state == State.InProgress.toString()
        set(value) {
            field = value
            if (state != "In Progress") {
                field = false
            }
        }
}