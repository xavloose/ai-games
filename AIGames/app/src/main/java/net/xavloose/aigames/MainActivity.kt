package net.xavloose.aigames

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast

// Google Services
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

// Firebase Imports
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import net.xavloose.aigames.api.games.GameActivity

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

  private lateinit var mFirebaseAuth: FirebaseAuth
  private var mFirebaseUser: FirebaseUser? = null
  private lateinit var userUid: String
  lateinit var mFirebaseDatabase: DatabaseReference

  private lateinit var mAdView: AdView

  private var gameList = ArrayList<MainItem>()
  private lateinit var recyclerView: RecyclerView
  private lateinit var mainAdapter: MainAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    mAdView = findViewById(R.id.adView)
    var adRequest = AdRequest.Builder().build()
    mAdView.loadAd(adRequest)

    mFirebaseAuth = FirebaseAuth.getInstance()
    mFirebaseUser = mFirebaseAuth.currentUser
    if (mFirebaseUser == null) {
      startActivity( Intent(this, SignInActivity::class.java))
      finish()
      return
    }
    userUid = mFirebaseUser!!.uid

    mFirebaseDatabase = FirebaseDatabase.getInstance().reference

    recyclerView = findViewById(R.id.main_recycler_view)
    mainAdapter = MainAdapter(gameList)
    var mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = mLayoutManager
    recyclerView.itemAnimator = DefaultItemAnimator()
    recyclerView.adapter = mainAdapter
    val noSAM = object: ClickListener {
      override fun onClick(view: View, position: Int) {
        if (gameList[position].title == "Chess") {
          startActivity(Intent(this@MainActivity, GameActivity::class.java))
        } else {
          Toast.makeText(this@MainActivity, gameList[position].title + " not yet implemented", Toast.LENGTH_SHORT).show()
        }
      }
    }
    recyclerView.addOnItemTouchListener(RecyclerTouchListener(this, recyclerView, noSAM))

    gameList.add(MainItem("Chess"))
    gameList.add(MainItem("Checkers"))
    gameList.add(MainItem("Poker"))
  }

  override fun onStart() {
    super.onStart()
    // TODO: add code to check if user is signed in
  }

  override fun onPause() {
    mAdView.pause()
    //mFirebaseAdapter.stopListenting()
    super.onPause()
  }

  override fun onResume() {
    mAdView.resume()
    super.onResume()
  }

  override fun onDestroy() {
    mAdView.destroy()
    super.onDestroy()
  }

  override fun onConnectionFailed(connectionResult: ConnectionResult) {
    Log.d("MainActivity", "onConnectionFailed:" + connectionResult)
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
  }

  interface ClickListener {
    fun onClick(view: View, position: Int)
    //fun onLongClick(view: View, position: Int)
  }

  class RecyclerTouchListener : RecyclerView.OnItemTouchListener {
    private var clickListener: ClickListener
    private var gestureDetector: GestureDetector

    constructor(context: Context, recycleView: RecyclerView, clickListener: ClickListener) {
      this.clickListener = clickListener
      var noSAM = object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
          return true
        }
      }
      gestureDetector = GestureDetector(context, noSAM)
    }

    override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean {
      var child = rv!!.findChildViewUnder(e!!.x, e!!.y)
      if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
        clickListener.onClick(child, rv.getChildAdapterPosition(child))
      }
      return false
    }

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
  }
}