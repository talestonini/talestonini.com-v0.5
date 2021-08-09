{%
  class.name = ScalaDecorators
%}
Decorator is a structural Design Pattern whose intent, according to Erich Gamma and others in their classic book
**Design Patterns: Elements of Reusable Object-Oriented Software**, is:

> Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing
> for extending functionality. (Erich Gamma and others, 1994, 175)

Also known as *wrappers*, decorators are a great alternative to inheritance. They are more pluggable, less static and
over time are friendlier of code refactoring.

In this post, I would like to show how you can take advantage of some really nice features of the Scala language to
write elegant decorators. Besided that, you'll see how you can extend the functionality of third-party libraries even
when they have been explicitly marked to not allow inheritance (*final* classes in Java, or *sealed* classes in Scala).

## The problem: a sealed library class

Imagine you want to extend the functionality of a library class that has been marked *sealed*, so that you are not able
to extend it:

``` lang-scala
{CodeSnippets.ScalaDecorators.thirdPartyApi()}
```
<div class="aside"><figcaption>Snippet.1 - Third-party library class marked sealed</figcaption></div>

Sealed classes can only be extended in their own Scala file. Since this is a third-party library, you cannot do that and
if you tried to extend the **ThirdPartyApi** class in your project, the Scala compiler would complain.

## Decorator to the rescue

With a decorator like the following, you can overcome that issue and attach new functionality to the third-party library
class dinamicaly:

``` lang-scala
{CodeSnippets.ScalaDecorators.traditionalDecorator()}
```
<div class="aside"><figcaption>Snippet.2 - A traditional decorator</figcaption></div>

Note how at creation time the decorator expects to be injected with an instance of the decorated class, i.e. the object
to be wrapped. And because the decorator must adhere to the interface of the wrapped class (after all the decorator is
supposed to simply wrap it and go wherever it would go), you need to repeat the original functionality and forward calls
to the wrapped onject.

The following snippet shows how you'd invoke the newly attached functionality through the decorator instance:

``` lang-scala
{CodeSnippets.ScalaDecorators.usingTraditionalDecorator()}
```
<div class="aside"><figcaption>Snippet.3 - Using a traditional decorator</figcaption></div>

Now your third-party API object can do something special. But isn't it so annoying that you need to repeat the whole
interface of the original class to add your special function?

## Scala implicit conversions can help here

Scala has this somewhat controversial feature called *implicits* and with it, implicit conversions. Implicits allow you
to declare a value (or variable or function) *implicit* within a scope. If there is a function call in that scope that
expects a value of that type, and that happens to be the only implicit value of that type in the scope), then the value
is passed in to the function with no need for explicitly mentioning it in the call. Implicits can make your code look
much cleaner and elegant (and sometimes a little harder to follow, so use it wisely). It resembles Spring autowiring.
