package de.pdmitriev.test.staffbase.staffbase

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages= arrayOf("de.pdmitriev.test.staffbase"))
class TestApplication

fun main(args: Array<String>) {
	runApplication<TestApplication>(*args)
}
