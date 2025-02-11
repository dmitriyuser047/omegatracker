package com.example.omegatracker.ui.history

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.omegatracker.OmegaTrackerApplication
import com.example.omegatracker.R
import com.example.omegatracker.databinding.HistoryFragmentBinding
import com.example.omegatracker.ui.Screens
import com.example.omegatracker.ui.base.activity.BaseActivity.Companion.startScreen
import com.example.omegatracker.ui.base.fragment.BaseFragment
import kotlinx.coroutines.launch
import java.util.Date

class HistoryFragment : BaseFragment(), HistoryFragmentView, HistoryFragmentListener {

    private lateinit var adapter: HistoryFragmentAdapter
    private lateinit var binding: HistoryFragmentBinding

    override val presenter: HistoryFragmentPresenter by providePresenter {
        HistoryFragmentPresenter(OmegaTrackerApplication.appComponent.repository())
    }

    override val layoutRes: Int = R.layout.history_fragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistoryFragmentBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadInitialData()
        clickBackButton()
        clickClearButton()
    }

    private fun setupRecyclerView() {
        adapter = HistoryFragmentAdapter(this)
        binding.historyRecyclerView.adapter = adapter
        loadStateListener()
    }

    private fun loadStateListener() {
        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading ||
                loadState.append is LoadState.Loading || loadState.prepend is LoadState.Loading)
                showProgressBar(true)
            else {
                showProgressBar(false)
                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(requireContext(), it.error.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showProgressBar(display : Boolean)
    {
        if(!display)
            binding.progressBar.visibility = View.GONE
        else
            binding.progressBar.visibility = View.VISIBLE
    }

    private fun loadInitialData() {
        lifecycleScope.launch {
            presenter.loadItems().collect { historyItems ->
                adapter.submitData(lifecycle, historyItems)
                showProgressBar(true)
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

    private fun clickClearButton() {
        binding.clearButton.setOnClickListener {
            showClearConfirmationDialog()
        }
    }

    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle(context?.getString(R.string.confirm))
            .setMessage(context?.getString(R.string.are_you_sure_you_want_to_cleanse))
            .setPositiveButton(context?.getString(R.string.yes)) { dialog, _ ->
                presenter.clearData()
                dialog.dismiss()
            }
            .setNegativeButton(context?.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun refreshHistory() {
        loadInitialData()
    }

    override fun navigateScreen(screens: Screens) {
        activity?.let { startScreen(it, screens) }
    }

    override fun clickStickyHeader(position: Int) {
        binding.historyRecyclerView.smoothScrollToPosition(position)
    }

    override fun getDayOfTheWeek(date: Date): String {
        return presenter.getDayOfTheWeek(date)
    }

    override fun onRetryClicked() {
        loadInitialData()
    }

    override fun getStartOfDay(date: Date): Long {
        return presenter.getStartOfDayInMillis(date)
    }
}