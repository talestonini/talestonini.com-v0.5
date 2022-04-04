/***************************************************************************************************
 *
 * THIS CODE IS GENERATED AT COMPILE TIME BY LAIKA SBT PLUGIN.
 *
 * Do not modify it directly, as compilation will ovewrite your modifications.
 *
 **************************************************************************************************/
package com.talestonini.pages.sourcegen.posts

import com.talestonini.CodeSnippets
import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object DbLayerRefactor extends BasePostPage {

  @html def postContent(): Binding[Node] =
    <div>
      <p>A few weeks ago I took myself the task of updating the database access layer of this website. Some parts of this page
      are stored in a Cloud Firestore database, like <em>comments</em> and <em>likes</em> (when I actually implement <em>likes</em>), and are
      retrieved via Cloud Firestore&#39;s REST API straight from the browser (there is not a <em>backend for frontend</em> running at the
      server side). The motivation for the update? Despite so thin and intuitive, library
      <a href="https://github.com/hmil/RosHTTP">RösHTTP</a> is not being maintained anymore, and that would hold me back when I am
      finally able to upgrade the code from Scala 2.13 to Scala 3. At the moment, even
      <a href="https://github.com/ThoughtWorksInc/Binding.scala">ThoughtWorks Binding</a> - the data-binding library at the core of this
      website - is not yet ready for Scala 3, but that&#39;s another story.</p>
      <p>Back to the database layer update, my first choice as a substitute to RösHTTP was
      <a href="https://sttp.softwaremill.com/en/v2/">sttp</a>. Because everything runs on the browser, I needed an HTTP library that
      provides a JavaScript backend, which sttp does via the
      <a href="https://sttp.softwaremill.com/en/v2/backends/javascript/fetch.html">Fetch API</a>. It was all well and good until I hit a
      a wall: this backend implementation would require me to change server side Cloud Firestore&#39;s REST API to overcome a
      <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS">CORS</a> issue, which is not an option.</p>
      <p>Back to the drawing board, I needed another HTTP library with a JavaScript backend. So I decided to try
      <a href="https://http4s.org/">http4s</a>, after all it also supports lots of backends and favours the <em>pure functional</em> side of
      Scala through the use of <a href="https://typelevel.org/cats-effect/">Cats Effect</a>. It turned out to be an interesting
      learning opportunity on ScalaJS as a whole. Bear with me.</p>
      <p>On chosing an http4s backend, I started off by searching for the ones with a corresponding ScalaJS offering. <em>Ember</em>
      looked good at first: it offers binaries for ScalaJS
      <a href="https://http4s.org/v1/docs/client.html#creating-the-client">2.13 and 3</a> and has an
      example on <a href="https://http4s.org/v1/docs/client.html#creating-the-client">creating the client</a> in the http4s
      documentation. It turns out that <strong>just because a library transpiles to JavaScript, it does not mean that it can run on
      the browser</strong>. That is the case with ember as it relies on plain TCP sockets, which are not available in the browser
      (check <a href="https://stackoverflow.com/questions/40599069/node-js-net-socket-is-not-a-constructor">this issue</a> on stack
      overflow). And so I learned that ember clients are fine for NodeJS, but not for the browser.</p>
      <p>Ok, I still needed a suitable http4s backend for my refactor. At the corner of http4s documentation page there are some
      &quot;related projects&quot;. One of them - <a href="https://http4s.github.io/http4s-dom/">http4s-dom</a> - looked very promissing...</p>
      <div class="aside">
        <img src="/img/http4s-dom.png" alt="http4s-dom"/>
        <figcaption>Fig.1 - http4s-dom documentation snipet</figcaption>
      </div>
      <p>...until I read dreadind words &quot;backed by fetch&quot;... No, <em>fetch</em> again! What if I ran into a CORS issue once more? I had
      to try it. I already had all TDD tests waiting for the code.</p>
      <p>Changing from ember to the <em>fetch</em> client was super simple. Apart from building the client itself, which ember &quot;wraps&quot;
      in a <a href="https://typelevel.org/cats-effect/docs/std/resource">Cats Effect Resource</a>, the REST API calls used the very same
      interfaces, as expected from a very well designed library such as http4s.</p>
    </div>

}
