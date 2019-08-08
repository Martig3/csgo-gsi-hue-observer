package com.martige.gsi.model

data class Player(
    val forward: String?,
    val match_stats: MatchStats?,
    val name: String?,
    val observer_slot: Int?,
    val position: String?,
    val state: State?,
    val team: String?
)
