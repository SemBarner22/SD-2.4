import com.google.common.annotations.VisibleForTesting
import org.springframework.http.HttpStatus
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class NetworkUtils(private val url: String) {
    class HttpResponse {
        var response: String? = null
        var code = 0
    }

    @Throws(IOException::class)
    private fun readStream(reader: InputStream): String {
        val content = StringBuilder()
        BufferedReader(InputStreamReader(reader)).use { input ->
            var inputLine: String?
            while (input.readLine().also { inputLine = it } != null) {
                content.append(inputLine).append(System.lineSeparator())
            }
            return content.toString()
        }
    }

    private fun doRequest(reqMethod: String, parameters: Map<String?, String?>, method: String): HttpResponse {
        val response = HttpResponse()
        try {
            val url = URL("$url/$reqMethod")
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = method
            con.doOutput = true
            val out = DataOutputStream(con.outputStream)
            out.writeBytes(getParamsString(parameters))
            out.flush()
            out.close()
            response.code = con.responseCode
            if (response.code != HttpStatus.OK.value()) {
                response.response = readStream(con.errorStream)
            } else {
                response.response = readStream(con.inputStream)
            }
            con.disconnect()
            if (response.code != HttpStatus.OK.value()) {
                throw RuntimeException("Bad response code " + response.code + ": " + response.response)
            }
        } catch (e: IOException) {
            throw RuntimeException("Failed to perform a request: " + e.message)
        }
        return response
    }

    private fun getParamsString(params: Map<String?, String?>): String {
        val result = StringBuilder()
        for ((key, value) in params) {
            result.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
            result.append("=")
            result.append(URLEncoder.encode(value, StandardCharsets.UTF_8))
            result.append("&")
        }
        val resultString = result.toString()
        return if (resultString.isNotEmpty()) resultString.substring(0, resultString.length - 1) else resultString
    }

    private fun doGetRequest(reqMethod: String, parameters: Map<String?, String?>): HttpResponse {
        return doRequest(reqMethod, parameters, "GET")
    }

    @VisibleForTesting
    fun doPostRequest(reqMethod: String, parameters: Map<String?, String?>): HttpResponse {
        return doRequest(reqMethod, parameters, "POST")
    }

    fun modifyShare(shareName: String?, companyName: String?, amountDelta: Long, priceDelta: Double): Double {
        val resp = doGetRequest(
                "modify-share",
                java.util.Map.of(
                        "name", shareName,
                        "company", companyName,
                        "qdelta", amountDelta.toString(),
                        "pdelta", priceDelta.toString())
        )
        val split = resp.response!!.split(" ").toTypedArray()
        return split[split.size - 1].toDouble()
    }

    fun queryPrice(shareQualifiedName: String): Double {
        val resp = doGetRequest("share-info", java.util.Map.of())
        val split = resp.response!!.split(System.lineSeparator()).toTypedArray()
        for (line in split) {
            if (line.contains("'$shareQualifiedName'")) {
                val splitLine = line.split(" ").toTypedArray()
                return splitLine[splitLine.size - 1].toDouble()
            }
        }
        throw IllegalArgumentException("No share $shareQualifiedName has been found on market")
    }
}