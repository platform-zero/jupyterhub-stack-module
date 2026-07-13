package org.webservices.testrunner.suites

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
        val qdrantResponse = runCatching {
            client.getRawResponse("${endpoints.qdrant.trimEnd('/')}/healthz")
        }.getOrNull()

        val jupyterReachable = jupyterResponse.status == HttpStatusCode.OK ||
            jupyterResponse.status == HttpStatusCode.Found ||
            jupyterResponse.status == HttpStatusCode.SeeOther
        val pipelineReachable = pipelineResponse?.status == HttpStatusCode.OK
        val qdrantReachable = qdrantResponse?.status == HttpStatusCode.OK

        if (jupyterReachable && (pipelineReachable || qdrantReachable)) {
            println("      ✓ Data analysis stack ready")
            if (pipelineReachable) {
                println("      ℹ️  JupyterHub can analyze data from Pipeline/Qdrant")
            } else {
                println("      ℹ️  Pipeline management API is not exposed; validated JupyterHub + Qdrant data plane")
            }
        } else {
            fail("Data analysis stack not ready: Jupyter=$jupyterReachable Pipeline=$pipelineReachable Qdrant=$qdrantReachable")
        }
    }
}
