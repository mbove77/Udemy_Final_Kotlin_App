package com.bove.martin.udemyfinalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.bove.martin.udemyfinalapp.activities.LoginActivity
import com.bove.martin.udemyfinalapp.adapters.PagerAdapter
import com.bove.martin.udemyfinalapp.fragments.ChatFragment
import com.bove.martin.udemyfinalapp.fragments.InfoFragment
import com.bove.martin.udemyfinalapp.fragments.RateFragment
import com.bove.martin.udemyfinalapp.utils.goToActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    // Initialize Firebase Auth
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Guardamos que MenuIten esta seleccionado
    private var prevBottomSelected: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar as Toolbar)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpViewPager(getPagerAdapter())
        setUpBottonNavBar()
     }

    // El orden es importante
    private fun getPagerAdapter(): PagerAdapter {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(RateFragment())
        adapter.addFragment(ChatFragment())
        return adapter
    }

    // Seteamos el ViewPager
    private fun setUpViewPager(adapter: PagerAdapter) {
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            // Manejamos evento cuando se cambia de pagina
            override fun onPageSelected(position: Int) {
                if (prevBottomSelected == null) {
                    // seleccionamos el primer item si es la primera vez que se carga
                    bottonNavigation.menu.getItem(0).isChecked = false
                } else {
                    // Si no, des seleccionamos el item que teníamos guardado
                    prevBottomSelected!!.isChecked = false
                }
                // Seleccionamos el item escogido
                bottonNavigation.menu.getItem(position).isChecked = true
                prevBottomSelected = bottonNavigation.menu.getItem(position)
            }
        })
    }

    // Manejamos los eventos del Botton Nav para cambiar al fragment selecionado
    private fun setUpBottonNavBar() {
       bottonNavigation.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
           when(menuItem.itemId) {
                R.id.bottom_nav_info -> {
                    viewPager.currentItem = 0; true
                }
                R.id.bottom_nav_rates -> {
                    viewPager.currentItem = 1; true
                }
                R.id.bottom_nav_chat -> {
                    viewPager.currentItem = 2; true
                }
               else -> false
           }
       }
    }

    // Creamos Option Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Manejamos eventos del Option Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                goToActivity<LoginActivity>{
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
