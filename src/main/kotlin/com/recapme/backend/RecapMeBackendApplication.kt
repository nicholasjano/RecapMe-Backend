package com.recapme.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RecapMeBackendApplication

fun main(args: Array<String>) {
	runApplication<RecapMeBackendApplication>(*args)
}
