package com.example.omegatracker.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.HistoryFragmentBinding
import com.example.omegatracker.entity.HistoryItem
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity.Companion.startScreen
import com.example.omegatracker.ui.base.fragment.BaseFragment
import com.omega_r.libs.omegarecyclerview.OmegaRecyclerView

class HistoryFragment : BaseFragment(), HistoryFragmentView, HistoryFragmentListener {

    private lateinit var adapter: HistoryFragmentAdapter
    private lateinit var binding: HistoryFragmentBinding
    private lateinit var historyRv: OmegaRecyclerView

    override val presenter: HistoryFragmentPresenter by providePresenter {
        HistoryFragmentPresenter(OmegaTrackerApplication.appComponent.repository())
    }

    override val layoutRes: Int = R.layout.history_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutRes, container, false)
        binding = HistoryFragmentBinding.bind(view)
        historyRv = binding.historyRecyclerView
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backButtonClick()
    }

    private fun backButtonClick() {
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun navigateScreen(screens: Screens) {
        activity?.let { startScreen(it, screens) }
    }

    override fun getHistoryItems(historyTasks: List<HistoryItem>) {
        adapter = HistoryFragmentAdapter(historyTasks, this)
        historyRv.adapter = adapter
    }

    override fun clickStickyHeader(position: Int) {
        historyRv.scrollToPosition(position)
    }
}