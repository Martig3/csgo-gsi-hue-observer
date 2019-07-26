package com.martige.gsi.service

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.martige.gsi.model.GameStateModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@Configuration
class HueLightServiceImpl : HueLightService {

    @Value("lightservice.url")
    val hueLightUrl = ""
    @Value("lightservice.user")
    val hueUser = ""

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

        setLights(roundPhase, winTeam, phaseEndsIn)

    }

    private fun setLights(roundPhase: String, winTeam: String, phaseEndsIn: Double) {
        when (RoundPhase.valueOf(roundPhase.toUpperCase())) {
            RoundPhase.WARMUP -> setBlueLights(1)
            RoundPhase.FREEZETIME -> setGreenLights(1)
            RoundPhase.LIVE -> setBlueLights(1)
            RoundPhase.BOMB -> setBombLights(phaseEndsIn)
            RoundPhase.DEFUSE -> setDefuseLights(phaseEndsIn)
            RoundPhase.OVER -> setOverLights(WinTeam.valueOf(winTeam.toUpperCase()))
            RoundPhase.NONE -> setBlueLights(1)
        }
    }

    private fun setBombLights(phaseEndsIn: Double) {
        when {
            phaseEndsIn > 1.0 -> setRedLights(1)
            else -> setWhiteLights(0)
        }
    }

    private fun sendRequest(body: String) {
        val restTemplate = RestTemplate()
        restTemplate.put(this.hueLightUrl + "/api/" + this.hueUser + "/lights/1/state", body)
    }

    private fun setDefuseLights(phaseEndsIn: Double) = if (phaseEndsIn > 9.0) {
        setBlueLights(100)
    } else {
        setBlueLights(50)
    }

    private fun setWhiteLights(transitionTime: Int) {
        val request = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 0,\n" +
                "    \"bri\": 254,\n" +
                "    \"hue\": 10000,\n" +
                "    \"transitiontime\": " + transitionTime + "\n" +
                "}"
        sendRequest(request)
    }

    private fun setOverLights(winTeam: WinTeam) {
        val request: String
        when (winTeam) {
            WinTeam.CT -> request = "{\n" +
                    "\t\"on\": true,\n" +
                    "    \"sat\": 150,\n" +
                    "    \"bri\": 252,\n" +
                    "    \"hue\": 45500,\n" +
                    "    \"transitiontime\": 1\n" +
                    "}"
            else -> request = "{\n" +
                    "\t\"on\": true,\n" +
                    "    \"sat\": 100,\n" +
                    "    \"bri\": 252,\n" +
                    "    \"hue\": 5000,\n" +
                    "    \"transitiontime\": 1\n" +
                    "}"
        }
        sendRequest(request)
    }

    private fun setBombExplodeLights(transitionTime: Int) {
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 252,\n" +
                "    \"bri\": 252,\n" +
                "    \"hue\": 5000,\n" +
                "    \"transitiontime\": " + transitionTime + "\n" +
                "}"
        sendRequest(request)
    }

    private fun setRedLights(transitionTime: Int) {
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 252,\n" +
                "    \"bri\": 252,\n" +
                "    \"hue\": 0,\n" +
                "    \"transitiontime\": " + transitionTime + "\n" +
                "}"
        sendRequest(request)
    }

    private fun setBlueLights(transitionTime: Int) {
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 252,\n" +
                "    \"bri\": 252,\n" +
                "    \"hue\": 45000,\n" +
                "    \"transitiontime\": " + transitionTime + "\n" +
                "}"
        sendRequest(request)
    }

    private fun setGreenLights(transitionTime: Int) {
        val request: String = "{\n" +
                "\t\"on\": true,\n" +
                "    \"sat\": 252,\n" +
                "    \"bri\": 252,\n" +
                "    \"hue\": 19000,\n" +
                "    \"transitiontime\": " + transitionTime + "\n" +
                "}"
        sendRequest(request)
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
