package com.martige.gsi

import com.martige.gsi.service.WinTeam
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner::class)
@SpringBootTest
class GsiApplicationTests {

	@Test
	fun contextLoads() {
	}

    @Test
    fun setBlueLights() {
        val restTemplate = RestTemplate()
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 254,\n" +
                "    \"bri\": 254,\n" +
                "    \"hue\": 45000,\n" +
                "    \"transitiontime\": 4\n" +
                "}\n" +
                "\t "
        restTemplate.put("http://192.168.1.2/api/BOiEsc0sFNSWan1IAalU9V0b5-08Z8kFPbjBNvbL/lights/1/state", request)
    }

    @Test
    fun setBombLights() {
        val restTemplate = RestTemplate()
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 254,\n" +
                "    \"bri\": 254,\n" +
                "    \"hue\": 0,\n" +
                "    \"transitiontime\": 4\n" +
                "}"
        restTemplate.put("http://192.168.1.2/api/BOiEsc0sFNSWan1IAalU9V0b5-08Z8kFPbjBNvbL/lights/1/state", request)
    }

    @Test
    fun setBombExplodeLights() {
        val restTemplate = RestTemplate()
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 254,\n" +
                "    \"bri\": 254,\n" +
                "    \"hue\": 5000,\n" +
                "    \"transitiontime\": 4\n" +
                "}"
        restTemplate.put("http://192.168.1.2/api/BOiEsc0sFNSWan1IAalU9V0b5-08Z8kFPbjBNvbL/lights/1/state", request)
    }

    @Test
    fun setOverLights() {
        val restTemplate = RestTemplate()
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 140,\n" +
                "    \"bri\": 254,\n" +
                "    \"hue\": 46000,\n" +
                "    \"transitiontime\": 4\n" +
                "}"
        restTemplate.put("http://192.168.1.2/api/BOiEsc0sFNSWan1IAalU9V0b5-08Z8kFPbjBNvbL/lights/1/state", request)
    }

}
