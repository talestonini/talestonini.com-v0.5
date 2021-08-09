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
      <p>Also known as <em>wrappers</em>, decorators are a great alternative to inheritance. They are more pluggable, less static and
      over time are friendlier of code refactoring.</p>
      <p>In this post, I would like to show how you can take advantage of some really nice features of the Scala language to
      write elegant decorators. Besided that, you&#39;ll see how you can extend the functionality of third-party libraries even
      when they have been explicitly marked to not allow inheritance (<em>final</em> classes in Java, or <em>sealed</em> classes in Scala).</p>
      <h1 id="the-problem-a-sealed-library-class" class="title">The problem: a sealed library class</h1>
      <p>Imagine you want to extend the functionality of a library class that has been marked <em>sealed</em>, so that you are not able
      to extend it:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.thirdPartyApi()}</code></pre>
      <div class="aside"><figcaption>Snippet.1 - Third-party library class marked sealed</figcaption></div>
      <p>Sealed classes can only be extended in their own Scala file. Since this is a third-party library, you cannot do that and
      if you tried to extend the <strong>ThirdPartyApi</strong> class in your project, the Scala compiler would complain.</p>
      
      <h2 id="decorator-to-the-rescue" class="section">Decorator to the rescue</h2>
      <p>With a decorator like the following, you can overcome that issue and attach new functionality to the third-party library
      class dinamicaly:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.traditionalDecorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.2 - A traditional decorator</figcaption></div>
      <p>Note how at creation time the decorator expects to be injected with an instance of the decorated class, i.e. the object
      to be wrapped. And because the decorator must adhere to the interface of the wrapped class (after all the decorator is
      supposed to simply wrap it and go wherever it would go), you need to repeat the original functionality and forward calls
      to the wrapped onject.</p>
      <p>The following snippet shows how you&#39;d invoke the newly attached functionality through the decorator instance:</p>
      <pre><code class="lang-scala">{CodeSnippets.ScalaDecorators.usingTraditionalDecorator()}</code></pre>
      <div class="aside"><figcaption>Snippet.3 - Using a traditional decorator</figcaption></div>
      <p>Now your third-party API object can do something special. But isn&#39;t it so annoying that you need to repeat the whole
      interface of the original class to add your special function?</p>
      
      <h2 id="scala-implicit-conversions-can-help-here" class="section">Scala implicit conversions can help here</h2>
      <p>Scala has this somewhat controversial feature called <em>implicits</em> and with it, implicit conversions. Implicits allow you
      to declare a value (or variable or function) <em>implicit</em> within a scope. If there is a function call in that scope that
      expects a value of that type, and that happens to be the only implicit value of that type in the scope), then the value
      is passed in to the function with no need for explicitly mentioning it in the call. Implicits can make your code look
      much cleaner and elegant (and sometimes a little harder to follow, so use it wisely). It resembles Spring autowiring.</p>
    </div>

}
