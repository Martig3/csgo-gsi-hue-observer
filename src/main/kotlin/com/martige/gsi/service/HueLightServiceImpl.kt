package com.martige.gsi.service

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.martige.gsi.controller.LightJob
import com.martige.gsi.model.HueLightParamModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@Configuration
@PropertySource("classpath:gsi.properties")
class HueLightServiceImpl : HueLightService {

    @Value("\${lightservice.hueControllerUrl}")
    lateinit var hueControllerUrl: String
    @Value("\${lightservice.hueUser}")
    lateinit var hueUser: String
    @Value("\${lightservice.hueLightUrl}")
    lateinit var hueLightUrl: String

    var lastRoundPhase = lastRoundPhase()

    companion object {
        fun lastRoundPhase(): String = String()
    }

    override suspend fun updateLighting(gameState: String) {
        this.lastRoundPhase = "NONE"
        var roundPhase = "NONE"
        var winTeam = "NONE"
        var phaseEndsIn = 0.0
        val jsonParser: JsonElement = JsonParser().parse(gameState)
        if (jsonParser.asJsonObject.has("phase_countdowns")) {
            roundPhase = jsonParser.asJsonObject.get("phase_countdowns").asJsonObject.get("phase").asString
            phaseEndsIn = jsonParser.asJsonObject.get("phase_countdowns").asJsonObject.get("phase_ends_in").asDouble
        }
        if (jsonParser.asJsonObject.has("round") && jsonParser.asJsonObject.getAsJsonObject("round").has("win_team"))
            winTeam = jsonParser.asJsonObject.get("round").asJsonObject.get("win_team").asString

        setRoundPhase(roundPhase, winTeam, phaseEndsIn)

    }

    private suspend fun setRoundPhase(roundPhase: String, winTeam: String, phaseEndsIn: Double) {
        if (LightJob.currentLightJob != null && (RoundPhase.valueOf(roundPhase.toUpperCase()) != RoundPhase.BOMB))
            LightJob.currentLightJob!!.cancel()

        when (RoundPhase.valueOf(roundPhase.toUpperCase())) {
            RoundPhase.WARMUP -> setBlueLights(1)
            RoundPhase.FREEZETIME -> setGreenLights(1)
            RoundPhase.LIVE -> setBlueLights(1)
            RoundPhase.DEFUSE -> setDefuseLights(phaseEndsIn)
            RoundPhase.BOMB -> setBombLights(phaseEndsIn)
            RoundPhase.OVER -> setOverLights(WinTeam.valueOf(winTeam.toUpperCase()))
            RoundPhase.NONE -> setBlueLights(1)
        }
        this.lastRoundPhase = roundPhase
    }

    private fun getLightPutRequest(on: Boolean, saturation: Int, brightness: Int, hue: Int, transitionTime: Int): String {
        return GsonBuilder().create().toJson(HueLightParamModel(on, saturation, brightness, hue, transitionTime))
    }

    private fun sendRequest(body: String) {
        val restTemplate = RestTemplate()
        restTemplate.put("${this.hueControllerUrl}/api/${this.hueUser}/${this.hueLightUrl}", body)
    }

    private suspend fun setBombLights(phaseEndsIn: Double) {
        this.lastRoundPhase = RoundPhase.BOMB.name.toLowerCase()
        when {
            LightJob.currentLightJob == null -> startBombLights()
            phaseEndsIn > 1.0 && (!LightJob.currentLightJob!!.isActive) -> startBombLights()
            phaseEndsIn <= 1.0 && phaseEndsIn > 0.0 -> {
                LightJob.currentLightJob!!.cancel()
                setWhiteLights(0)
            }
            phaseEndsIn <= 0.0 -> {
                LightJob.currentLightJob!!.cancel()
                setExplodeLights(0)
            }
        }
    }

    private fun startBombLights() = runBlocking {
        val lightJob: Job = launch {
            while (true) {
                setLightOff(10)
                delay(1000)
                setRedLights(10)
                delay(1000)
            }
        }
        LightJob.currentLightJob = lightJob
    }

    private suspend fun setDefuseLights(phaseEndsIn: Double) {
        if (this.lastRoundPhase.toUpperCase() == RoundPhase.DEFUSE.name) {
            when {
                phaseEndsIn < 1.0 -> {
                    setWhiteLights(0)
                    return
                }
                phaseEndsIn < 0.0 -> {
                    setExplodeLights(0)
                    setRedLights(10)
                }
                else -> return
            }
        }

        if (RoundPhase.valueOf(this.lastRoundPhase.toUpperCase()) != RoundPhase.DEFUSE) {
            setBlueLights((phaseEndsIn * 10.0).toInt())
        }
    }

    private fun setWhiteLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 0, 254, 10000, transitionTime))
    }

    private fun setOverLights(winTeam: WinTeam) {
        val request: String = when (winTeam) {
            WinTeam.CT -> getLightPutRequest(true, 180, 254, 45000, 1)
            else -> getLightPutRequest(true, 180, 254, 5000, 1)
        }
        sendRequest(request)
    }

    private fun setLightOff(transitionTime: Int) {
        sendRequest(getLightPutRequest(false, 254, 254, 5000, transitionTime))
    }

    private fun setRedLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 254, 254, 0, transitionTime))
    }

    private fun setBlueLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 254, 254, 45000, transitionTime))
    }

    private fun setGreenLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 254, 254, 19000, transitionTime))
    }

    private fun setExplodeLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 254, 254, 5000, transitionTime))
    }

}

enum class WinTeam {
    CT,
    T,
    NONE
}

enum class RoundPhase {
    WARMUP,
    LIVE,
    FREEZETIME,
    BOMB,
    OVER,
    NONE,
    DEFUSE,
}
