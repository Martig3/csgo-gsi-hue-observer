package com.martige.gsi.service

import com.martige.gsi.model.GameStateModel

interface HueLightService {
    fun updateLighting(gameState: GameStateModel)
}
