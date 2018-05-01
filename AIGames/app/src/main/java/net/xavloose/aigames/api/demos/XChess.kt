package net.xavloose.aigames.api.demos

import android.graphics.Color
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import net.xavloose.aigames.R
import net.xavloose.aigames.api.games.Game
import kotlin.math.absoluteValue

class XChess : Game {
  override val layout = R.layout.fragment_xchess
  private var pressed = false
  private var pressedButton: Int = -1
  private lateinit var exitCallback: () -> Unit

  private var p1ai: Boolean = false
  private lateinit var p1aiUri: String
  private var p2ai: Boolean = false
  private lateinit var p2aiUri: String

  private var game = FEN()
  private var wKing = 4
  private var bKing = 60
  private var wCheckState = 0
  private var bCheckState = 0
  private var buttonArray = IntArray(64)
  private var locNotation = Array<String>(64, {""})

  init {
    buttonArray[0]=R.id.button1A; buttonArray[1]=R.id.button1B; buttonArray[2]=R.id.button1C
    buttonArray[3]=R.id.button1D; buttonArray[4]=R.id.button1E; buttonArray[5]=R.id.button1F
    buttonArray[6]=R.id.button1G; buttonArray[7]=R.id.button1H; buttonArray[8]=R.id.button2A
    buttonArray[9]=R.id.button2B; buttonArray[10]=R.id.button2C; buttonArray[11]=R.id.button2D
    buttonArray[12]=R.id.button2E; buttonArray[13]=R.id.button2F; buttonArray[14]=R.id.button2G
    buttonArray[15]=R.id.button2H; buttonArray[16]=R.id.button3A; buttonArray[17]=R.id.button3B
    buttonArray[18]=R.id.button3C; buttonArray[19]=R.id.button3D; buttonArray[20]=R.id.button3E
    buttonArray[21]=R.id.button3F; buttonArray[22]=R.id.button3G; buttonArray[23]=R.id.button3H
    buttonArray[24]=R.id.button4A; buttonArray[25]=R.id.button4B; buttonArray[26]=R.id.button4C
    buttonArray[27]=R.id.button4D; buttonArray[28]=R.id.button4E; buttonArray[29]=R.id.button4F
    buttonArray[30]=R.id.button4G; buttonArray[31]=R.id.button4H; buttonArray[32]=R.id.button5A
    buttonArray[33]=R.id.button5B; buttonArray[34]=R.id.button5C; buttonArray[35]=R.id.button5D
    buttonArray[36]=R.id.button5E; buttonArray[37]=R.id.button5F; buttonArray[38]=R.id.button5G
    buttonArray[39]=R.id.button5H; buttonArray[40]=R.id.button6A; buttonArray[41]=R.id.button6B
    buttonArray[42]=R.id.button6C; buttonArray[43]=R.id.button6D; buttonArray[44]=R.id.button6E
    buttonArray[45]=R.id.button6F; buttonArray[46]=R.id.button6G; buttonArray[47]=R.id.button6H
    buttonArray[48]=R.id.button7A; buttonArray[49]=R.id.button7B; buttonArray[50]=R.id.button7C
    buttonArray[51]=R.id.button7D; buttonArray[52]=R.id.button7E; buttonArray[53]=R.id.button7F
    buttonArray[54]=R.id.button7G; buttonArray[55]=R.id.button7H; buttonArray[56]=R.id.button8A
    buttonArray[57]=R.id.button8B; buttonArray[58]=R.id.button8C; buttonArray[59]=R.id.button8D
    buttonArray[60]=R.id.button8E; buttonArray[61]=R.id.button8F; buttonArray[62]=R.id.button8G
    buttonArray[63]=R.id.button8H
    locNotation[0]="a1"; locNotation[1]="b1"; locNotation[2]="c1"; locNotation[3]="d1"
    locNotation[4]="e1"; locNotation[5]="f1"; locNotation[6]="g1"; locNotation[7]="h1"
    locNotation[8]="a2"; locNotation[9]="b2"; locNotation[10]="c2"; locNotation[11]="d2"
    locNotation[12]="e2"; locNotation[13]="f2"; locNotation[14]="g2"; locNotation[15]="h2"
    locNotation[16]="a3"; locNotation[17]="b3"; locNotation[18]="c3"; locNotation[19]="d3"
    locNotation[20]="e3"; locNotation[21]="f3"; locNotation[22]="g3"; locNotation[23]="h3"
    locNotation[24]="a4"; locNotation[25]="b4"; locNotation[26]="c4"; locNotation[27]="d4"
    locNotation[28]="e4"; locNotation[29]="f4"; locNotation[30]="g4"; locNotation[31]="h4"
    locNotation[32]="a5"; locNotation[33]="b5"; locNotation[34]="c5"; locNotation[35]="d5"
    locNotation[36]="e5"; locNotation[37]="f5"; locNotation[38]="g5"; locNotation[39]="h5"
    locNotation[40]="a6"; locNotation[41]="b6"; locNotation[42]="c6"; locNotation[43]="d6"
    locNotation[44]="e6"; locNotation[45]="f6"; locNotation[46]="g6"; locNotation[47]="h6"
    locNotation[48]="a7"; locNotation[49]="b7"; locNotation[50]="c7"; locNotation[51]="d7"
    locNotation[52]="e7"; locNotation[53]="f7"; locNotation[54]="g7"; locNotation[55]="h7"
    locNotation[56]="a8"; locNotation[57]="b8"; locNotation[58]="c8"; locNotation[59]="d8"
    locNotation[60]="e8"; locNotation[61]="f8"; locNotation[62]="g8"; locNotation[63]="h8"
  }

  class FEN {
    var boardString = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
    var activeMove = 'w'
    var castlingMode = "-" //TODO IMPLEMENT
    var enPassant = "-" //TODO IMPLEMENT
    var halfMoveClock = '0' //TODO IMPLEMENT
    var fullMoveClock = '1' //TODO IMPLEMENT
  }

  override fun startGame(view: View, p1ai: Boolean, p1aiUri: String, p2ai: Boolean, p2aiUri: String,
                         callback: () -> Unit) {
    this.p1ai = p1ai
    this.p2ai = p2ai
    this.p1aiUri = p1aiUri
    this.p2aiUri = p2aiUri

    drawBoard(view)

    view.findViewById<Button>(R.id.saveButton).setOnClickListener(View.OnClickListener {
      var mFirebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
          .child(FirebaseAuth.getInstance().currentUser!!.uid).child("xchess")
      mFirebaseDatabase.child("saved").push().child("game").setValue(game.boardString + ":" + game.activeMove.toString() +
      ":" + game.castlingMode + ":" + game.enPassant + ":" + game.halfMoveClock + ":" + game.fullMoveClock)
      callback()
    })
    view.findViewById<Button>(R.id.giveUpButton).setOnClickListener(View.OnClickListener {
      var mFirebaseDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
          .child(FirebaseAuth.getInstance().currentUser!!.uid).child("xchess")
      mFirebaseDatabase.child("end").push().child("game").setValue("L:p1:p2:" + game.boardString + ":" + game.activeMove.toString() +
          ":" + game.castlingMode + ":" + game.enPassant + ":" + game.halfMoveClock + ":" + game.fullMoveClock)
      callback()
    })
    exitCallback = callback

    if (!this.p1ai) {
      for (i in 0..63)
        view.findViewById<Button>(buttonArray[i]).setOnClickListener(View.OnClickListener {
          processButton(view, it.id)
        })
    } else {
      //TODO get ai move
    }
  }

  private fun drawBoard(view: View) {
    var i = 0
    var loc = 0
    while (i < game.boardString.length) {
      if (game.boardString[i].isDigit()) {
        for (j in 0.until(game.boardString[i].toInt() - '0'.toInt())) {
          view.findViewById<Button>(buttonArray[loc]).text = ""
          loc++
        }
      } else if (game.boardString[i] == '/') {
      } else {
        view.findViewById<Button>(buttonArray[loc]).text = game.boardString[i].toString()
        loc++
      }
      i++
    }
  }

  private fun updateFEN(view: View): String {
    var board= ""
    var i = 0
    var counter = 0
    while (i < 64) {
      board += view.findViewById<Button>(buttonArray[i]).text
      while (view.findViewById<Button>(buttonArray[i]).text == "" && (i% 8 != 0 || (counter == 0 && i%8 == 0))) {
        if(i% 8 != 0 || (counter == 0 && i%8 == 0)) {
          counter++
          i++
        }
      }
      if (counter > 0) {
        board += counter.toString()
        counter = 0
        i-- // We have to step 1 too far to exit the previous loop
      }
      i++
      if (i%8 == 0 && i < 64) board += "/"
    }
    return board
  }

  private fun processButton(view: View, id: Int) {
    if (pressed) {
      if (pressedButton == id) {
        pressed = false
        view.findViewById<Button>(id).setTextColor(Color.BLACK)
      } else {
        when (view.findViewById<Button>(pressedButton).text[0].toUpperCase()) {
          'P' -> checkPawnMove(view, pressedButton, id)
          'R' -> checkRookMove(view, pressedButton, id)
          'N' -> checkKnightMove(view, pressedButton, id)
          'B' -> checkBishopMove(view, pressedButton, id)
          'Q' -> checkQueenMove(view, pressedButton, id)
          'K' -> checkKingMove(view, pressedButton, id)
        }
      }
    } else {
      if ((view.findViewById<Button>(id).text != "") &&
          ((view.findViewById<Button>(id).text[0].isUpperCase() && game.activeMove == 'b') ||
          (view.findViewById<Button>(id).text[0].isLowerCase() && game.activeMove == 'w'))) {
        pressed = true
        pressedButton = id
        view.findViewById<Button>(id).setTextColor(Color.RED)
      }
    }
    val newFENBoard = updateFEN(view)
    if (newFENBoard != game.boardString) {
      game.boardString = newFENBoard
      if (game.activeMove == 'w' && p1ai) {
        //get ai move
        for (i in 0..63) view.findViewById<Button>(buttonArray[i]).setOnClickListener(null)
      } else if (game.activeMove == 'w' && !p1ai) {
        for (i in 0..63)
          view.findViewById<Button>(buttonArray[i]).setOnClickListener(View.OnClickListener {
            processButton(view, it.id)
          })
      } else if (game.activeMove == 'b' && p2ai) {
        //get ai move
        for (i in 0..63) view.findViewById<Button>(buttonArray[i]).setOnClickListener(null)
      } else {
        for (i in 0..63)
          view.findViewById<Button>(buttonArray[i]).setOnClickListener(View.OnClickListener {
            processButton(view, it.id)
          })
      }
    }
  }

  private fun checkPawnMove(view: View, from: Int, to: Int) {
    val fromIdx = buttonArray.indexOf(pressedButton)
    val toIdx = buttonArray.indexOf(to)
    val home = ((fromIdx/8)%8 == 1 || (fromIdx/8)%8 == 6)
    if (view.findViewById<Button>(from).text[0].isLowerCase()) {
      // Generic Move
      if ( toIdx % 8 == fromIdx % 8 && // same column
          ((home && toIdx/8-fromIdx/8 == 2 && toIdx/8-fromIdx/8 > 0) || // 0 to 2 Moves Forward if Home
          (toIdx/8-fromIdx/8 > 0 && toIdx/8-fromIdx/8 == 1)) && // 0 to 1 Moves Forward)
          (isFree(view, fromIdx, toIdx + 8))) {
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        view.findViewById<Button>(to).text = "p"
        game.activeMove = 'b'
        pressed = false
      } else if (toIdx/8 - fromIdx/8 == 1 && (toIdx%8 - fromIdx%8).absoluteValue == 1 &&
          view.findViewById<Button>(buttonArray[toIdx]).text[0].isUpperCase()) {
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        view.findViewById<Button>(to).text = "p"
        game.activeMove = 'b'
        pressed = false
      }
    } else {
      // Generic Move
      if ( toIdx % 8 == fromIdx % 8 && // same column
          ((home && toIdx/8-fromIdx/8 == -2 && toIdx/8-fromIdx/8 < 0) || // 0 to 2 Moves Forward if Home
          (toIdx/8-fromIdx/8 < 0 && toIdx/8-fromIdx/8 == -1)) && // 0 to 1 Moves Forward)
          (isFree(view, fromIdx, toIdx - 8))) {
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        view.findViewById<Button>(to).text = "P"
        game.activeMove = 'w'
        pressed = false
      } else if (toIdx/8 - fromIdx/8 == -1 && (toIdx%8 - fromIdx%8).absoluteValue == 1 &&
          view.findViewById<Button>(buttonArray[toIdx]).text[0].isLowerCase()) {
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        view.findViewById<Button>(to).text = "P"
        game.activeMove = 'w'
        pressed = false
      }
    }
  }

  private fun checkRookMove(view: View, from: Int, to: Int) {
    val fromIdx = buttonArray.indexOf(pressedButton)
    val toIdx = buttonArray.indexOf(to)
    if((fromIdx/8-toIdx/8 == 0 || fromIdx%8 - toIdx%8 == 0) && isFree(view, fromIdx, toIdx)) {
      if (view.findViewById<Button>(to).text == "" ||
          view.findViewById<Button>(from).text[0].isLowerCase() != view.findViewById<Button>(to).text[0].isLowerCase()) {
        view.findViewById<Button>(to).text = view.findViewById<Button>(from).text
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        if (game.activeMove == 'w')
          game.activeMove = 'b'
        else
          game.activeMove = 'w'
        pressed = false
      }
    }
  }

  private fun checkKnightMove(view: View, from: Int, to: Int) {
    val fromIdx = buttonArray.indexOf(pressedButton)
    val toIdx = buttonArray.indexOf(to)
    if (((fromIdx/8 - toIdx/8).absoluteValue == 1 && (fromIdx%8 - toIdx%8).absoluteValue == 2) ||
        ((fromIdx/8 - toIdx/8).absoluteValue == 2 && (fromIdx%8 - toIdx%8).absoluteValue == 1)) {
      if (view.findViewById<Button>(to).text == "" ||
          view.findViewById<Button>(from).text[0].isLowerCase() != view.findViewById<Button>(to).text[0].isLowerCase()) {
        view.findViewById<Button>(to).text = view.findViewById<Button>(from).text
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        if (game.activeMove == 'w') {
          wKing = toIdx
          game.activeMove = 'b'
        } else {
          bKing = toIdx
          game.activeMove = 'w'
        }
        pressed = false
      }
    }
  }

  private fun checkBishopMove(view: View, from: Int, to: Int) {
    val fromIdx = buttonArray.indexOf(pressedButton)
    val toIdx = buttonArray.indexOf(to)
    if((fromIdx/8-toIdx/8).absoluteValue == (fromIdx%8 - toIdx%8).absoluteValue && isFree(view, fromIdx, toIdx)) {
      if (view.findViewById<Button>(to).text == "" ||
          view.findViewById<Button>(from).text[0].isLowerCase() != view.findViewById<Button>(to).text[0].isLowerCase()) {
        view.findViewById<Button>(to).text = view.findViewById<Button>(from).text
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        if (game.activeMove == 'w')
          game.activeMove = 'b'
        else
          game.activeMove = 'w'
        pressed = false
      }
    }
  }

  private fun checkQueenMove(view: View, from: Int, to: Int) {
    checkRookMove(view, from, to)
    checkBishopMove(view, from, to)
  }

  private fun checkKingMove(view: View, from: Int, to: Int) {
    val fromIdx = buttonArray.indexOf(pressedButton)
    val toIdx = buttonArray.indexOf(to)
    if ((fromIdx/8 - toIdx/8).absoluteValue <= 1 && (fromIdx%8 - toIdx%8).absoluteValue <= 1) {
      if ((view.findViewById<Button>(to).text == "" ||
          view.findViewById<Button>(from).text[0].isLowerCase() != view.findViewById<Button>(to).text[0].isLowerCase()) &&
          !inCheck(view.findViewById<Button>(from).text[0], toIdx, view)) {
        view.findViewById<Button>(to).text = view.findViewById<Button>(from).text
        view.findViewById<Button>(from).setTextColor(Color.BLACK)
        view.findViewById<Button>(from).text = ""
        if (game.activeMove == 'w')
          game.activeMove = 'b'
        else
          game.activeMove = 'w'
        pressed = false
      }
    }
  }

  private fun isFree(view: View, from: Int, to: Int): Boolean {
    if (from%8 == to%8 && from/8 != to/8) {
      val diff = to/8 - from/8
      for (i in 1..(diff.absoluteValue - 1)) {
        if (view.findViewById<Button>(buttonArray[from + i * (diff/diff.absoluteValue) * 8]).text != "")
          return false
      }
    } else if (from/8 == to/8 && from%8 != to%8) {
      val diff = to%8 - from%8
      for (i in 1..(diff.absoluteValue - 1)) {
        if (view.findViewById<Button>(buttonArray[from + i * (diff / diff.absoluteValue)]).text != "")
          return false
      }
    } else if ((from/8-to/8).absoluteValue == (from%8 - to%8).absoluteValue) {
      val vDiff = to/8-from/8
      val hDiff = to%8-from%8
      for (i in 1..(vDiff.absoluteValue - 1)) {
        if (view.findViewById<Button>(buttonArray[from + i * (vDiff/vDiff.absoluteValue) * 8 + i * (hDiff / hDiff.absoluteValue)]).text != "")
          return false
      }
    } else {
      return false
    }
    return true
  }

  private fun updateCheckStates(view: View) {
    if (inCheck('K', bKing, view))
      bCheckState = 1
    else
      bCheckState = 0
    if (inCheck('k', wKing, view))
      wCheckState = 1
    else
      wCheckState = 0

    if (bCheckState == 1 &&
        (bKing + 8 >= 64 || inCheck('K', bKing + 8, view)) &&
        (bKing - 8 <  0  || inCheck('K', bKing - 8, view)) &&
        ((bKing+1)%8== 0 || inCheck('K', bKing + 1, view)) &&
        ((bKing-1)%8== 7 || inCheck('K', bKing - 1, view)) &&
        ((bKing + 8 >= 64 && (bKing+1)%8== 0) || inCheck('K', bKing + 9, view)) &&
        ((bKing - 8 >= 64 && (bKing+1)%8== 0) || inCheck('K', bKing - 7, view)) &&
        ((bKing - 8 >= 64 && (bKing-1)%8== 7) || inCheck('K', bKing - 9, view)) &&
        ((bKing + 8 >= 64 && (bKing-1)%8== 7) || inCheck('K', bKing + 7, view)))
      bCheckState = 2
    if (wCheckState == 1 &&
        (wKing + 8 >= 64 || inCheck('k', wKing + 8, view)) &&
        (wKing - 8 <  0  || inCheck('k', wKing - 8, view)) &&
        ((wKing+1)%8== 0 || inCheck('k', wKing + 1, view)) &&
        ((wKing-1)%8== 7 || inCheck('k', wKing - 1, view)) &&
        ((wKing + 8 >= 64 && (wKing+1)%8== 0) || inCheck('k', wKing + 9, view)) &&
        ((wKing - 8 >= 64 && (wKing+1)%8== 0) || inCheck('k', wKing - 7, view)) &&
        ((wKing - 8 >= 64 && (wKing-1)%8== 7) || inCheck('k', wKing - 9, view)) &&
        ((wKing + 8 >= 64 && (wKing-1)%8== 7) || inCheck('k', wKing + 7, view)))
      wCheckState = 2
  }

  private fun inCheck(king: Char, loc: Int, view: View): Boolean {
    // Knight Checks +-6 +-10 +-15 +-17
    when (loc % 8) { // 2 5 1 6
      2 -> {
        if ( loc + 6 < 64 &&
            view.findViewById<Button>(buttonArray[loc + 6]).text != "" &&
            view.findViewById<Button>(buttonArray[loc + 6]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc + 6]).text[0].isUpperCase() != king.isUpperCase())
          return true
        if ( loc - 10 < 64 &&
            view.findViewById<Button>(buttonArray[loc - 10]).text != "" &&
            view.findViewById<Button>(buttonArray[loc - 10]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc - 10]).text[0].isUpperCase() != king.isUpperCase())
          return true
      }
      5 -> {
        if ( loc - 6 < 64 &&
            view.findViewById<Button>(buttonArray[loc - 6]).text != "" &&
            view.findViewById<Button>(buttonArray[loc - 6]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc - 6]).text[0].isUpperCase() != king.isUpperCase())
          return true
        if ( loc + 10 < 64 &&
            view.findViewById<Button>(buttonArray[loc + 10]).text != "" &&
            view.findViewById<Button>(buttonArray[loc + 10]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc + 10]).text[0].isUpperCase() != king.isUpperCase())
          return true
      }
      1 -> {
        if ( loc + 15 < 64 &&
            view.findViewById<Button>(buttonArray[loc + 15]).text != "" &&
            view.findViewById<Button>(buttonArray[loc + 15]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc + 15]).text[0].isUpperCase() != king.isUpperCase())
          return true
        if ( loc - 17 >= 0 &&
            view.findViewById<Button>(buttonArray[loc - 17]).text != "" &&
            view.findViewById<Button>(buttonArray[loc - 17]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc - 17]).text[0].isUpperCase() != king.isUpperCase())
          return true
      }
      6 -> {
        if (loc + 17 < 64 &&
            view.findViewById<Button>(buttonArray[loc + 17]).text != "" &&
            view.findViewById<Button>(buttonArray[loc + 17]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc + 17]).text[0].isUpperCase() != king.isUpperCase())
          return true
        if (loc - 15 >= 0 &&
            view.findViewById<Button>(buttonArray[loc - 15]).text != "" &&
            view.findViewById<Button>(buttonArray[loc - 15]).text[0].toUpperCase() == 'N' &&
            view.findViewById<Button>(buttonArray[loc - 15]).text[0].isUpperCase() != king.isUpperCase())
          return true
      }
    }

    var up = 1
    while (loc + up * 8 < 64) {
      if (view.findViewById<Button>(buttonArray[loc+up*8]).text != "" &&
          view.findViewById<Button>(buttonArray[loc+up*8]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc+up*8]).text[0].toUpperCase() == 'R' ||
          view.findViewById<Button>(buttonArray[loc+up*8]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc+up*8]).text != "") break
      up++
    }

    var down = 1
    while (loc - down * 8 >= 0) {
      if (view.findViewById<Button>(buttonArray[loc-down*8]).text != "" &&
          view.findViewById<Button>(buttonArray[loc-down*8]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc-down*8]).text[0].toUpperCase() == 'R' ||
          view.findViewById<Button>(buttonArray[loc-down*8]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc-down*8]).text != "") break
      down++
    }

    var left = 1
    while ((loc - left)%8 >= 0) {
      if (view.findViewById<Button>(buttonArray[loc-left]).text != "" &&
          view.findViewById<Button>(buttonArray[loc-left]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc-left]).text[0].toUpperCase() == 'R' ||
          view.findViewById<Button>(buttonArray[loc-left]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc-left]).text != "") break
      left++
    }

    var right = 1
    while ((loc+right)%8 <= 7) {
      if (view.findViewById<Button>(buttonArray[loc+right]).text != "" &&
          view.findViewById<Button>(buttonArray[loc+right]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc+right]).text[0].toUpperCase() == 'R' ||
          view.findViewById<Button>(buttonArray[loc+right]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc+right]).text != "") break
      right++
    }

    var NE = 1
    while ((loc + NE)%8 <= 7 && (loc + NE*8) < 64) {
      if (view.findViewById<Button>(buttonArray[loc+NE*9]).text != "" &&
          view.findViewById<Button>(buttonArray[loc+NE*9]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc+NE*9]).text[0].toUpperCase() == 'B' ||
          view.findViewById<Button>(buttonArray[loc+NE*9]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc+NE*9]).text != "") break
      NE++
    }

    var SE = 1
    while ((loc + SE)%8 <= 7 && (loc - SE*8) >= 0) {
      if (view.findViewById<Button>(buttonArray[loc-SE*7]).text != "" &&
          view.findViewById<Button>(buttonArray[loc-SE*7]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc-SE*7]).text[0].toUpperCase() == 'B' ||
          view.findViewById<Button>(buttonArray[loc-SE*7]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc-SE*7]).text != "") break
      SE++
    }

    var SW = 1
    while ((loc - SW)%8 >= 0 && (loc - SW*8) >= 0) {
      if (view.findViewById<Button>(buttonArray[loc-SW*9]).text != "" &&
          view.findViewById<Button>(buttonArray[loc-SW*9]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc-SW*9]).text[0].toUpperCase() == 'B' ||
          view.findViewById<Button>(buttonArray[loc-SW*9]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc-SW*9]).text != "") break
      SW++
    }

    var NW = 1
    while ((loc - NW)%8 >= 0 && (loc + NW*8) < 64) {
      if (view.findViewById<Button>(buttonArray[loc+NW*7]).text != "" &&
          view.findViewById<Button>(buttonArray[loc+NW*7]).text[0].isUpperCase() != king.isUpperCase() &&
          (view.findViewById<Button>(buttonArray[loc+NW*7]).text[0].toUpperCase() == 'B' ||
          view.findViewById<Button>(buttonArray[loc+NW*7]).text[0].toUpperCase() == 'Q'))
        return true
      if (view.findViewById<Button>(buttonArray[loc+NW*7]).text != "") break
      NW++
    }
    return false
  }
}