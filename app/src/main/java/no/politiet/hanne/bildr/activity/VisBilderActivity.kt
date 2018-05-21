package no.politiet.hanne.bildr.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_vis_bilder.*
import no.politiet.hanne.bildr.cache.BildeCache
import no.politiet.hanne.bildr.R
import no.politiet.hanne.bildr.adapter.BildeListeAdapter

class VisBilderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vis_bilder)
        fab.setOnClickListener {
            onBackPressed()
        }
        val listView = ListView(this)
        listView.adapter = BildeListeAdapter(this,BildeCache.getAllKeysFromCache())
        layout_bildeliste.addView(listView)
    }

    override fun onBackPressed() {
        BildeCache.tomCache()
        super.onBackPressed()
    }

}
