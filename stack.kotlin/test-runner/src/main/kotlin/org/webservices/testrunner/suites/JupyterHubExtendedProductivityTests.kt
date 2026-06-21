package org.webservices.testrunner.suites

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import org.webservices.testrunner.framework.*

suspend fun TestRunner.jupyterHubExtendedProductivityTests() = suite("JupyterHub Extended Productivity Tests") {

fun requireStatus(response: HttpResponse, allowed: Set<HttpStatusCode>, message: String) {
        require(response.status in allowed) {
            "$message: ${response.status}"
        }
    }

test("JupyterHub: Service is accessible") {
        val response = client.getRawResponse(endpoints.jupyterhub)
        requireOkOrRedirectResponse(response, "JupyterHub service")
        println("      ✓ JupyterHub endpoint returned ${response.status}")
    }

    test("JupyterHub: Login route is protected by edge auth") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/login")
        requireOkOrAuthBoundary(response, "JupyterHub login route")

        if (response.status == HttpStatusCode.OK) {
            val body = response.bodyAsText()
            body shouldContain "JupyterHub"
            println("      ✓ JupyterHub login page loads")
        } else {
            println("      ✓ JupyterHub login route is auth protected (${response.status})")
        }
    }

    test("JupyterHub: API endpoint responds") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/api")
        val body = requireOkResponse(response, "JupyterHub API")
        val json = Json.parseToJsonElement(body)
        require(json is JsonObject) { "API should return JSON object" }
        println("      ✓ JupyterHub API accessible")
    }

    test("JupyterHub: User API endpoint enforces authentication") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/api/users")
        requireAuthBoundary(response, "JupyterHub users API")
        println("      ✓ Users API endpoint: ${response.status}")
    }

    test("JupyterHub: OAuth integration configured") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/oauth_login")
        when (response.status) {
            HttpStatusCode.NotFound -> println("      ✓ JupyterHub relies on edge auth; /hub/oauth_login is intentionally absent")
            else -> {
                requireOkOrRedirectResponse(response, "JupyterHub OAuth login endpoint")
                println("      ✓ OAuth login endpoint: ${response.status}")
            }
        }
    }

    test("JupyterHub: Static assets are served") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/static/css/style.min.css")
        requireStatus(
            response,
            setOf(HttpStatusCode.OK, HttpStatusCode.NotModified),
            "JupyterHub static assets were not served"
        )
        println("      ✓ Static assets served")
    }

    test("JupyterHub: Kernel specifications endpoint") {
        val response = client.getRawResponse("${endpoints.jupyterhub}/hub/api/kernelspecs")
        when (response.status) {
            HttpStatusCode.NotFound -> println("      ✓ Kernel specs are deferred until a single-user server is active")
            else -> {
                val body = requireOkResponse(response, "JupyterHub kernel specs endpoint")
                val json = Json.parseToJsonElement(body)
                require(json is JsonObject) { "Kernel specs endpoint should return JSON object" }
                println("      ✓ Kernel specs endpoint: ${response.status}")
            }
        }
    }
}
