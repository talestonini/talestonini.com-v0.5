package com.talestonini

object CodeSnippets {

  object ScalaDecorators {
    def thirdPartyApi() =
      """sealed class ThirdPartyApi {
        |  val prop: String = "foo"
        |  var otherProp: String = "bar"
        |
        |  def doSth() = println(s"doing something with $prop")
        |  def doSthElse() = println(s"doing something else with $prop")
        |  def doAnotherThing() = println(s"doing another thing with $prop")
        |}
        |""".stripMargin

    def traditionalDecorator() =
      """class TraditionalDecorator(tp: ThirdPartyApi) {
        |  def doSth() = tp.doSth()
        |  def doSthElse() = tp.doSthElse()
        |  def doAnotherThing() = tp.doAnotherThing()
        |
        |  def doSthSpecial() = {
        |    println(s"doing something special with ${tp.prop}, the traditional way")
        |    tp.otherProp = "baz"
        |  }
        |}
        |""".stripMargin

    def usingTraditionalDecorator() =
      """  ...
        |  val tp = new ThirdPartyApi()
        |  tp.doSth()
        |
        |  val decorator = new TraditionalDecorator(tp)
        |  decorator.doSthSpecial()
        |  ...
        |""".stripMargin
  }

}
