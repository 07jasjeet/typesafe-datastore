package com.jasjeet.typesafe_datastore_app

import com.jasjeet.typesafe_datastore_test.MockPrimitivePreference
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MockTest {
    @Test
    fun `test mocks`() {
        val mock = "Mock"
        val pref = MockPrimitivePreference(mock)
        val value = runBlocking { pref.get() }
        assertEquals(value, mock)
    }
}