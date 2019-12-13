package com.reno.philipshue

import com.reno.philipshue.model.Bridge
import com.reno.philipshue.network.BridgeService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.*

import org.junit.Assert.*

class BridgeManagerTest {
    private val bridgeManager: IBridgeManager by lazy {
        BridgeManager()
    }
    @MockK
    private lateinit var bridgeService: BridgeService

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

            bridgeService.useUPnP()
        }
        //retrofit mock

        //when
        val bridge = bridgeManager.connectBridge()

        //then
        assertNotNull(bridge)
    }
}