package no.politiet.hanne.bildr

import android.graphics.Bitmap
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import junit.framework.Assert.assertTrue
import kotlinx.android.synthetic.main.activity_kamera.*
import no.politiet.hanne.bildr.activity.KameraActivity
import no.politiet.hanne.bildr.cache.BildeCache
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class KameraActivityTest {
    @Test
    fun itShouldDisplayTaBilde() {
        val activity = Robolectric.setupActivity(KameraActivity::class.java)
        val fab = activity.findViewById(R.id.btn_tabilde) as FloatingActionButton
        Assert.assertEquals(fab.visibility,RelativeLayout.VISIBLE)
    }

    @Test
    fun itShouldNotDisplayVisBilder() {
        val activity = Robolectric.setupActivity(KameraActivity::class.java)
        val visbilde = activity.findViewById(R.id.layout_bildeteller) as View
        Assert.assertEquals(visbilde.visibility,RelativeLayout.GONE)
    }
    @Test
    fun itShouldViseBilderKnappHvisBildeErTatt() {
        val bilde = mock(Bitmap::class.java)
        val activity = Robolectric.buildActivity(KameraActivity::class.java).create().resume().start().get()
        val visbilde = activity.findViewById(R.id.layout_bildeteller) as View

        Assert.assertEquals(visbilde.visibility,RelativeLayout.GONE)
        assertTrue(BildeCache.teller == 0)

        BildeCache.addBitmapToMemoryCache("test",bilde)
        activity.tellerOppdatert()
        Assert.assertEquals(visbilde.visibility,RelativeLayout.VISIBLE)
        assertTrue(BildeCache.teller > 0)
    }
}