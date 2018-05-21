package no.politiet.hanne.bildr.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import no.politiet.hanne.bildr.R
import no.politiet.hanne.bildr.dependencyinjection.repository


class BildePagerAdapter(mContext: Context, private val mResources: List<String>) : PagerAdapter() {
    private val mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return mResources.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.bilde_liste, container, false)

        val imageView = itemView.findViewById(R.id.bilde) as ImageView
        imageView.setImageBitmap(repository().bildeRepository.hentBilde(mResources[position]))

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}