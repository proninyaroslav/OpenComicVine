package org.proninyaroslav.opencomicvine.model

import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImageStoreTest {
    private lateinit var imageStore: ImageStore

    private val context: Context = ApplicationProvider.getApplicationContext()

    private val contentResolver = context.contentResolver!!

    @Before
    fun setUp() {
        imageStore = ImageStoreImpl(context)
    }

    @Test
    fun save() {
        val bytes = byteArrayOf(1, 2, 3)
        val res = imageStore.save(bytes.inputStream(), "test.bmp")
        assertTrue(res is ImageStore.Result.Success)

        val uri = (res as ImageStore.Result.Success).uri
        try {
            contentResolver.openInputStream(uri).use {
                assertArrayEquals(bytes, it?.readBytes())
            }
        } finally {
            contentResolver.delete(uri, Bundle())
        }
    }
}