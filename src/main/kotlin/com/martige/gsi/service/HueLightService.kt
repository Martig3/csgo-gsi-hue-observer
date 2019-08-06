package com.martige.gsi.service

import com.martige.gsi.model.GameStateModel

interface HueLightService {
    suspend fun updateLighting(gameState: GameStateModel)
}
