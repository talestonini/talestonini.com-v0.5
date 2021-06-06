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

object MorseCodeChallenge extends BasePostPage {

  @html def postContent(): Binding[Node] =
    <div>
      <p>This is the second code challenge I developed for <a href="https://eliiza.com.au">Eliiza</a> as we continue to grow our <em>data
      engineering</em> practice.</p>
      <p>This time we want to expand our <a href="https://kafka.apache.org">Apache Kafka</a> skills, so in this challenge candidates are
      asked to decode a <strong>stream of Morse Code messages</strong> with some old news headlines.</p>
      <p>Check it out <a href="https://github.com/eliiza/challenge-morse-code">here</a>!  Would you give it a crack?</p>
    </div>

}
