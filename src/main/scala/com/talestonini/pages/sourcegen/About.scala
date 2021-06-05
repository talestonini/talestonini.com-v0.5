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
        <table style="width:100%">
          <tr>
            <td style="padding-right: 15px; width: 30%;"><img src="/img/talestonini.jpg"/></td>
            <td>I&#39;m <strong>Tales Tonini</strong>, software engineer working in data engineering at <a href="https://eliiza.com.au/">Eliiza</a>.</td>
          </tr>
          <tr>
            <td colspan="2">My current interests in the field are Functional Programming, Distributed Systems, the Scala language, Spark and Kafka.</td>
          </tr>
          <tr>
            <td colspan="2">I live in Melbourne, Australia, but am originally from Brazil. Aside from programming, I like to spend time with family and friends, swimming and the outdoors.</td>
          </tr>
        </table>
      </div>
      
      <h1 id="about-the-website" class="section">About the website</h1>
      <p>I started this website to share my interests in Software Engineering and as a way to learn ScalaJS.
      These are some of the technologies and libraries that I used to build it:</p>
      <ul>
        <li><a href="https://www.scala-lang.org/">Scala 2.13</a></li>
        <li><a href="https://www.scala-js.org/">ScalaJS</a></li>
        <li><a href="https://github.com/ThoughtWorksInc/Binding.scala">ThoughtWorks Binding</a></li>
        <li><a href="https://planet42.github.io/Laika/">Laika</a></li>
        <li><a href="https://firebase.google.com/">Firebase</a></li>
        <li><a href="https://firebase.google.com/firebase/cloud-firestore">Cloud Firestore</a></li>
      </ul>
    </div>

}