package computerdatabase

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulationHAR extends Simulation {

	val httpProtocol = http
		.baseUrl("https://computer-database.gatling.io")
		.inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.woff2""", """.*\.(t|o)tf""", """.*\.png""", """.*detectportal\.firefox\.com.*"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en-US;q=0.9,en;q=0.8")
		.doNotTrackHeader("1")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36")

	val headers_0 = Map(
		"Cache-Control" -> "max-age=0",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"sec-ch-ua" -> """Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-gpc" -> "1")

	val headers_1 = Map(
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"sec-ch-ua" -> """Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-gpc" -> "1")

	val headers_7 = Map(
		"Cache-Control" -> "max-age=0",
		"Origin" -> "https://computer-database.gatling.io",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "same-origin",
		"Sec-Fetch-User" -> "?1",
		"sec-ch-ua" -> """Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-gpc" -> "1")

	object Search {
		val searchFeeder = csv("data/search.csv").random
		var search = exec(http("Load_HomePage")
			.get("/computers"))
			.pause(2)
			.feed(searchFeeder)
			.exec(http("Search_Computer_${searchCriterion}")
				.get("/computers?f=${searchCriterion}")
				.check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
		.pause(2)
				.exec(http("Select_Computer_${searchComputerName}")
					.get("${computerURL}"))
					.pause(2)
	}

	object Browse {
		var brows = {
			repeat(5, "i") {
				exec(http("Browse_Page_${i}")
					.get("/computers?p=${i}"))
					.pause(2)
			}
		}
	}

  object Create {
		val computerFeeder = csv("data/computers.csv").circular
		var create = exec(http("Load_Create_Computer_Page")
			.get("/computers/new"))
			.pause(2)
			.feed(computerFeeder)
			.exec(http("Create_Computer_${computerName}")
				.post("/computers")
				.formParam("name", "${computerName}")
				.formParam("introduced", "${introduced}")
				.formParam("discontinued", "${discontinued}")
				.formParam("company", "${companyId}")
			.check(status.is(200)))
	}

  val admins = scenario("Admins").exec(Search.search, Browse.brows, Create.create)

	val users= scenario("Users").exec(Search.search, Browse.brows)

	setUp(
		admins.inject(atOnceUsers(5)),
		users.inject(
			nothingFor(5),
			atOnceUsers(1),
			rampUsers(5) during(10),
			constantUsersPerSec(20) during(20)
		)
	).protocols(httpProtocol)
}