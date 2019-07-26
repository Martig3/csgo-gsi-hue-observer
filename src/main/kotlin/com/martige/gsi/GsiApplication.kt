package com.martige.gsi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GsiApplication

fun main(args: Array<String>) {
	runApplication<GsiApplication>(*args)
}
