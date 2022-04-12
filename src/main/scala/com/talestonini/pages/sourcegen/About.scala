/***************************************************************************************************
 *
 * THIS CODE IS GENERATED AT COMPILE TIME BY LAIKA SBT PLUGIN.
 *
 * Do not modify it directly, as compilation will ovewrite your modifications.
 *
 **************************************************************************************************/
package com.talestonini.pages.sourcegen

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object About {

  @html def apply(): Binding[Node] =
    <div>
      <h1 id="about-me" class="title">About me</h1>
      <div class="aside">
        <table class="w3-hide-small" style="width:100%">
          <tr>
            <td style="padding-right: 15px; width: 30%;"><img src="/img/talestonini.jpg"/></td>
            <td>
              <p>Hi!, my name is <strong>Tales Tonini</strong> and I&#39;m a software engineer interested in Functional Programming, Distributed Systems and the Scala language.</p>
              <p>I work in data engineering for an AI consultancy called <a href="https://eliiza.com.au/">Eliiza</a>, and for the past 5 years I&#39;ve mainly worked with Spark and Kafka, platforms that I really enjoy to program in.</p>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              I live in Melbourne, Australia, but am originally from Brazil. Aside from programming, I like to spend time with family and friends, swimming and the outdoors.
            </td>
          </tr>
        </table>
        <table class="w3-hide-large w3-hide-medium" style="width:100%">
          <tr>
            <td style="padding-right: 10px; width: 30%;"><img src="/img/talestonini.jpg"/></td>
            <td>
              Hi!, my name is <strong>Tales Tonini</strong> and I&#39;m a software engineer interested in Functional Programming, Distributed Systems and the Scala language.
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <p>I work in data engineering for an AI consultancy called <a href="https://eliiza.com.au/">Eliiza</a>, and for the past 5 years I&#39;ve mainly worked with Spark and Kafka, platforms that I really enjoy to program in.</p>
              <p>I live in Melbourne, Australia, but am originally from Brazil. Aside from programming, I like to spend time with family and friends, swimming and the outdoors.</p>
            </td>
          </tr>
        </table>
      </div>
      
      <h1 id="about-my-website" class="section">About my website</h1>
      <p>I started this website to share my interests and learnings and as a way to play around with ScalaJS. These are some of
      the technologies and libraries that I use to build it:</p>
      <ul>
        <li><a href="https://www.scala-lang.org/">Scala 2.13</a></li>
        <li><a href="https://www.scala-js.org/">ScalaJS</a></li>
        <li><a href="https://github.com/ThoughtWorksInc/Binding.scala">ThoughtWorks Binding</a></li>
        <li><a href="https://planet42.github.io/Laika/">Laika</a></li>
        <li><a href="https://firebase.google.com/">Firebase</a></li>
        <li><a href="https://firebase.google.com/firebase/cloud-firestore">Cloud Firestore</a></li>
        <li><a href="https://www.w3schools.com/w3css/default.asp">W3.CSS</a></li>
        <li><a href="https://prismjs.com/index.html">PrismJS</a></li>
      </ul>
      <p>The <strong>source code is open</strong> <a href="https://github.com/talestonini/talestonini.com">in my GitHub account</a> and I would gladly
      receive feedback about it.</p>
      <p>As you can see, I built a little engine to generate ScalaJS code for the posts I write in
      <a href="https://en.wikipedia.org/wiki/Markdown">Markdown</a>.</p>
      
      <h2 id="a-note-on-data-privacy-and-transparency" class="section">A note on data privacy and transparency</h2>
      <p>My website is hosted by <strong>Firebase</strong> (a platform that is owned by Google) and has <strong>Google Analytics</strong> enabled, so that
      I can track visits to my pages and posts. I also allow visitors to login via some social media platforms (GitHub,
      Twitter, Google and Facebook), which allows visitors to leave comments on the posts and soon to leave likes on them too.
      I do not do anything special or malicious with the data I collect about visitors and visits, other than satisfy my own
      curiosity about which posts attract more attention. For the sake of full transparency, this is what I collect:</p>
      <ul>
        <li>Count of visits per page (not linked to visitors);</li>
        <li>Personal identifiers that visitors utilise in their social media accounts used to log into my website;</li>
        <li>Comments (and soon likes) left on the posts by logged-in visitors.</li>
      </ul>
      
      <h1 id="release-notes" class="section">Release notes</h1>
      
      <h3 id="_0-2-0" class="section">0.2.0</h3>
      <ul>
        <li>Replaced <a href="https://github.com/hmil/RosHTTP">RösHTTP</a> for <a href="https://http4s.github.io/http4s-dom/">Http4s-DOM</a> due to the
        former not being maintained anymore and to give me a reason to play with <a href="https://typelevel.org/cats/">Cats</a>. This is at
        the database layer, implementing API calls to Cloud Firestore.</li>
        <li>Packaging the app with <a href="https://scalacenter.github.io/scalajs-bundler/">scalajs-bundler</a>.</li>
      </ul>
      
      <h3 id="_0-2-1" class="section">0.2.1</h3>
      <ul>
        <li>New post <a href="https://talestonini.com/#/dbLayerRefactor">Refactoring the database access layer</a>.</li>
      </ul>
      
      <h3 id="_0-2-2" class="section">0.2.2</h3>
      <ul>
        <li>Fixed typos.</li>
      </ul>
    </div>

}
