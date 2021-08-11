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

object ScalaDecorators extends BasePostPage {

  @html def postContent(): Binding[Node] =
    <div>
      <p>Decorator is a structural Design Pattern whose intent, according to Erich Gamma and others in their classic book
      <strong>Design Patterns: Elements of Reusable Object-Oriented Software</strong>, is:</p>
      <blockquote>Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing
      for extending functionality. (Erich Gamma and others, 1994, 175)</blockquote>
      <p>Also known as <em>wrappers</em>, decorators are the <em>preferred</em> alternative to inheritance, as they are more pluggable, less
      static and over time are friendlier of code refactoring.</p>
      <p>In this post, I would like to show how you can take advantage of some really nice features of the Scala language to
      write elegant decorators. Besides that, you&#39;ll see how you can extend functionality of third-party libraries even when
      they have been explicitly marked to not allow inheritance (i.e. <em>final</em> classes in Java, or <em>sealed</em> classes in Scala).</p>
      <h1 id="the-problem-a-sealed-library-class" class="title">The problem: a sealed library class</h1>
      <p>Imagine you want to extend functionality of a library class that has been marked <em>sealed</em>, like the one below:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.thirdPartyApi()}</code></pre>
      <div class="aside"><figcaption>Snippet.1 - Third-party library class marked sealed</figcaption></div>
      <p>Sealed classes can only be extended in their own Scala file. Since this is a third-party library, you won&#39;t be able to
      do that and if you stubbornly tried to extend the <strong>ThirdPartyApi</strong> class above in your own code, the Scala compiler
      would complain.</p>
      
      <h2 id="how-can-a-decorator-help-here" class="section">How can a <em>decorator</em> help here?</h2>
      <p>With a decorator like the following, you can overcome that issue and attach new functionality to the third-party library
      class <em>dinamicaly</em>:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.traditionalDecorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.2 - A traditional decorator</figcaption></div>
      <p>Note how at creation time the decorator will expect to be injected with an instance of the decorated class, i.e. the
      object to be wrapped. And because the decorator must adhere to the interface of the wrapped class (after all that&#39;s
      what makes it look like it&#39;s extending the original class), you need to repeat the original functionality, forwarding
      calls to the wrapped object.</p>
      <p>The following snippet shows how you&#39;d invoke the newly attached functionality through the decorator instance:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.usingTraditionalDecorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.3 - Using a traditional decorator</figcaption></div>
      <p>Now your third-party API object can do something special. But isn&#39;t it so annoying how you need to repeat the whole
      interface of the original class to add your special function?</p>
      
      <h2 id="using-scala-s-implicit-conversions" class="section">Using Scala&#39;s <em>implicit conversions</em></h2>
      <p>Scala has this somewhat controversial feature called <em>implicits</em> and with it, implicit conversions. Implicits allow you
      to declare a value (or variable or function) <em>implicit</em> within a scope. Then, if there is a function call in <em>that
      scope</em> that expects a value of <em>that type</em>, and the implicit value happens to be <em>the only</em> implicit value of that type
      in that scope, then it is passed in to the function with no need for explicitly mentioning it in the call. Implicits can
      make your code look much cleaner and elegant (and sometimes a little harder to follow too, so use it wisely). They
      resemble Spring framework components autowiring, in a way.</p>
      <p>But what about implicit <em>conversions</em>? Simply put, <strong>implicit conversions</strong> are a feature in the Scala language by which
      an implicit function definition can automatically coerce a type into another type. Consider a scope that has such
      implicit function coercing type <strong>A</strong> into type <strong>B</strong> and there is a statement where <strong>B</strong> is needed, but the scope only
      has a value of type <strong>A</strong>. In such scope, there would be no need to explicitly declare a value of type <strong>B</strong> to satisfy
      the statement, because the implicit function would automatically &quot;kick in&quot; to convert the value of type <strong>A</strong> into
      <strong>B</strong>.</p>
      <p>Let&#39;s look at some code to make things clearer:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.scala2Decorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.4 - A Scala 2 decorator</figcaption></div>
      <p>The implicit converter function definition is in object Scala2Decorator (it could have any name really). The object has
      an accompanying class where in fact functionality of the third-party library is extended with a special function. Note
      here how the decorator did not have to repeat the interface of the decorated class!</p>
      <p>The following snippet shows how you can invoke the dynamically attached special function:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.usingScala2Decorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.5 - Using a Scala 2 decorator</figcaption></div>
      <p>As you can see, here we can call the special function straight from the wrapped class instance. The fact that there is a
      decorator wrapping your library class is actually barely noticeable! How does that work? The special function belongs to
      the decorator, not to <em>tp</em>!? Well, the implicit converter function imported into the scope is able to kick in and coerce
      <em>tp</em> into a Scala2Decorator, because it &quot;knows&quot; how to convert a ThirdPartyApi into a Scala2Decorator, allowing the
      newly attached special function to be invoked.</p>
      <p>Now, can we improve this even further?</p>
      
      <h2 id="using-scala-3-s-extension-methods" class="section">Using Scala 3&#39;s <em>extension methods</em></h2>
      <p>Scala 3 has been recently launched and many improvements have been added to the whole family of implicit features. As
      part of these, <strong>extension methods</strong> bring new syntax to simplify the extension of classes. As stated in the
      <a href="https://docs.scala-lang.org/scala3/reference/contextual/extension-methods.html">documentation</a>, &quot;extension methods
      allow one to add methods to a type after the type is defined&quot;. Sounds perfect to write a new decorator, right?</p>
      <p>Check out the following code snippet for a taste of extension methods:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.scala3Decorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.6 - A Scala 3 decorator</figcaption></div>
      <p>Not event a new type is needed! This is how you can invoke your decorated library class now:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.usingScala3Decorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.7 - Using a Scala 3 decorator</figcaption></div>
      <p>Here</p>
    </div>

}
