package controllers

import javax.inject.Inject

import play.api.libs.streams.Accumulator
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.sys.process._

/**
 * A controller full of vulnerabilities.
 */
class HomeController @Inject()(ws: WSClient, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def index = Action { implicit request  =>
     Ok(Html(s"""
       <html>
         <body>
           <ul>
             <li><a href="${routes.HomeController.attackerQuerySimple}">attackerQuerySimple</a></li>
             <li><a href="${routes.HomeController.attackerQueryPatternMatching}">attackerQueryPatternMatching</a></li>
             <li><a href="${routes.HomeController.attackerQuery}">attackerQuery</a></li>
             <li><a href="${routes.HomeController.attackerRouteControlledQuery("foo")}">attackerRouteControlledQuery</a></li>
             <li><a href="${routes.HomeController.attackerRouteControlledPath("foo")}">attackerRouteControlledPath</a></li>
             <li><a href="${routes.HomeController.attackerCookie}">attackerCookie</a></li>
             <li><a href="${routes.HomeController.attackerHeader}">attackerHeader</a></li>
             <li><a href="${routes.HomeController.attackerFormInput}">attackerFormInput</a></li>
             <li><a href="${routes.HomeController.attackerFlash}">attackerFlash</a></li>
             <li><a href="${routes.HomeController.constraintForm}">constraintForm</a></li>
             <li><a href="${routes.HomeController.attackerSSRF}">attackerSSRF</a></li>
             <li><a href="${routes.HomeController.attackerCustomBodyParser}">attackerCustomBodyParser</a></li>
           </ul>
         </body>
       </html>
     """))
  }

  /**
   * Command injection & XSS directly from directly called query parameter
   */
  def attackerQuerySimple = Action { implicit request  =>
    val address = request.getQueryString("address")

    // [RuleTest] Command Injection 
    s"ping ${address}".!

    // [RuleTest] Cross-Site Scripting: Reflected
    val html = Html(s"Host ${address} pinged")

    Ok(html) as HTML
  }

  /**
   * Command injection & XSS directly from directly called query parameter
   */
  def attackerQueryPatternMatching = Action { implicit request  =>

    val addressRE= "(.*):(\\d+)".r
    val address = request.cookies.get("address").get.value

    address match {
      // [RuleTest] Command Injection 
      case addressRE(address, port) => s"ping ${address}".!
    }
    // [RuleTest] Cross-Site Scripting: Reflected
    Ok(Html(s"Host ${address} pinged")) as HTML
  }

  /**
   * XSS directly from directly called query parameter
   */
  def attackerQuery = Action { implicit request  =>

    val result = request.getQueryString("attacker").map { command =>
      // Render the command directly from query parameter, this is the obvious example
      // User thinks this is system controlled and validated, so turns it into HTML
      // and unescapes it, resulting in XSS.
      Ok(Html(command))
    }.getOrElse(Ok(""))

    result as HTML
  }

  /**
   * XSS through query string parsed by generated router from conf/routes file.
   */
  def attackerRouteControlledQuery(attacker: String) = Action { implicit request =>
    Ok(Html(attacker)) as HTML
  }

  /**
   * XSS through path binding parsed by generated router from conf/routes file.
   */
  def attackerRouteControlledPath(attacker: String) = Action { implicit request =>
    Ok(Html(attacker)) as HTML
  }

  /**
   * XSS through attacker controlled info in cookie
   */
  def attackerCookie = Action { implicit request =>
    // User cookies have no message authentication by default, so an attacker can pass in a cookie
    val result = request.cookies.get("attacker").map { attackerCookie =>
      // Render the command
      Ok(Html(attackerCookie.value))
    }.getOrElse(Ok(""))

    result as HTML
  }

  /**
   * XSS through attacker controlled header
   */
  def attackerHeader = Action { implicit request =>
    // Request headers are also unvalidated by default.
    // The usual example is pulling the Location header to do an unsafe redirect
    val result = request.headers.get("Attacker").map { command =>
      // Render the command
      Ok(command)
    }.getOrElse(Ok(""))

    result as HTML
  }

  /**
   * Unbound redirect through Header
   */
  def attackerOpenRedirect = Action { implicit request =>
    request.headers.get("Location") match {
      case Some(attackerLocation) =>
        // Also see https://github.com/playframework/playframework/issues/6450
        Redirect(attackerLocation)

      case None =>
        Ok("No location found!")
    }
  }

  /**
   * XSS through URL encoded form input.
   */
  def attackerFormInput = Action { implicit request =>
    val boundForm = FormData.form.bindFromRequest()
    boundForm.fold(badData => BadRequest("Bad form binding"), userData => {
      // Render the attacker command as HTML
      val command = userData.name
      Ok(Html(command)) as HTML
    })
  }

  /**
   * XSS through attacker controlled flash cookie.
   */
  def attackerFlash = Action { implicit request =>
    // Flash is usually handled with
    // Redirect(routes.HomeController.attackerFlash()).flashing("info" -> "Some text")
    // but if the user puts HTML in it and then renders it,
    // Flash is not signed by default in 2.5.x, and so will take a user's unvalidated
    // flash cookie as input.

    val result = request.flash.get("info").map { command =>
      // Page displays XSS in the flashing input.
      Ok(Html(command))
    }.getOrElse(Ok(""))

    result as HTML
  }

  // Render a boring form
  def constraintForm = Action { implicit request  =>
    Ok(views.html.index(FormData.customForm))
  }

  /**
   * XSS through custom constraint with user input
   */
  def attackerConstraintForm = Action { implicit request =>

    // Bind a form that uses the i18n messages api, but the user input is reflected in the error message
    // Play takes a raw string here and escapes everything, but it may be possible to escape this...
    val boundForm = FormData.customForm.bindFromRequest()
    boundForm.fold(formWithErrors => {
      val nameField = formWithErrors("name")
      nameField.error.map { error =>
        val formWithMoreErrors = formWithErrors.withGlobalError(s"""You should not have typed <a href="#">${error.message}</a>""")
        BadRequest(views.html.index(formWithMoreErrors))
      }.getOrElse(BadRequest(views.html.index(formWithErrors)))
    }, userData => {
        Ok("everything is fine") as HTML
    })
  }

  /**
   * SSRF attacks done with Play WS
   */
  def attackerSSRF = Action.async { implicit request  =>
    // Play WS does not have a whitelist of valid URLs, so if you're calling it
    // directly with user input, you're open to SSRF.  The best thing to do is
    // to place WS access in a wrapper, i.e.
    // https://www.playframework.com/documentation/2.5.x/ScalaTestingWebServiceClients#Testing-a-GitHub-client

    val attackerUrl = request.body.asText.getOrElse("http://google.com")

    ws.url(attackerUrl).get().map { response =>
      // For bonus points, we can pull things out of the response as well...
      Ok(s"I called out to $attackerUrl")
    }
  }

  /**
   * Command injection with custom body parser
   */
  def attackerCustomBodyParser = Action(bodyParser = BodyParser { header: RequestHeader =>
    // request header is a request without a body
    // http://localhost:9000/attackerCustomBodyParser?address=/etc/passwd
    val result = header.getQueryString("filename").map { filename =>
      // [RuleTest] Command Injection
      s"cat ${filename}".!!
    }.getOrElse("No filename found!")

    Accumulator.done(Right(Foo(bar = result)))
  }) { implicit request: Request[Foo] =>
    val foo: Foo = request.body
    Ok(foo.bar)
  }

  case class Foo(bar: String)
  // Full on level 3 HATEOAS REST APIs are particularly dumb about following
  // URL links blindly... could use Siren/HAL/JSON-LD to make this more interesting...
  //
  //import play.api.libs.json._
  //val json: JsValue = request.body.asJson.getOrElse(Json.arr())
  //val attackerUrl: String = (json \ "attacker").get.as[String]

  // Attacks through custom body parsers?
  // https://www.playframework.com/documentation/2.5.x/ScalaBodyParsers#Writing-a-custom-body-parser

  // Attacks through custom multipart file upload?
  // https://github.com/playframework/play-scala-fileupload-example

  // CSRF attacks?
  // Play does not use CSRF tokens or SameSite unless you specifically enable the
  // CSRFFilter:
  // https://www.playframework.com/documentation/2.5.x/ScalaCsrf

  // Sadly chrome extensions can POST directly: see
  // https://www.playframework.com/security/vulnerability/20160304-CsrfBypass
  // for details.

  // DNS rebinding attacks still to do (practical without dnsrebinder.net?)
  // http://benmmurphy.github.io/blog/2016/07/11/rails-webconsole-dns-rebinding/
  // https://github.com/benmmurphy/rebinder
  // https://www.playframework.com/documentation/2.5.x/AllowedHostsFilter

  // Sidejacking attacks not really practical without subdomains

  // Fairly easy to do DoS attacks if you buffer a stream in memory with an unbounded data structure...
}

object FormData {
  import play.api.data.Form

  import play.api.data.Forms._

  def form = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(UserData.apply)(UserData.unapply)
  )

  def customForm = Form(
    mapping(
      "name" -> text.verifying(customConstraint),
      "age" -> number
    )(UserData.apply)(UserData.unapply)
  )

  import play.api.data.validation._

  // https://playframework.com/documentation/2.5.x/ScalaCustomValidations
  val customConstraint: Constraint[String] = Constraint("constraints.invaliduser")({
    attackerInput =>

      val errors = attackerInput match {
        case s: String if s.contains(" ") =>
          // Error message contains attacker controlled input text
          // Play takes a raw string here and escapes everything, but it may be possible to escape this...
          Seq(ValidationError("error.invaliduser", attackerInput))

        case _ =>
          Nil
      }

      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })


  case class UserData(name: String, age:Int)
}
