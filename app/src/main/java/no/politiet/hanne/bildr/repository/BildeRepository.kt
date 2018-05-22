package no.politiet.hanne.bildr.repository

import android.graphics.Bitmap
import no.politiet.hanne.bildr.cache.BildeCache

class BildeRepository (private val bildeCache: BildeCache) {
    fun tellBilder() : Int {
        return bildeCache.teller
    }

    fun leggTilBilde(key: String, bilde: Bitmap) {
        bildeCache.addBitmapToMemoryCache(key,bilde)
    }

    fun tomBilder() {
        bildeCache.tomCache()
    }

    fun hentAlleBildeNokler(): ArrayList<String> {
        return bildeCache.getAllKeysFromCache()
    }

    fun hentBilde(key: String): Bitmap? {
        return bildeCache.getBitmapFromMemCache(key)
    }

    fun hentSisteBilde(): Bitmap {
        return bildeCache.hentSisteBilde()
    }
}