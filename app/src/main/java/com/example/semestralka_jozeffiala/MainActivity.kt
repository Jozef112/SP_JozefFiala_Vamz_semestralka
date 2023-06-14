package com.example.semestralka_jozeffiala

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.semestralka_jozeffiala.databinding.ActivityMainBinding

/**
 * Trieda je aktivitou aplikácie ktorá je zodpovedná za navigáciu medzi fragmentami pomocou lišti.Tuto aktivitu som vytvoril ako defaultnu naviagationDrawerActivity a poupravil si ju podľa seba
 */
class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Vytvorenie inštancie triedy ActivityMainBinding na základe layoutu
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // nastavenie ako content layout bol ako z binding objektu
        setSupportActionBar(binding.appBarMain.toolbar) // nastavenie toolbaru ako ActionBar
        val drawerLayout: DrawerLayout = binding.drawerLayout

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main) // získanie referencie na navigačný kontrolér

        //Nastavenie čo bude obsahovať drawer - v tomto prípade bude obsahovať todolist a nav_calendar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calendar,R.id.nav_todolist
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration) // Nastavenie aby ActionBar podporoval navigaciu
        navView.setupWithNavController(navController) //  // Nastavenie navigačného kontroléra pre NavigationView
        navView.setNavigationItemSelectedListener(this) // listener ktorý zistí ktorý item bol vybraný
        binding.navView.setCheckedItem(R.id.nav_calendar) // slúži na zvýraznenie na ktorom predmete sa nachádza používatel
    }

    /**
     * Metóda ktorá slúži keď sa vyberie daný item z menu tak sa presunie na daný fragment
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //keď klikne na nav_calendar tak sa presunie na Calendar_fragment
            R.id.nav_calendar -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_to_calendarFragment)
            }
            //keď klikne na todolist tak sa presunie na todolist_fragment
            R.id.nav_todolist -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.action_to_todolist)
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START) // zatvorenie šuflíka "drawer"
        binding.navView.setCheckedItem(item.itemId) // slúži na zvýraznenie na ktorom predmete sa nachádza používatel
        return true
    }

    /**
     * Metóda ktorá služi na otvorenie lišti"drawer" keď sa klikne na šipku
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}