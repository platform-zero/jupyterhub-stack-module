package org.webservices.testrunner.suites

import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import org.webservices.testrunner.framework.*

suspend fun TestRunner.userInterfaceTests() = suite("User Interface Tests") {

    
    
    

    test("JupyterHub hub API is accessible") {
        val response = client.getRawResponse("${env.endpoints.jupyterhub}/hub/api")
        requireOkOrAuthBoundary(response, "JupyterHub hub API")
        if (response.status == HttpStatusCode.OK) {
            val json = Json.parseToJsonElement(response.bodyAsText())
            require(json is JsonObject) { "JupyterHub hub API should return a JSON object" }
        }
    }

    test("JupyterHub root endpoint responds") {
        val response = client.getRawResponse("${env.endpoints.jupyterhub}/")
        requireOkOrRedirectResponse(response, "JupyterHub root endpoint")
    }
}
