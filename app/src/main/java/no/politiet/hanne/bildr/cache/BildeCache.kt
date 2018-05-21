package no.politiet.hanne.bildr.cache

import android.graphics.Bitmap
import android.util.LruCache

object BildeCache {

    private var bildeCache: LruCache<String, Bitmap>? = null
    private var keys : ArrayList<String> = ArrayList()
    val teller : Int get() = keys.count()

    fun setupCache() {
        val maxMem : Long = Runtime.getRuntime().maxMemory()/1024
        val cacheSize = maxMem /8
        bildeCache = object : LruCache<String, Bitmap>(cacheSize.toInt()) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                return value!!.byteCount / 1024
            }
        }
    }

    fun tomCache () {
        bildeCache!!.evictAll()
        keys = ArrayList()
    }

    fun addBitmapToMemoryCache(key: String, value: Bitmap) {
        if(getBitmapFromMemCache(key) == null)
        {
            bildeCache!!.put(key,value)
            keys.add(key)
        }
    }

    fun getBitmapFromMemCache(key: String): Bitmap? {
        return bildeCache!!.get(key)
    }

    fun getAllKeysFromCache() : ArrayList<String> {
        return keys
    }
}