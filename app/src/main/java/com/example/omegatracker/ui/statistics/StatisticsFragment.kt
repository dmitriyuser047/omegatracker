package com.example.omegatracker.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.StatisticsFragmentBinding
import com.example.omegatracker.db.entity.HistoryData
import com.example.omegatracker.entity.Periods
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.fragment.BaseFragment
import com.example.omegatracker.ui.statistics.graph.StatisticsGraphHelper

class StatisticsFragment: BaseFragment(), StatisticsFragmentView {

    private lateinit var binding: StatisticsFragmentBinding
    private lateinit var statisticsGraph: StatisticsGraph
    private lateinit var statisticsGraphHelper: StatisticsGraphHelper

    override val presenter: StatisticsFragmentPresenter by providePresenter {
        StatisticsFragmentPresenter(OmegaTrackerApplication.appComponent.repository())
    }

    override val layoutRes: Int = R.layout.statistics_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutRes, container, false)
        binding = StatisticsFragmentBinding.bind(view)
        statisticsGraphHelper = OmegaTrackerApplication.appComponent.statisticsGraphHelper()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickBackButton()
        clickPeriodButtons()
        clickedPeriodsButton(Periods.DAY)
        statisticsGraph = binding.statisticGraph
    }

    override fun getStatistics(tasksCount: Int, time: Pair<String, String>) {
        binding.hours.text = time.first
        binding.minutes.text = time.second
        binding.tasksCount.text = tasksCount.toString()
    }

    override fun getStatisticsForGraph(historyData: List<HistoryData>, periods: Periods) {
        statisticsGraphHelper.setHistoryData(historyData)
        statisticsGraph.setYLabels(statisticsGraphHelper.getYLabels())
        statisticsGraph.dayPoints = statisticsGraphHelper.getTimeDays()
        statisticsGraph.hoursPoints = statisticsGraphHelper.getHoursDay()
        when (periods) {
            Periods.DAY -> {
                statisticsGraph.isWeek = false
                statisticsGraph.setXLabels(context?.resources?.getStringArray(R.array.day_labels)?.toList() ?: emptyList() )
            }
            Periods.WEEK -> {
                statisticsGraph.isWeek = true
                statisticsGraph.setXLabels(context?.resources?.getStringArray(R.array.week_labels)?.toList() ?: emptyList())
            }
        }
    }

    private fun clickBackButton() {
        binding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
    }

    private fun clickPeriodButtons() {
        binding.day.setOnClickListener {
           clickedPeriodsButton(Periods.DAY)
        }
        binding.week.setOnClickListener {
            clickedPeriodsButton(Periods.WEEK)
        }
    }

    private fun clickedPeriodsButton(periods: Periods) {
        when (periods) {
            Periods.DAY -> {
                selectButton(binding.day, binding.week)
                presenter.setData(Periods.DAY)
            }
            Periods.WEEK -> {
                selectButton(binding.week, binding.day)
                presenter.setData(Periods.WEEK)
            }
        }
    }

    private fun selectButton(selected: View, unselected: View) {
        selected.setBackgroundColor(resources.getColor(R.color.white))
        unselected.setBackgroundColor(resources.getColor(R.color.transparent))
    }

    override fun navigateScreen(screens: Screens) {

    }
}