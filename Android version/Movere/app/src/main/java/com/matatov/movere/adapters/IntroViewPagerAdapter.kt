package com.matatov.movere.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.matatov.movere.R
import com.matatov.movere.models.ScreenItemModel

class IntroViewPagerAdapter(var context: Context, var listScreen: MutableList<ScreenItemModel>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutScreen: View = inflater.inflate(R.layout.activity_screen, null)

        val introImg = layoutScreen.findViewById<ImageView>(R.id.introImg)
        val introTitle = layoutScreen.findViewById<TextView>(R.id.introTitle)
        val introDescription = layoutScreen.findViewById<TextView>(R.id.introDescription)

        introImg.setImageResource(listScreen.get(position).screenImg)
        introTitle.text = listScreen.get(position).screenTitle.toString()
        introDescription.text = listScreen.get(position).screenDescription.toString()

        container.addView(layoutScreen)
        return layoutScreen
    }

    override fun getCount(): Int {
        return listScreen.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
   //     super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }
}
