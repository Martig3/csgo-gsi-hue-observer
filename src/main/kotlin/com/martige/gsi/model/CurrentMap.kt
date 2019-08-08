package com.martige.gsi.model

data class CurrentMap(
    val current_spectators: Int?,
    val mode: String?,
    val name: String?,
    val num_matches_to_win_series: Int?,
    val phase: String?,
    val round: Int?,
    val souvenirs_total: Int?,
    val team_ct: TeamCt?,
    val team_t: TeamT?
)
