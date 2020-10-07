import scala.scalajs.js
import scala.scalajs.js.annotation._
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
    def delete(): firebase.Promise[js.Any] = js.native

    var emailVerified: Boolean = js.native

    def getToken(forceRefresh: Boolean = ???): firebase.Promise[js.Any] = js.native

    var isAnonymous: Boolean = js.native

    def link(credential: firebase.auth.AuthCredential): firebase.Promise[js.Any] = js.native

    def linkWithPopup(provider: firebase.auth.AuthProvider): firebase.Promise[js.Any] = js.native

    def linkWithRedirect(provider: firebase.auth.AuthProvider): firebase.Promise[js.Any] = js.native

    var providerData: js.Array[firebase.UserInfo | Null] = js.native

    def reauthenticate(credential: firebase.auth.AuthCredential): firebase.Promise[js.Any] = js.native

    var refreshToken: String = js.native

    def reload(): firebase.Promise[js.Any] = js.native

    def sendEmailVerification(): firebase.Promise[js.Any] = js.native

    def unlink(providerId: String): firebase.Promise[js.Any] = js.native

    def updateEmail(newEmail: String): firebase.Promise[js.Any] = js.native

    def updatePassword(newPassword: String): firebase.Promise[js.Any] = js.native

    def updateProfile(profile: js.Any): firebase.Promise[js.Any] = js.native
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

      var name: String    = js.native
      var options: Object = js.native
    }

  }

  package auth {

    @js.native
    trait ActionCodeInfo extends js.Object {}

    @js.native
    trait Auth extends js.Object {
      var app: firebase.app.App = js.native

      def applyActionCode(code: String): firebase.Promise[js.Any] = js.native

      def checkActionCode(code: String): firebase.Promise[js.Any] = js.native

      def confirmPasswordReset(code: String, newPassword: String): firebase.Promise[js.Any] = js.native

      def createCustomToken(uid: String, developerClaims: Object | Null = ???): String = js.native

      def createUserWithEmailAndPassword(email: String, password: String): firebase.Promise[js.Any] = js.native

      var currentUser: firebase.User | Null = js.native

      def fetchProvidersForEmail(email: String): firebase.Promise[js.Any] = js.native

      def getRedirectResult(): firebase.Promise[js.Any] = js.native

      def onAuthStateChanged(nextOrObserver: js.Function1[UserInfo, _],
        error: js.Function1[firebase.auth.Error, Any] = ???,
        completed: js.Function0[Any] = ???): js.Function0[Any] = js.native

      def sendPasswordResetEmail(email: String): firebase.Promise[js.Any] = js.native

      def signInAnonymously(): firebase.Promise[js.Any] = js.native

      def signInWithCredential(credential: firebase.auth.AuthCredential): firebase.Promise[js.Any] = js.native

      def signInWithCustomToken(token: String): firebase.Promise[js.Any] = js.native

      def signInWithEmailAndPassword(email: String, password: String): firebase.Promise[js.Any] = js.native

      def signInWithPopup(provider: firebase.auth.AuthProvider): firebase.Promise[js.Any] = js.native

      def signInWithRedirect(provider: firebase.auth.AuthProvider): firebase.Promise[js.Any] = js.native

      def signOut(): firebase.Promise[js.Any] = js.native

      def verifyIdToken(idToken: String): firebase.Promise[js.Any] = js.native

      def verifyPasswordResetCode(code: String): firebase.Promise[js.Any] = js.native
    }

    @js.native
    trait AuthCredential extends js.Object {
      var provider: String = js.native
    }

    @js.native
    trait AuthProvider extends js.Object {
      var providerId: String = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.EmailAuthProvider")
    class EmailAuthProvider extends EmailAuthProvider_Instance {}

    @js.native
    @JSGlobal("firebase.auth.EmailAuthProvider")
    object EmailAuthProvider extends js.Object {
      var PROVIDER_ID: String = js.native

      def credential(email: String, password: String): firebase.auth.AuthCredential = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.EmailAuthProvider_Instance")
    class EmailAuthProvider_Instance extends firebase.auth.AuthProvider {}

    @js.native
    trait Error extends js.Object {
      var code: String    = js.native
      var message: String = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.FacebookAuthProvider")
    class FacebookAuthProvider extends FacebookAuthProvider_Instance {}

    @js.native
    @JSGlobal("firebase.auth.FacebookAuthProvider")
    object FacebookAuthProvider extends js.Object {
      var PROVIDER_ID: String = js.native

      def credential(token: String): firebase.auth.AuthCredential = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.FacebookAuthProvider_Instance")
    class FacebookAuthProvider_Instance extends firebase.auth.AuthProvider {
      def addScope(scope: String): js.Dynamic = js.native

      def setCustomParameters(customOAuthParameters: Object): js.Dynamic = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.GithubAuthProvider")
    class GithubAuthProvider extends GithubAuthProvider_Instance {}

    @js.native
    @JSGlobal("firebase.auth.GithubAuthProvider")
    object GithubAuthProvider extends js.Object {
      var PROVIDER_ID: String = js.native

      def credential(token: String): firebase.auth.AuthCredential = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.GithubAuthProvider_Instance")
    class GithubAuthProvider_Instance extends firebase.auth.AuthProvider {
      def addScope(scope: String): js.Dynamic = js.native

      def setCustomParameters(customOAuthParameters: Object): js.Dynamic = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.GoogleAuthProvider")
    class GoogleAuthProvider extends GoogleAuthProvider_Instance {}

    @js.native
    @JSGlobal("firebase.auth.GoogleAuthProvider")
    object GoogleAuthProvider extends js.Object {
      var PROVIDER_ID: String = js.native

      def credential(idToken: String | Null = ???,
        accessToken: String | Null = ???): firebase.auth.AuthCredential = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.GoogleAuthProvider_Instance")
    class GoogleAuthProvider_Instance extends firebase.auth.AuthProvider {
      def addScope(scope: String): js.Dynamic = js.native

      def setCustomParameters(customOAuthParameters: Object): js.Dynamic = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.TwitterAuthProvider")
    class TwitterAuthProvider extends TwitterAuthProvider_Instance {}

    @js.native
    @JSGlobal("firebase.auth.TwitterAuthProvider")
    object TwitterAuthProvider extends js.Object {
      var PROVIDER_ID: String = js.native

      def credential(token: String, secret: String): firebase.auth.AuthCredential = js.native
    }

    @js.native
    @JSGlobal("firebase.auth.TwitterAuthProvider_Instance")
    class TwitterAuthProvider_Instance extends firebase.auth.AuthProvider {

      def setCustomParameters(customOAuthParameters: Object): js.Dynamic = js.native
    }

    @js.native
    @JSGlobal("firebase.auth")
    object Auth extends js.Object {
      type UserCredential = js.Any
    }

  }

  @js.native
  @JSGlobal("firebase")
  object Firebase extends js.Object {
    var SDK_VERSION: String = js.native

    def app(name: String = ???): firebase.app.App = js.native

    var apps: js.Array[firebase.app.App | Null] = js.native

    def auth(app: firebase.app.App = ???): firebase.auth.Auth = js.native

    def initializeApp(options: FirebaseConfig, name: String = ???): firebase.app.App = js.native
  }

  @JSExportAll
  case class FirebaseConfig(
    apiKey: String,
    authDomain: String
  )

}
