package com.martige.gsi

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource(locations = ["classpath:gsi-test.properties"])
class GsiApplicationTests {

    @Value("\${lightservice.hueControllerUrl}")
    lateinit var hueControllerUrl: String
    @Value("\${lightservice.hueUser}")
    lateinit var hueUser: String
    @Value("\${lightservice.hueLightUrl}")
    lateinit var hueLightUrl: String

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
        restTemplate.put("${this.hueControllerUrl}/api/${this.hueUser}/${this.hueLightUrl}", request)
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

        restTemplate.put("${this.hueControllerUrl}/api/${this.hueUser}/${this.hueLightUrl}", request)
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
        restTemplate.put("${this.hueControllerUrl}/api/${this.hueUser}/${this.hueLightUrl}", request)
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
        restTemplate.put("${this.hueControllerUrl}/api/${this.hueUser}/${this.hueLightUrl}", request)
    }

}
