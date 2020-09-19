package com.talestonini.components

import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Logo {

  @html def apply(): Binding[Node] =
    <div class="w3-col w3-left logo" style="width:245px">
      <a href="#/" data:onclick="close_sidebar()">
        <table>
          <tr>
            <td class="symbol">&#x276F;</td>
            <td class="tales_t">T</td>
            <td class="ales">ales</td>
            {pronunciation("/tɑː \u2022 les/").bind}
          </tr>
          <tr>
            <td></td>
            <td class="tonini_t">T</td>
            <td class="onini">onini</td>
            {pronunciation("/toʊ \u2022 niː \u2022 nɪ/").bind}
          </tr>
          <tr>
            <td></td>
            <td class="dot">&#x2022;</td>
            <td class="com">com</td>
            <td></td>
          </tr>
        </table>
      </a>
    </div>

  @html def pronunciation(p: String): Binding[Node] =
    <td class="pronunciation">
      <a href="https://www.oxfordlearnersdictionaries.com/about/english/pronunciation_english" target="_blank">
        {p}
      </a>
    </td>

}
