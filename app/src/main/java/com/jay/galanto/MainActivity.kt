package com.jay.galanto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jay.galanto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        loadFragment(DashboardFragment(),"Dashboard")
        _binding.bottomNavigationView.selectedItemId = R.id.fgDashboard
        bottomAppBarListener()

    }
    private fun loadFragment(fragment: Fragment, tag:String) {
        val currentFragment = supportFragmentManager.findFragmentByTag(tag)
        if (currentFragment == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
        }
    }

    private fun bottomAppBarListener() {
        _binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.fgHome -> {
                    loadFragment(HomeFragment(), "Home")
                    true
                }

                R.id.fgFiles -> {
                    loadFragment(FilesFragment(), "Files")
                    true
                }

                R.id.fgGames -> {
                    loadFragment(GamesFragment(), "Games")
                    true
                }

                R.id.fgDashboard ->{
                    loadFragment(fragment = DashboardFragment(), tag = "Dashboard")
                    true
                }

                R.id.fgSettings ->{
                    loadFragment(fragment = SettingsFragment(), tag = "Settings")
                    true
                }
                else -> false
            }
        }
    }
}