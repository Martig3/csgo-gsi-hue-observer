package com.martige.gsi.controller

import kotlinx.coroutines.Job

class LightJob {
    companion object {
        var currentLightJob: Job? = null
    }
}
