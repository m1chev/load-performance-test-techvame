package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseUrl("https://computer-database.gatling.io")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:87.0) Gecko/20100101 Firefox/87.0")

	val headers_0 = Map(
		"Accept" -> "*/*",
		"Accept-Encoding" -> "gzip, deflate",
		"Content-Type" -> "application/ocsp-request")

	val headers_2 = Map(
		"Cache-Control" -> "max-age=0",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_3 = Map(
		"Accept" -> "*/*",
		"Accept-Encoding" -> "gzip, deflate")

	val headers_4 = Map("Upgrade-Insecure-Requests" -> "1")

	val headers_8 = Map(
		"Origin" -> "https://computer-database.gatling.io",
		"Upgrade-Insecure-Requests" -> "1")

    val uri1 = "http://ciscobinary.openh264.org/openh264-macosx64-2e1774ab6dc6c43debb0b5b628bdf122a391d521.zip"
    val uri2 = "http://ocsp.usertrust.com"
    val uri4 = "http://ocsp.pki.goog/gts1o1core"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.post(uri2 + "/")
			.headers(headers_0)
			.body(RawFileBody("computerdatabase/recordedsimulation/0000_request.dat")))
		.pause(11)
		.exec(http("request_1")
			.post(uri4)
			.headers(headers_0)
			.body(RawFileBody("computerdatabase/recordedsimulation/0001_request.dat")))
		.pause(94)
		.exec(http("request_2")
			.get("/computers")
			.headers(headers_2))
		.pause(21)
		.exec(http("request_3")
			.get(uri1)
			.headers(headers_3))
		.pause(57)
		// search
		.exec(http("request_4")
			.get("/computers?f=macbook")
			.headers(headers_4))
		.pause(30)
		// select computer
		.exec(http("request_5")
			.get("/computers/6")
			.headers(headers_4))
		.pause(20)
		.exec(http("request_6")
			.get("/computers")
			.headers(headers_4))
		.pause(36)
		// create computer
		.exec(http("request_7")
			.get("/computers/new")
			.headers(headers_4))
		.pause(60)
		.exec(http("request_8")
			.post("/computers")
			.headers(headers_8)
			.formParam("name", "macbook air")
			.formParam("introduced", "2021-04-09")
			.formParam("discontinued", "2021-06-09")
			.formParam("company", "1"))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}