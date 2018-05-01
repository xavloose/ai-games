package net.xavloose.aigames.api.ai

import android.content.Context

class AI(url: String, context: Context, volleySingleton: VolleySingleton) {
  private val url = url
  var queue = volleySingleton

  fun makeRequest() {

  }
}