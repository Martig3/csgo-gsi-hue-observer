package com.martige.gsi.controller

import com.martige.gsi.model.GameStateModel
import com.martige.gsi.repository.GameStateRepository
import com.martige.gsi.service.HueLightServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/gsi")
class GsiController {

    lateinit var gameStateRepo: GameStateRepository
    lateinit var lightService: HueLightServiceImpl

    @Autowired
    fun setGameStateRepository(gameStateRepo: GameStateRepository) {
        this.gameStateRepo = gameStateRepo
    }

    @Autowired
    fun setHueLightService(hueLightService: HueLightServiceImpl) {
        this.lightService = hueLightService
    }

    @PostMapping("/endpoint", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.TEXT_HTML_VALUE])
    fun gsiEndpoint(@RequestBody gameState: String): String {
        this.gameStateRepo.gameState.state = gameState
        this.lightService.updateLighting(this.gameStateRepo.gameState)
//        val responseHeaders = HttpHeaders()
//        responseHeaders.set("Content-Type", "text/html")
//        return ResponseEntity("", responseHeaders, HttpStatus.OK)
        return ""
    }

    @GetMapping("/scoreboard", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getScoreboard(): GameStateModel? {
        val completedFuture = this.gameStateRepo.asyncGameStateLookup()
        CompletableFuture.allOf(completedFuture).join()
        return completedFuture.get()
    }

}
