package no.politiet.hanne.bildr.activity

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vis_bilder.*
import no.politiet.hanne.bildr.R
import no.politiet.hanne.bildr.adapter.BildePagerAdapter
import no.politiet.hanne.bildr.dependencyinjection.repository

class VisBilderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vis_bilder)
        fab.setOnClickListener {
            onBackPressed()
        }
        val pager = layout_bildeliste as ViewPager
        val adapter = BildePagerAdapter(this, repository().bildeRepository.hentAlleBildeNokler())
        pager.adapter = adapter
    }

    override fun onBackPressed() {
        repository().bildeRepository.tomBilder()
        super.onBackPressed()
    }
}
