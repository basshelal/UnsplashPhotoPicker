package com.github.basshelal.unsplashpicker

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Backend Tests")
class BackendTests {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            UnsplashPhotoPickerConfig.apply {
                accessKey = ""
                secretKey = ""
                unsplashAppName = ""
            }
        }
    }

    @DisplayName("Test")
    @Test
    fun testTest() {
        assertEquals(4, 2 + 2)
    }

}