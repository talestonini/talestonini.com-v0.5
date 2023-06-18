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
      <p>I started this website to share my interests and learnings and as a way to play around with Scala.js. These are some of
      the technologies and libraries that I use to build it:</p>
      <ul>
        <li><a href="https://www.scala-lang.org/">Scala 2.13</a></li>
        <li><a href="https://www.scala-js.org/">Scala.js</a></li>
        <li><a href="https://github.com/ThoughtWorksInc/Binding.scala">ThoughtWorks Binding</a></li>
        <li><a href="https://planet42.github.io/Laika/">Laika</a></li>
        <li><a href="https://firebase.google.com/">Firebase</a></li>
        <li><a href="https://firebase.google.com/firebase/cloud-firestore">Cloud Firestore</a></li>
        <li><a href="https://www.w3schools.com/w3css/default.asp">W3.CSS</a></li>
        <li><a href="https://prismjs.com/index.html">PrismJS</a></li>
      </ul>
      <p>The <strong>source code is open</strong> <a href="https://github.com/talestonini/talestonini.com">in my GitHub account</a> and I would gladly
      receive feedback about it.</p>
      <p>As you can see, I built a little engine to generate Scala.js code for the posts I write in
      <a href="https://en.wikipedia.org/wiki/Markdown">Markdown</a>.</p>
      
      <h1 id="release-notes" class="section">Release notes</h1>
      
      <h3 id="_0-2-x" class="section">0.2.x</h3>
      <ul>
        <li>Replaced <a href="https://github.com/hmil/RosHTTP">RösHTTP</a> for <a href="https://http4s.github.io/http4s-dom/">http4s-dom</a> due to the
        former not being maintained anymore and to give me a reason to play with <a href="https://typelevel.org/cats/">Cats</a>. This is at
        the database layer, implementing API calls to Cloud Firestore.</li>
        <li>Packaging the app with <a href="https://scalacenter.github.io/scalajs-bundler/">scalajs-bundler</a>.</li>
      </ul>
      
      <h3 id="_0-3-x" class="section">0.3.x</h3>
      <ul>
        <li>Added links that allow for sharing a post via LinkedIn and Twitter, and also for copying a post URL to the clipboard.</li>
      </ul>
      
      <h3 id="_0-4-x" class="section">0.4.x</h3>
      <ul>
        <li>Refactored database package to remove usage of <strong>Future</strong> in favour of <strong>Cats IO</strong>.</li>
      </ul>
      
      <h3 id="_0-5-x" class="section">0.5.x</h3>
      <ul>
        <li>Code cleanup.</li>
      </ul>
    </div>

}
