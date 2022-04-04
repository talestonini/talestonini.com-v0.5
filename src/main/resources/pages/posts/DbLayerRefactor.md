{%
  class.name = DbLayerRefactor
%}
A few weeks ago I took myself the task of updating the database access layer of this website. Some parts of this page
are stored in a Cloud Firestore database, like *comments* and *likes* (when I actually implement *likes*), and are
retrieved via Cloud Firestore's REST API straight from the browser (there is not a *backend for frontend* running at the
server side). The motivation for the update? Despite so thin and intuitive, library
[RösHTTP](https://github.com/hmil/RosHTTP) is not being maintained anymore, and that would hold me back when I am
finally able to upgrade the code from Scala 2.13 to Scala 3. At the moment, even
[ThoughtWorks Binding](https://github.com/ThoughtWorksInc/Binding.scala) - the data-binding library at the core of this
website - is not yet ready for Scala 3, but that's another story.

Back to the database layer update, my first choice as a substitute to RösHTTP was
[sttp](https://sttp.softwaremill.com/en/v2/). Because everything runs on the browser, I needed an HTTP library that
provides a JavaScript backend, which sttp does via the
[Fetch API](https://sttp.softwaremill.com/en/v2/backends/javascript/fetch.html). It was all well and good until I hit a
a wall: this backend implementation would require me to change server side Cloud Firestore's REST API to overcome a
[CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS) issue, which is not an option.

Back to the drawing board, I needed another HTTP library with a JavaScript backend. So I decided to try
[http4s](https://http4s.org/), after all it also supports lots of backends and favours the *pure functional* side of
Scala through the use of [Cats Effect](https://typelevel.org/cats-effect/). It turned out to be an interesting
learning opportunity on ScalaJS as a whole. Bear with me.

On chosing an http4s backend, I started off by searching for the ones with a corresponding ScalaJS offering. *Ember*
looked good at first: it offers binaries for ScalaJS
[2.13 and 3](https://http4s.org/v1/docs/client.html#creating-the-client) and has an
example on [creating the client](https://http4s.org/v1/docs/client.html#creating-the-client) in the http4s
documentation. It turns out that **just because a library transpiles to JavaScript, it does not mean that it can run on
the browser**. That is the case with ember as it relies on plain TCP sockets, which are not available in the browser
(check [this issue](https://stackoverflow.com/questions/40599069/node-js-net-socket-is-not-a-constructor) on stack
overflow). And so I learned that ember clients are fine for NodeJS, but not for the browser.

Ok, I still needed a suitable http4s backend for my refactor. At the corner of http4s documentation page there are some
"related projects". One of them - [http4s-dom](https://http4s.github.io/http4s-dom/) - looked very promissing...

<div class="aside">
  <img src="/img/http4s-dom.png" alt="http4s-dom" />
  <figcaption>Fig.1 - http4s-dom documentation snipet</figcaption>
</div>

...until I read dreadind words "backed by fetch"... No, *fetch* again! What if I ran into a CORS issue once more? I had
to try it. I already had all TDD tests waiting for the code.

Changing from ember to the *fetch* client was super simple. Apart from building the client itself, which ember "wraps"
in a [Cats Effect Resource](https://typelevel.org/cats-effect/docs/std/resource), the REST API calls used the very same
interfaces, as expected from a very well designed library such as http4s.
