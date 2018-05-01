package net.xavloose.aigames.api.games

import android.content.Context
import android.os.Bundle
import android.renderscript.Sampler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import net.xavloose.aigames.R
import net.xavloose.aigames.api.ai.AI
import net.xavloose.aigames.api.ai.VolleySingleton
import net.xavloose.aigames.api.demos.XChess

class GameActivity: AppCompatActivity() {
  private lateinit var mAdView: AdView
  private lateinit var mContext: Context
  private var gameStates = ArrayList<GameState>()
  private lateinit var mVolleySingleton :VolleySingleton
  private var xChess = XChess()

  data class GameState(
    val p1ai: Boolean = false,
    val p1aiUrl: String = "",
    var p2ai: Boolean = false,
    var p2aiUrl: String = "",
    var state: String = ""
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_api_game)

    var mFirebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("xchess")
    val gameListener = object : ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        p0.children.mapNotNullTo(gameStates) {it.getValue<GameState>(GameState::class.java)}
      }
      override fun onCancelled(p0: DatabaseError?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
      }
    }
    mFirebaseDatabase.addListenerForSingleValueEvent(gameListener)


    mContext = applicationContext
    mVolleySingleton = VolleySingleton(mContext)

    mAdView = findViewById(R.id.gAdView)
    var adRequest = AdRequest.Builder().build()
    mAdView.loadAd(adRequest)

    var inflater = layoutInflater
    var gameContainer = findViewById<FrameLayout>(R.id.game_view)
    var questionLayout = inflater.inflate(R.layout.fragment_game_question, null)

    questionLayout.findViewById<Button>(R.id.button3).setOnClickListener(View.OnClickListener {
      if (it.id == R.id.button3) {
        gameContainer.removeAllViewsInLayout()
        var gameLayout = inflater.inflate(xChess.layout, null)
        gameContainer.addView(gameLayout, 0)
        xChess.startGame(gameContainer, questionLayout.findViewById<Switch>(R.id.switch1).isChecked,
            questionLayout.findViewById<EditText>(R.id.editText).text.toString(),
            questionLayout.findViewById<Switch>(R.id.switch2).isChecked,
            questionLayout.findViewById<EditText>(R.id.editText2).text.toString(), {
          gameContainer.removeAllViewsInLayout()
          gameContainer.addView(questionLayout, 0)
        })
      }
    })

    questionLayout.findViewById<Button>(R.id.button4).setOnClickListener(View.OnClickListener {
      if (it.id == R.id.button4) Toast.makeText(mContext, "Not Implemented Yet :(", Toast.LENGTH_SHORT).show()
    })

    questionLayout.findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
      if (it.id == R.id.button) finish()
    })

    gameContainer.addView(questionLayout, 0)
  }
}