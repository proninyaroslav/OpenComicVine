package org.proninyaroslav.opencomicvine.ui.image_viewer

import android.graphics.Bitmap
import android.net.Uri
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.proninyaroslav.opencomicvine.data.ErrorReportInfo
import org.proninyaroslav.opencomicvine.model.ErrorReportService
import org.proninyaroslav.opencomicvine.model.ImageStore
import org.proninyaroslav.opencomicvine.ui.getCompressFormatByImageType
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ImageSaverViewModelTest {
    lateinit var viewModel: ImageSaverViewModel

    val dispatcher = StandardTestDispatcher()

    @MockK
    lateinit var bitmap: Bitmap

    @MockK
    lateinit var store: ImageStore

    @MockK
    lateinit var localUri: Uri

    @MockK
    lateinit var url: Uri

    @MockK
    lateinit var errorReportService: ErrorReportService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = ImageSaverViewModel(store, errorReportService, dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun save() = runTest {
        val name = "image.jpg"
        val urlStr = "https://example.org/$name"
        val compressFormat = Bitmap.CompressFormat.JPEG
        val mimeType = "image/jpg"

        val expectedStates = listOf(
            ImageSaverState.Initial,
            ImageSaverState.Saving,
            ImageSaverState.SaveSuccess(
                uri = localUri,
                mimeType = mimeType,
            ),
        )
        val expectedEffects = listOf(
            ImageSaverEffect.SaveSuccess,
        )

        val actualStates = mutableListOf<ImageSaverState>()
        val actualEffects = mutableListOf<ImageSaverEffect>()

        every { url.getCompressFormatByImageType() } returns compressFormat
        every { bitmap.compress(compressFormat, 80, any()) } returns true
        every { url.toString() } returns urlStr
        every {
            store.save(
                imageStream = any(),
                name = name,
            )
        } returns ImageStore.Result.Success(localUri)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(ImageSaverEvent.Save(bitmap = bitmap, url = url))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun `Save for sharing`() = runTest {
        val name = "image.jpg"
        val urlStr = "https://example.org/$name"
        val compressFormat = Bitmap.CompressFormat.JPEG
        val mimeType = "image/jpg"

        val expectedStates = listOf(
            ImageSaverState.Initial,
            ImageSaverState.Saving,
            ImageSaverState.SaveSuccess(
                uri = localUri,
                mimeType = mimeType,
            ),
        )
        val expectedEffects = listOf(
            ImageSaverEffect.ReadyToShare(
                uri = localUri,
                mimeType = mimeType,
            ),
        )

        val actualStates = mutableListOf<ImageSaverState>()
        val actualEffects = mutableListOf<ImageSaverEffect>()

        every { url.getCompressFormatByImageType() } returns compressFormat
        every { bitmap.compress(compressFormat, 80, any()) } returns true
        every { url.toString() } returns urlStr
        every {
            store.save(
                imageStream = any(),
                name = name,
            )
        } returns ImageStore.Result.Success(localUri)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(ImageSaverEvent.SaveForSharing(bitmap = bitmap, url = url))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun `Save failed`() = runTest {
        val name = "image.jpg"
        val urlStr = "https://example.org/$name"
        val compressFormat = Bitmap.CompressFormat.JPEG

        val expectedStates = listOf(
            ImageSaverState.Initial,
            ImageSaverState.Saving,
            ImageSaverState.SaveFailed.StoreError(
                ImageStore.Result.Failed(null),
            )
        )
        val expectedEffects = listOf(
            ImageSaverEffect.SaveFailed.StoreError(
                ImageStore.Result.Failed(null),
            )
        )

        val actualStates = mutableListOf<ImageSaverState>()
        val actualEffects = mutableListOf<ImageSaverEffect>()

        every { url.getCompressFormatByImageType() } returns compressFormat
        every { bitmap.compress(compressFormat, 80, any()) } returns true
        every { url.toString() } returns urlStr
        every {
            store.save(
                imageStream = any(),
                name = name,
            )
        } returns ImageStore.Result.Failed(null)

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(ImageSaverEvent.Save(bitmap = bitmap, url = url))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun `Unsupported format`() = runTest {
        val name = "image"
        val urlStr = "https://example.org/$name"

        val expectedStates = listOf(
            ImageSaverState.Initial,
            ImageSaverState.Saving,
            ImageSaverState.SaveFailed.UnsupportedFormat,
        )
        val expectedEffects = listOf(
            ImageSaverEffect.SaveFailed.UnsupportedFormat,
        )

        val actualStates = mutableListOf<ImageSaverState>()
        val actualEffects = mutableListOf<ImageSaverEffect>()

        every { url.getCompressFormatByImageType() } returns null
        every { url.toString() } returns urlStr

        val stateJob = launch(UnconfinedTestDispatcher()) {
            viewModel.state.toList(actualStates)
        }
        val effectJob = launch {
            viewModel.effect.toList(actualEffects)
        }
        dispatcher.scheduler.apply {
            viewModel.event(ImageSaverEvent.Save(bitmap = bitmap, url = url))
            runCurrent()
        }

        assertEquals(expectedStates, actualStates)
        assertEquals(expectedEffects, actualEffects)
        stateJob.cancel()
        effectJob.cancel()
    }

    @Test
    fun `Error report`() {
        val info = ErrorReportInfo(
            error = IOException(),
            comment = "comment",
        )

        every { errorReportService.report(info) } just runs

        viewModel.event(ImageSaverEvent.ErrorReport(info))
        dispatcher.scheduler.runCurrent()

        verify { errorReportService.report(info) }
        confirmVerified(errorReportService)
    }
}