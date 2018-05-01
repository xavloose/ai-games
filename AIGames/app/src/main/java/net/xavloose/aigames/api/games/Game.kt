package net.xavloose.aigames.api.games

import android.view.View
import org.json.JSONObject

interface Game {
  val layout: Int
    get() = this.layout
  fun startGame(view: View, p1ai: Boolean, p1aiUri: String, p2ai: Boolean, p2aiUri: String, callback: () -> Unit)
}