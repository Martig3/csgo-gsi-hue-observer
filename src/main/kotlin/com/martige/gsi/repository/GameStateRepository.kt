package com.martige.gsi.repository

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.martige.gsi.model.CurrentMap
import com.martige.gsi.model.GameStateModel
import com.martige.gsi.model.Player
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.springframework.stereotype.Repository

@Repository
class GameStateRepository {

    var gameStateJson: String = gameStateJson()

    companion object {
        fun gameStateJson(): String = String()
    }

    suspend fun gameStateLookup(): GameStateModel {
        val jsonParser: JsonElement = JsonParser().parse(gameStateJson)
        val currentMap = GlobalScope.async {
            val map = jsonParser.asJsonObject.get("map") ?: JsonObject()
            Gson().fromJson(map.asJsonObject, CurrentMap::class.java)
        }
        val players = GlobalScope.async {
            val allPlayers = (jsonParser.asJsonObject.get("allplayers") ?: JsonObject())
            val players = ArrayList<Player>()
            allPlayers.asJsonObject.entrySet().mapTo(players) { Gson().fromJson(it.value, Player::class.java) }
        }
        return (GameStateModel(currentMap.await(), players.await()))
    }

}
