{%
  class.name = ScalaDecorators
%}
Decorator is a structural Design Pattern whose intent, according to Erich Gamma and others in their classic book
**Design Patterns: Elements of Reusable Object-Oriented Software**, is:

> Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing
> for extending functionality. (Erich Gamma and others, 1994, 175)

Also known as *wrappers*, decorators are the *preferred* alternative to inheritance, as they are more pluggable, less
static and over time are friendlier of code refactoring.

In this post, I would like to show how you can take advantage of some really nice features of the Scala language to
write elegant decorators. Besides that, you'll see how you can extend functionality of third-party libraries even when
they have been explicitly marked to not allow inheritance (i.e. *final* classes in Java, or *sealed* classes in Scala).

## The problem: a sealed library class

Imagine you want to extend functionality of a library class that has been marked *sealed*, like the one below:

``` lang-scala
{CodeSnippets.ScalaDecorators.thirdPartyApi()}
```
<div class="aside"><figcaption>Snippet.1 - Third-party library class marked sealed</figcaption></div>

Sealed classes can only be extended in their own Scala file. Since this is a third-party library, you won't be able to
do that and if you stubbornly tried to extend the **ThirdPartyApi** class above in your own code, the Scala compiler
would complain.

## How can a *decorator* help here?

With a decorator like the following, you can overcome that issue and attach new functionality to the third-party library
class *dinamicaly*:

``` lang-scala
{CodeSnippets.ScalaDecorators.traditionalDecorator()}
```
<div class="aside"><figcaption>Snippet.2 - A traditional decorator</figcaption></div>

Note how at creation time the decorator will expect to be injected with an instance of the decorated class, i.e. the
object to be wrapped. And because the decorator must adhere to the interface of the wrapped class (after all that's
what makes it look like it's extending the original class), you need to repeat the original functionality, forwarding
calls to the wrapped object.

The following snippet shows how you'd invoke the newly attached functionality through the decorator instance:

``` lang-scala
{CodeSnippets.ScalaDecorators.usingTraditionalDecorator()}
```
<div class="aside"><figcaption>Snippet.3 - Using a traditional decorator</figcaption></div>

Now your third-party API object can do something special. But isn't it so annoying how you need to repeat the whole
interface of the original class to add your special function?

## Using Scala's *implicit conversions*

Scala has this somewhat controversial feature called *implicits* and with it, implicit conversions. Implicits allow you
to declare a value (or variable or function) *implicit* within a scope. Then, if there is a function call in *that
scope* that expects a value of *that type*, and the implicit value happens to be *the only* implicit value of that type
in that scope, then it is passed in to the function with no need for explicitly mentioning it in the call. Implicits can
make your code look much cleaner and elegant (and sometimes a little harder to follow too, so use it wisely). They
resemble Spring framework components autowiring, in a way.

But what about implicit *conversions*? Simply put, **implicit conversions** are a feature in the Scala language by which
an implicit function definition can automatically coerce a type into another type. Consider a scope that has such
implicit function coercing type **A** into type **B** and there is a statement where **B** is needed, but the scope only
has a value of type **A**. In such scope, there would be no need to explicitly declare a value of type **B** to satisfy
the statement, because the implicit function would automatically "kick in" to convert the value of type **A** into
**B**.

Let's look at some code to make things clearer:

``` lang-scala
{CodeSnippets.ScalaDecorators.scala2Decorator()}
```
<div class="aside"><figcaption>Snippet.4 - A Scala 2 decorator</figcaption></div>

The implicit converter function definition is in object Scala2Decorator (it could have any name really). The object has
an accompanying class where in fact functionality of the third-party library is extended with a special function. Note
here how the decorator did not have to repeat the interface of the decorated class!

The following snippet shows how you can invoke the dynamically attached special function:

``` lang-scala
{CodeSnippets.ScalaDecorators.usingScala2Decorator()}
```
<div class="aside"><figcaption>Snippet.5 - Using a Scala 2 decorator</figcaption></div>

As you can see, here we can call the special function straight from the wrapped class instance. The fact that there is a
decorator wrapping your library class is actually barely noticeable! How does that work? The special function belongs to
the decorator, not to *tp*!? Well, the implicit converter function imported into the scope is able to kick in and coerce
*tp* into a Scala2Decorator, because it "knows" how to convert a ThirdPartyApi into a Scala2Decorator, allowing the
newly attached special function to be invoked.

Now, can we improve this even further?

## Using Scala 3's *extension methods*

Scala 3 has been recently launched and many improvements have been added to the whole family of implicit features. As
part of these, **extension methods** bring new syntax to simplify the extension of classes. As stated in the
[documentation](https://docs.scala-lang.org/scala3/reference/contextual/extension-methods.html), "extension methods
allow one to add methods to a type after the type is defined". Sounds perfect to write a new decorator, right?

Check out the following code snippet for a taste of extension methods:

``` lang-scala
{CodeSnippets.ScalaDecorators.scala3Decorator()}
```
<div class="aside"><figcaption>Snippet.6 - A Scala 3 decorator</figcaption></div>

Not event a new type is needed! This is how you can invoke your decorated library class now:

``` lang-scala
{CodeSnippets.ScalaDecorators.usingScala3Decorator()}
```
<div class="aside"><figcaption>Snippet.7 - Using a Scala 3 decorator</figcaption></div>

Here

