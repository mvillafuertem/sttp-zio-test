import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
import sttp.client3._
import sttp.client3.asynchttpclient.zio._
import sttp.client3.circe._
import zio.console.{Console, putStrLn}
import zio.duration._
import zio.test.Assertion.equalTo
import zio.test._
import zio.test.environment.{TestClock, TestEnvironment}
import zio.{Chunk, RIO, Schedule}

object SttpZioTest extends DefaultRunnableSpec {

  // @see https://requestbin.com/r/ene80m1n53nb
  implicit val customConfig: Configuration = Configuration.default

  case class Response(success: Boolean)

  case class Request(success: Boolean)

  private val requestGET = basicRequest
    .get(uri"https://ene80m1n53nb.x.pipedream.net/")
    .response(asJson[Response])

  override def spec: Spec[TestEnvironment, TestFailure[Throwable], TestSuccess] =
    suite(getClass.getSimpleName)(
      testM(s"repeat policy")(
        assertM(
          for {
            fiber <- AsyncHttpClientZioBackend().flatMap(backend =>
              backend
                .send(requestGET)
                .map(_.body)
                .absolve
                .repeat(
                  (Schedule.spaced(2.second) >>> Schedule.recurWhile[Long](_ < 5)) *>
                    Schedule.collectAll[Response].tapInput[Console, Response](response => putStrLn(response.toString).exitCode)
                )
                .catchAll(_ => RIO.effect(Chunk(Response(false))))
                .fork
            )
            _ <- TestClock.adjust(20.seconds)
            actual <- fiber.join
          } yield actual
        )(equalTo(Chunk.fill(5)(Response(true))))
      )
    )
}



import zio._
import zio.duration._
import zio.test.Assertion._
import zio.test.environment._

object ExampleSpec extends DefaultRunnableSpec {

  val schedule = (Schedule.spaced(2.second) >>> Schedule.recurWhile[Long](_ < 5)) *>
    Schedule.collectAll[Int].tapInput[Console, Int](response => putStrLn(response.toString).exitCode)


  def spec =
    testM("test") {
      for {
        ref    <- Ref.make(0)
        fiber  <- ref.getAndUpdate(_ + 1).repeat(schedule).fork
        _      <- TestClock.adjust(20.seconds)
        values <- fiber.join
      } yield assert(values)(equalTo(Chunk.fromIterable(0 to 5)))
    }
}
