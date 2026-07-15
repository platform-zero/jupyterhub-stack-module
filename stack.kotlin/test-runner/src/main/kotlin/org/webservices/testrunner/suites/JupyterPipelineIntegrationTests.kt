package org.webservices.testrunner.suites

import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.webservices.testrunner.framework.*

suspend fun TestRunner.jupyterPipelineIntegrationTests() = suite("Jupyter Pipeline Integration Tests") {
test("Integration: JupyterHub + Data Pipeline analysis capability") {
        if (skipUnlessAnySelectedComponent("JupyterHub data analysis integration", "pipeline", "inference", "qdrant")) {
            return@test
        }
        val jupyterResponse = client.getRawResponse(endpoints.jupyterhub)
        val pipelineEndpoint = endpoints.pipeline
        require(pipelineEndpoint.isNotBlank() && pipelineEndpoint != "null") {
            "Pipeline endpoint not configured for Jupyter integration test"
        }
        val pipelineResponse = runCatching {
            client.getRawResponse("${pipelineEndpoint}/health")
        }.getOrNull()
        requireOkOrRedirectResponse(jupyterResponse, "JupyterHub integration endpoint")
        require(pipelineResponse != null) { "Pipeline health request failed" }
        require(pipelineResponse.status == HttpStatusCode.OK) {
            "Pipeline health endpoint returned ${pipelineResponse.status}"
        }
        require(pipelineResponse.bodyAsText().isNotBlank()) { "Pipeline health endpoint returned an empty payload" }
        println("      ✓ JupyterHub and Pipeline endpoints are both ready")
    }
}
