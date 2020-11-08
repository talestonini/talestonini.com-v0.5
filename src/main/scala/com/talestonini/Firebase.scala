import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.annotation.JSImport.Namespace
import scala.scalajs.js.|

package firebase {

  @js.native
  @JSGlobal("firebase.Promise")
  class Promise[T] extends Promise_Instance[T] {}

  @js.native
  @JSGlobal("firebase.Promise_Instance")
  class Promise_Instance[T] protected () extends firebase.Thenable[js.Any] {
    def this(resolver: js.Function2[js.Function1[T, Unit], js.Function1[Error, Unit], Any]) = this()
  }

  @js.native
  trait Thenable[T] extends js.Object {
    def `catch`(onReject: js.Function1[Error, Any] = ???): js.Dynamic = js.native

    def then(onResolve: js.Function1[T, Any] = ???,
      onReject: js.Function1[Error, Any] = ???): firebase.Thenable[js.Any] = js.native
  }

  @js.native
  trait User extends firebase.UserInfo {
    def getIdToken(forceRefresh: Boolean = ???): firebase.Promise[js.Any] = js.native
  }

  @js.native
  trait UserInfo extends js.Object {
    var displayName: String | Null = js.native
    var email: String | Null       = js.native
    var photoURL: String | Null    = js.native
    var providerId: String         = js.native
    var uid: String                = js.native
  }

  package app {

    @js.native
    trait App extends js.Object {
      def auth(): firebase.auth.Auth = js.native
    }

  }

  package auth {

    @js.native
    trait Auth extends js.Object {
      def onAuthStateChanged(nextOrObserver: js.Function1[User, _], error: js.Function1[firebase.auth.Error, Any] = ???,
        completed: js.Function0[Any] = ???): js.Function0[Any] = js.native

      def signOut(): firebase.Promise[js.Any] = js.native
    }

    @js.native
    trait Error extends js.Object {
      var code: String    = js.native
      var message: String = js.native
    }

  }

  @js.native
  @JSImport("firebase", Namespace)
  object Firebase extends js.Object {
    def app(name: String = ???): firebase.app.App = js.native

    var apps: js.Array[firebase.app.App | Null] = js.native

    def auth(app: firebase.app.App = ???): firebase.auth.Auth = js.native

    def initializeApp(options: FirebaseConfig, name: String = ???): firebase.app.App = js.native
  }

  @JSExportAll
  case class FirebaseConfig(
    apiKey: String = "AIzaSyDSpyLoxb_xSC7XAO-VUDJ0Hd_XyuquAnY",
    authDomain: String = ""
  )

}
