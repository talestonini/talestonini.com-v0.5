package com.talestonini.pages.posts

import com.talestonini.pages.BasePostPage
import com.thoughtworks.binding.Binding
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object FunProgCapstone extends BasePostPage {

  @html def postContent(): Binding[Node] =
    <div>
      <h1 id="an-h1-header" class="title">An h1 header</h1>
      <p>Paragraphs are separated by a blank line.</p>
      <p>2nd paragraph. <em>Italic</em>, <strong>bold</strong>, and <code>monospace</code>. Itemized lists
      look like:</p>
      <ul>
        <li>this one</li>
      </ul>
      <ul>
        <li>that one</li>
      </ul>
      <ul>
        <li>the other one</li>
      </ul>
      <p>Note that --- not considering the asterisk --- the actual text
      content starts at 4-columns in.</p>
      <blockquote>
        <p>Block quotes are
        written like so.</p>
        <p>They can span multiple paragraphs,
        if you like.</p>
      </blockquote>
      <p>Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., &quot;it&#39;s all
      in chapters 12--14&quot;). Three dots ... will be converted to an ellipsis.
      Unicode is supported. ☺</p>

      <h2 id="an-h2-header" class="section">An h2 header</h2>
      <p>Here&#39;s a numbered list:</p>
      <ol class="arabic">
        <li>first item</li>
      </ol>
      <ol class="arabic">
        <li>second item</li>
      </ol>
      <ol class="arabic">
        <li>third item</li>
      </ol>
      <p>Note again how the actual text starts at 4 columns in (4 characters
      from the left side). Here&#39;s a code sample:</p>
      <pre><code># Let me re-iterate ...
for i in 1 .. 10 do-something(i)</code></pre>
      <p>As you probably guessed, indented 4 spaces. By the way, instead of
      indenting the block, you can use delimited blocks, if you like:</p>
      <pre><code>define foobar()
    print &quot;Welcome to flavor country!&quot;;</code></pre>
      <p>(which makes copying &amp; pasting easier). You can optionally mark the
      delimited block for Pandoc to syntax highlight it:</p>
      <pre><code class="python">import time
# Quick, count to ten!
for i in range(10):
    # (but not *too* quick)
    time.sleep(0.5)
    print i</code></pre>

      <h3 id="an-h3-header" class="section">An h3 header</h3>
      <p>Now a nested list:</p>
      <ol class="arabic">
        <li>
          <p>First, get these ingredients:</p>
          <ul>
            <li>carrots</li>
          </ul>
          <ul>
            <li>celery</li>
          </ul>
          <ul>
            <li>lentils</li>
          </ul>
        </li>
      </ol>
      <ol class="arabic">
        <li>Boil some water.</li>
      </ol>
      <ol class="arabic">
        <li>
          <p>Dump everything in the pot and follow
          this algorithm:</p>
          <pre><code>find wooden spoon
uncover pot
stir
cover pot
balance wooden spoon precariously on pot handle
wait 10 minutes
goto first step (or shut off burner when done)</code></pre>
          <p>Do not bump wooden spoon or it will fall.</p>
        </li>
      </ol>
      <p>Notice again how text always lines up on 4-space indents (including
      that last line which continues item 3 above).</p>
      <p>Here&#39;s a link to <a href="http://foo.bar">a website</a>, to a <a href="../local-doc.html">local
      doc</a>, and to a <a href="#an-h2-header">section heading in the current
      doc</a>. Here&#39;s a footnote ^1.</p>
      <p>^1: Footnote text goes here.</p>
      <p>Tables can look like this:</p>
      <p>size  material      color
      ----  ------------  ------------
      9     leather       brown
      10    hemp canvas   natural
      11    glass         transparent</p>
      <p>Table: Shoes, their sizes, and what they&#39;re made of</p>
      <p>(The above is the caption for the table.) Pandoc also supports
      multi-line tables:</p>
      <hr />
      <p>keyword   text
      --------  -----------------------
      red       Sunsets, apples, and
                other red or reddish
                things.</p>
      <p>green     Leaves, grass, frogs
                and other things it&#39;s
                not easy being.
      --------  -----------------------</p>
      <p>A horizontal rule follows.</p>
      <hr />
      <p>Here&#39;s a definition list:</p>
      <p>apples
        : Good for making applesauce.
      oranges
        : Citrus!
      tomatoes
        : There&#39;s no &quot;e&quot; in tomatoe.</p>
      <p>Again, text is indented 4 spaces. (Put a blank line between each
      term/definition pair to spread things out more.)</p>
      <p>Here&#39;s a &quot;line block&quot;:</p>
      <p>| Line one
      |   Line too
      | Line tree</p>
      <p>and images can be specified like so:</p>
      <p><img src="../example-image.jpg" alt="example image" title="An exemplary image" /></p>
      <p>Inline math equations go in like so: $\omega = d\phi / dt$. Display
      math should get its own line and be put in in double-dollarsigns:</p>
      <p>$$I = \int \rho R^2 dV$$</p>
      <p>And note that you can backslash-escape any punctuation characters
      which you wish to be displayed literally, ex.: `foo`, *bar*, etc.</p>
    </div>

}
