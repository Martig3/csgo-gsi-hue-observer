package com.martige.gsi.service

interface HueLightService {
    suspend fun updateLighting(gameState: String)
}
