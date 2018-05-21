package no.politiet.hanne.bildr.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.app.Activity
import android.view.View
import android.widget.BaseAdapter
import android.widget.ImageView
import no.politiet.hanne.bildr.R
import no.politiet.hanne.bildr.cache.BildeCache

class BildeListeAdapter(private val context: Activity, private val imageIdList: ArrayList<String>)
    : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var viewHolder = ViewHolderItem
        var convertView = p1
        if(p1 == null) {
            val inflater = context.layoutInflater
            convertView = inflater.inflate(R.layout.bilde_liste,p2,false)
            val imageView = convertView.findViewById<ImageView>(R.id.bilde)
            viewHolder.view = imageView
            convertView.tag = viewHolder
        }
        else {
            viewHolder = convertView!!.tag as ViewHolderItem
        }

        viewHolder.view!!.setImageBitmap(BildeCache.getBitmapFromMemCache(imageIdList[p0]))
        return convertView!!
    }
    override fun getItem(p0: Int): Any {
        return imageIdList.get(p0)
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }
    override fun getCount(): Int {
        return imageIdList.size
    }
    companion object ViewHolderItem {
        @SuppressLint("StaticFieldLeak")
        var view : ImageView? = null
    }
}
