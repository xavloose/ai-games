package net.xavloose.aigames

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainAdapter(gameList: ArrayList<MainItem>) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
  private var gameList = gameList

  class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title = view.findViewById<TextView>(R.id.title)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
    var itemView = LayoutInflater.from(parent.context)
        .inflate(R.layout.game_cardview, parent, false)
    return MainViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
    var game = gameList[position]
    holder.title.setText(game.title)
  }

  override fun getItemCount() = gameList.size
}