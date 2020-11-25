package com.talestonini.pages

import com.talestonini.Routing.postPageObjMap
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding.{Var, Vars}
import org.lrng.binding.html
import org.scalajs.dom.raw.Node

object Home {

  @html def apply(): Binding[Node] =
    <div>{postPreviews()}</div>

  val bPostPages: Vars[BasePostPage] = Vars(
    postPageObjMap.values.toSeq: _*
  )

  @html private def postPreviews() =
    for (pp <- bPostPages)
      yield <div>
              {pp.postHeadline()}
              {pp.postPreviewContent()}
            </div>

}
