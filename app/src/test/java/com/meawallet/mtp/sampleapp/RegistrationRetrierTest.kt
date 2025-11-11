package com.meawallet.mtp.sampleapp

import android.content.Context
import android.util.Log
import com.meawallet.mtp.MeaError
import com.meawallet.mtp.MeaErrorCode
import com.meawallet.mtp.MeaListener
import com.meawallet.mtp.sampleapp.platform.RegistrationRetrier
import com.meawallet.mtp.sampleapp.platform.TokenPlatform
import com.meawallet.mtp.sampleapp.utils.Cancellable
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.whenever

import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

import java.util.concurrent.atomic.AtomicInteger

class RegistrationRetrierTest {
    private lateinit var logMock: MockedStatic<Log>
    private val context: Context = mock<Context>()

    @Before
    fun setUpLog() {
        logMock = Mockito.mockStatic(Log::class.java)
    }

    /** Helper to fabricate MeaError with desired code. */
    private fun error(errorCode: Int): MeaError {
        val e: MeaError = mock()
        whenever(e.code).thenReturn(errorCode)
        whenever(e.message).thenReturn(MeaErrorCode.getDescription(errorCode))
        return e
    }

    /** Stub register() to emit a sequence: MeaError = fail, anything else = success. */
    private fun stubRegisterSequence(platform: TokenPlatform, vararg outcomes: Any) {
        val idx = AtomicInteger(0)
        doAnswer { inv ->
            val listener = inv.getArgument<MeaListener>(2)
            when (val out = outcomes[idx.getAndIncrement()]) {
                is MeaError -> listener.onFailure(out)
                else -> listener.onSuccess()
            }
            null
        }.whenever(platform).register(any<String>(), any<String>(), any<MeaListener>())
    }

    /** Stub delete() with either success or failure. */
    private fun stubDelete(platform: TokenPlatform, outcome: Any) {
        doAnswer { inv ->
            val listener = inv.getArgument<MeaListener>(0)
            if (outcome is MeaError) listener.onFailure(outcome) else listener.onSuccess()
            null
        }.whenever(platform).delete(any<MeaListener>())
    }

    /** Stub initialize() with either success or failure. */
    private fun stubInitialize(platform: TokenPlatform, outcome: Any) {
        doAnswer { inv ->
            val listener = inv.getArgument<MeaListener>(1)
            if (outcome is MeaError) listener.onFailure(outcome) else listener.onSuccess()
            null
        }.whenever(platform).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given platform not registered when successful register call then succeeds immediately`() {
        val platform: TokenPlatform = mock()
        whenever(platform.isRegistered()).thenReturn(false)
        stubRegisterSequence(platform, Unit) // first attempt succeeds

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it }
        )

        assertEquals(1, successCalls)
        assertNull(finalError)

        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
        verify(platform, never()).delete(any<MeaListener>())
        verify(platform, never()).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given platform not registered when non-retriable register error then fails immediately`() {
        val platform: TokenPlatform = mock()
        whenever(platform.isRegistered()).thenReturn(false)

        val nonRetriable = error(MeaErrorCode.SDK_DEPRECATED)
        stubRegisterSequence(platform, nonRetriable)

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it }
        )

        assertEquals(0, successCalls)
        assertEquals(nonRetriable, finalError)

        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
        verify(platform, never()).delete(any<MeaListener>())
        verify(platform, never()).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given platform not registered when ALREADY_REGISTERED register error then cleanup and retry`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val retriable = error(MeaErrorCode.ALREADY_REGISTERED)

        // First register fails (retriable), second succeeds
        stubRegisterSequence(platform, retriable, Unit)
        stubDelete(platform, Unit)
        stubInitialize(platform, Unit)

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it },
            postInitialize = { /* no-op */ }
        )

        assertEquals(1, successCalls)
        assertNull(finalError)

        // Sequence: register (fail) -> delete -> initialize -> register (success)
        verify(platform, times(2)).register(eq("T"), eq("en"), any<MeaListener>())
        verify(platform, times(1)).delete(any<MeaListener>())
        verify(platform, times(1)).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given platform is registered when ALREADY_REGISTERED register error then fail immediately`() {
        val platform = mock<TokenPlatform>()
        // This makes the predicate false (p.isRegistered() == true)
        whenever(platform.isRegistered()).thenReturn(true)

        val retriableLooking = error(MeaErrorCode.ALREADY_REGISTERED)
        stubRegisterSequence(platform, retriableLooking)

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it }
        )

        assertEquals(0, successCalls)
        assertEquals(retriableLooking, finalError)
        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
        verify(platform, never()).delete(any<MeaListener>())
        verify(platform, never()).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given retriable registration when delete on retry fails then fail immediately`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val firstErr = error(MeaErrorCode.ALREADY_REGISTERED)
        val deleteErr = error(MeaErrorCode.CRYPTO_ERROR)

        stubRegisterSequence(platform, firstErr) // first attempt fails (retriable)
        stubDelete(platform, deleteErr)          // cleanup delete fails
        // initialize() should never be called

        val retrier = RegistrationRetrier(platform)

        var finalError: MeaError? = null
        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { fail("should not succeed") },
            onFinalFailure = { finalError = it }
        )

        // onFinalFailure should receive the **original register error**, per implementation
        assertEquals(firstErr, finalError)

        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
        verify(platform, times(1)).delete(any<MeaListener>())
        verify(platform, never()).initialize(any<Context>(), any<MeaListener>())
    }

    @Test
    fun `given retriable registration when initialize on retry fails then fail immediately`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val firstErr = error(MeaErrorCode.ALREADY_REGISTERED)
        val initErr = error(MeaErrorCode.ROOTED_DEVICE)

        stubRegisterSequence(platform, firstErr)
        stubDelete(platform, Unit)           // delete OK
        stubInitialize(platform, initErr)    // init fails

        val retrier = RegistrationRetrier(platform)

        var finalError: MeaError? = null
        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { fail("should not succeed") },
            onFinalFailure = { finalError = it }
        )

        assertEquals(firstErr, finalError)
        verify(platform, times(1)).delete(any<MeaListener>())
        verify(platform, times(1)).initialize(any<Context>(), any<MeaListener>())
        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
    }

    @Test
    fun `given retriable registration when successful initialize and cleanup then postInitialize is invoked`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val retriable = error(MeaErrorCode.ALREADY_REGISTERED)

        // Fail first register, succeed second
        stubRegisterSequence(platform, retriable, Unit)

        // We want to ensure postInitialize is called — keep delete/init successful
        stubDelete(platform, Unit)
        stubInitialize(platform, Unit)

        var postInitCalled = false
        val retrier = RegistrationRetrier(platform)
        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { /* ignore */ },
            onFinalFailure = { fail("should not fail") },
            postInitialize = { postInitCalled = true }
        )

        assertTrue("postInitialize should be called", postInitCalled)

        // And the retry should have happened immediately afterwards:
        verify(platform, times(2)).register(eq("T"), eq("en"), any<MeaListener>())
    }

    @Test
    fun `given retriable registration when retry cancelled then next retry not invoked`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val firstErr = error(MeaErrorCode.ALREADY_REGISTERED)

        // First register fails (retriable). Second WOULD succeed if we let it retry.
        stubRegisterSequence(platform, firstErr, Unit)

        // Capture listeners without invoking them yet
        val deleteCaptor = argumentCaptor<MeaListener>()
        doAnswer { /* don't call back yet */ null }
            .whenever(platform).delete(deleteCaptor.capture())

        val initCaptor = argumentCaptor<MeaListener>()
        doAnswer { /* don't call back yet */ null }
            .whenever(platform).initialize(any(), initCaptor.capture())

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        val handle: Cancellable = retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it }
        )

        // delete() must have been invoked; now we have a captured value
        verify(platform, times(1)).delete(any())
        assertNotNull(deleteCaptor.firstValue)

        // Cancel BEFORE finishing cleanup
        handle.cancel()

        // Finish cleanup: delete -> initialize -> (would schedule retry, but cancel stops it)
        deleteCaptor.firstValue.onSuccess()

        verify(platform, times(1)).initialize(any(), any())
        assertNotNull(initCaptor.firstValue)
        initCaptor.firstValue.onSuccess()

        // Because we canceled, no retry ran
        assertEquals(0, successCalls)
        assertNull(finalError)
        verify(platform, times(1)).register(eq("T"), eq("en"), any())
    }

    @Test
    fun `given retriable registration when attempt count exceeded then fails permanently`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val firstErr = error(MeaErrorCode.ALREADY_REGISTERED)

        // Four register calls fail (retriable).
        stubRegisterSequence(platform, firstErr, firstErr, firstErr, firstErr, Unit)
        stubDelete(platform,  Unit)
        stubInitialize(platform, Unit)

        val retrier = RegistrationRetrier(platform)

        var successCalls = 0
        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 3,
            onSuccess = { successCalls++ },
            onFinalFailure = { finalError = it }
        )

        verify(platform, times(2)).delete(any())
        verify(platform, times(2)).initialize(any(), any())

        assertEquals(0, successCalls)
        assertEquals(firstErr, finalError)
        verify(platform, times(3)).register(eq("T"), eq("en"), any())
    }

    @Test
    fun `given retriable registration when postInitialize fails then fail immediately`() {
        val platform = mock<TokenPlatform>()
        whenever(platform.isRegistered()).thenReturn(false)

        val firstErr = error(MeaErrorCode.ALREADY_REGISTERED)
        stubRegisterSequence(platform, firstErr)

        stubDelete(platform, Unit)
        stubInitialize(platform, Unit)

        val retrier = RegistrationRetrier(platform)

        var finalError: MeaError? = null

        retrier.register(
            context = context,
            token = "T",
            language = "en",
            attemptCount = 2,
            onSuccess = { fail("should not succeed") },
            onFinalFailure = { finalError = it },
            postInitialize = { throw RuntimeException("boom") }
        )

        // Should abort retry and propagate the ORIGINAL register error
        assertEquals(firstErr, finalError)
        // Only the first register call was made
        verify(platform, times(1)).register(eq("T"), eq("en"), any<MeaListener>())
    }

    @After
    fun tearDownLog() {
        logMock.close()
    }
}