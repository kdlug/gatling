import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MyFirstTest extends Simulation {

  // 1 Common HTTP Configuration
  val httpConf = http
      .baseUrl("http://localhost:8080/app/")
      .header("Accept", "application/json")

  // 2 Scenario Definition
  val scn = scenario("My First test")
    .exec(http("Get all Games")
    .get("videogames"))

  // 3 Load Scenario - How many users to run, How long to run for?
  setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpConf)
}
