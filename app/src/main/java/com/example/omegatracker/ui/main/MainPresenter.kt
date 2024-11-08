package com.example.omegatracker.ui.main

import com.example.omegatracker.data.RepositoryImpl
import com.example.omegatracker.entity.task.TaskRun
import com.example.omegatracker.ui.base.activity.BasePresenter
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainPresenter @Inject constructor(
    private val repositoryImpl: RepositoryImpl
) : BasePresenter<MainView>() {

    fun addNewTask(task: TaskRun) {
        launch {
            repositoryImpl.addNewDataTaskToBase(task)
        }
    }

}