package com.reno.philipshue

import com.reno.philipshue.bridge.Bridge
import io.mockk.MockKAnnotations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.After
import org.junit.Before
import org.junit.Test

class BridgeManagerTest {
//    @MockK
//    private lateinit var bridgeService: BridgeService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `브릿지_연결에_성공한_경우_브릿지_정보를_리턴한다`() {
        //given
        val mockBridge =
            Bridge(
                "001788fffe100491",
                "192.168.2.23",
                "00:17:88:10:04:9",
                "Philips Hue"
            )

        GlobalScope.async {
//            bridgeService.useUPnP()
        }
        //retrofit mock

        //when
//        val bridge = bridgeManager.connectBridge()

        //then
    }
}