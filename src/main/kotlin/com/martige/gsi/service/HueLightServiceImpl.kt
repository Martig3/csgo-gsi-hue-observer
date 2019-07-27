package com.martige.gsi.service

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.martige.gsi.model.GameStateModel
import com.martige.gsi.model.HueLightParamModel
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

    override fun updateLighting(gameState: GameStateModel) {
        var roundPhase = "NONE"
        var winTeam = "NONE"
        var phaseEndsIn = 0.0
        val jsonParser: JsonElement = JsonParser().parse(gameState.state)
        if (jsonParser.asJsonObject.has("phase_countdowns")) {
            roundPhase = jsonParser.asJsonObject.get("phase_countdowns").asJsonObject.get("phase").asString
            phaseEndsIn = jsonParser.asJsonObject.get("phase_countdowns").asJsonObject.get("phase_ends_in").asDouble
        }
        if (jsonParser.asJsonObject.has("round") && jsonParser.asJsonObject.getAsJsonObject("round").has("win_team"))
            winTeam = jsonParser.asJsonObject.get("round").asJsonObject.get("win_team").asString


        setRoundPhase(roundPhase, winTeam, phaseEndsIn)

    }

    private fun setRoundPhase(roundPhase: String, winTeam: String, phaseEndsIn: Double) {
        when (RoundPhase.valueOf(roundPhase.toUpperCase())) {
            RoundPhase.WARMUP -> setBlueLights(1)
            RoundPhase.FREEZETIME -> setGreenLights(1)
            RoundPhase.LIVE -> setBlueLights(1)
            RoundPhase.BOMB -> setBombLights(phaseEndsIn)
            RoundPhase.DEFUSE -> setDefuseLights(phaseEndsIn)
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

    private fun setBombLights(phaseEndsIn: Double) {
        when {
            phaseEndsIn > 1.0 -> setRedLights(1)
            else -> setWhiteLights(0)
        }
    }

    private fun setDefuseLights(phaseEndsIn: Double) {
        if (this.lastRoundPhase.toUpperCase() == RoundPhase.DEFUSE.name) {
            if (phaseEndsIn < 1.0) {
                setWhiteLights(0)
                return
            }
            return
        }

        if (phaseEndsIn > 10.0 && RoundPhase.valueOf(lastRoundPhase().toUpperCase()) != RoundPhase.DEFUSE) {
            setBlueLights(100)
        } else {
            setBlueLights(50)
        }
    }

    private fun setWhiteLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 0, 254, 10000, transitionTime))
    }

    private fun setOverLights(winTeam: WinTeam) {
        val request: String = when (winTeam) {
            WinTeam.CT -> getLightPutRequest(true, 252, 45500, 1, 1)
            else -> getLightPutRequest(true, 100, 252, 5000, 1)
        }
        sendRequest(request)
    }

    private fun setBombExplodeLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 252, 252, 5000, transitionTime))
    }

    private fun setRedLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 252, 252, 0, transitionTime))
    }

    private fun setBlueLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 252, 252, 45000, transitionTime))
    }

    private fun setGreenLights(transitionTime: Int) {
        sendRequest(getLightPutRequest(true, 252, 252, 190000, transitionTime))
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
