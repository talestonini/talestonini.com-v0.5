package com.talestonini.pages

import com.talestonini.App.user
import com.talestonini.Routing._
import com.thoughtworks.binding.Binding
import firebase._
import org.lrng.binding.html
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.Node

object Home {

  @html def apply(): Binding[Node] =
    <div>
    {
      if (user.isLoggedIn.bind)
        <p>Hello, {user.displayName.bind}!</p>
        <button onclick={e: Event => Firebase.auth().signOut()}>Sign Out</button>
      else
        <p>Hello!</p>
        <button onclick={e: Event => route.state.value = hash2Page("login")}>Sign In</button>
    }
    </div>

}
