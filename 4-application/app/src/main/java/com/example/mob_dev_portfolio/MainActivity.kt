package com.example.mob_dev_portfolio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mob_dev_portfolio.data.repository.SurahRepository
import com.example.mob_dev_portfolio.data.seed.SeedLoader
import com.example.mob_dev_portfolio.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single host Activity. Hosts the nav graph, wires the bottom nav, and
 * kicks off the (idempotent) seed plus a background Surah refresh on cold
 * start. Orientation is locked to portrait via the manifest.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var seedLoader: SeedLoader

    @Inject lateinit var surahRepository: SurahRepository

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNav.setupWithNavController(navHost.navController)

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching { seedLoader.seedIfRequired() }
            runCatching { surahRepository.refresh() }
        }
    }
}
