package no.politiet.hanne.bildr.repository

import android.graphics.Bitmap
import no.politiet.hanne.bildr.cache.BildeCache

class BildeRepository {
    init {
        BildeCache.setupCache()
    }
    fun tellBilder() : Int {
        return BildeCache.teller
    }

    fun leggTilBilde(key: String, bilde: Bitmap) {
        BildeCache.addBitmapToMemoryCache(key,bilde)
    }

    fun tomBilder() {
        BildeCache.tomCache()
    }

    fun hentAlleBildeNokler(): ArrayList<String> {
        return BildeCache.getAllKeysFromCache()
    }

    fun hentBilde(key: String): Bitmap? {
        return BildeCache.getBitmapFromMemCache(key)
    }
}