package com.martige.gsi.model

data class HueLightParamModel(
        val on: Boolean,
        val sat: Int,
        val bri: Int,
        val hue: Int,
        val transitiontime: Int
)
