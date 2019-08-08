package com.martige.gsi.repository

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.martige.gsi.model.CurrentMap
import com.martige.gsi.model.GameStateModel
import com.martige.gsi.model.Player
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import java.util.concurrent.CompletableFuture

@Repository
class GameStateRepository {

    var gameStateJson: String = gameStateJson()

    companion object {
        fun gameStateJson(): String = String()
    }

    @Async("asyncExecutor")
    fun asyncGameStateLookup(): CompletableFuture<GameStateModel> {
        val jsonParser: JsonElement = JsonParser().parse(gameStateJson)
        val map: CurrentMap = Gson().fromJson(jsonParser.asJsonObject.getAsJsonObject("map"), CurrentMap::class.java)
        val allPlayers  = (jsonParser.asJsonObject.get("allplayers") ?: JsonObject())
        val players: ArrayList<Player> = ArrayList()
        allPlayers.asJsonObject.entrySet().mapTo(players) { Gson().fromJson(it.value, Player::class.java) }

        return CompletableFuture.completedFuture(GameStateModel(map, players))
    }
}
