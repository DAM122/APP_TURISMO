package com.example.appturismo.utils

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.appturismo.R
import com.example.appturismo.model.MonumentoResponse
import com.google.android.material.tabs.TabLayout

class InfoPagerAdapter(fragmentManager: FragmentManager, private val monumento: MonumentoResponse) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> InfoFragment.newInstance(monumento)
            1 -> FichaFragment.newInstance(monumento)
            2 -> ComentariosFragment.newInstance(monumento)
            3 -> MapFragment.newInstance(monumento)
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Info"
            1 -> "Ficha"
            2 -> "Comentarios"
            3 -> "Mapa"
            else -> null
        }
    }

    fun setTabWidth(tabLayout: TabLayout) {
        val tab = tabLayout.getTabAt(2) // Obtener la pestaña "Comentario"
        val params = tab?.view?.layoutParams as ViewGroup.MarginLayoutParams
        params.width = tabLayout.context.resources.getDimensionPixelSize(R.dimen.tab_width_comment)
        tab.view.layoutParams = params
    }
}