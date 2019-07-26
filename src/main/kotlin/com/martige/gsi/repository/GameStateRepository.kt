package com.martige.gsi.repository

import com.martige.gsi.model.GameStateModel
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@Repository
class GameStateRepository {

    var gameState = gameState()

    companion object {
        fun gameState(): GameStateModel = GameStateModel()
    }

    @Async("asyncExecutor")
    fun asyncGameStateLookup(): CompletableFuture<GameStateModel> {
        return CompletableFuture.completedFuture(this.gameState)
    }
}
