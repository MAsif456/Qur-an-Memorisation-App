package com.example.mob_dev_portfolio.ui.common

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Common fragment helpers for collecting flows lifecycle-aware. */
abstract class BaseFragment : Fragment() {

    /** Collects [flow] only while the view is at least STARTED. */
    protected fun <T> collectViewLifecycle(
        flow: Flow<T>,
        block: suspend (T) -> Unit,
    ) {
        viewLifecycleOwner.run {
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flow.collectLatest { block(it) }
                }
            }
        }
    }

    protected fun <T> LifecycleOwner.collectFlow(
        flow: Flow<T>,
        block: suspend (T) -> Unit,
    ) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest { block(it) }
            }
        }
    }
}
