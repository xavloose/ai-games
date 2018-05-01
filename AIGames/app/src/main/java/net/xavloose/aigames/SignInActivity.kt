package net.xavloose.aigames

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
  lateinit var mSignInButton: SignInButton
  lateinit var mGoogleApiClient: GoogleApiClient

  var mFirebaseAuth = FirebaseAuth.getInstance()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_in)

    mSignInButton = findViewById(R.id.sign_in_button)
    mSignInButton.setOnClickListener(this)

    var googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    mGoogleApiClient = GoogleApiClient.Builder(this)
        .enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
        .build()

    var mGuestSignIn = findViewById<Button>(R.id.guest)
    mGuestSignIn.setOnClickListener(View.OnClickListener {
      mFirebaseAuth.signInAnonymously()
          .addOnCompleteListener(this, { task ->
            if(task.isSuccessful) {
              startActivity(Intent(this@SignInActivity, MainActivity::class.java))
              finish()
            }
          })
    })
  }

  private fun firebaseAuthGoogle(acct: GoogleSignInAccount) {
    Log.d("SignInActivity", "firebaseAuthGoogle:" + acct.getId())
    var credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null)
    mFirebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this,  { task ->
          Log.d("SignInActivity", "signInWithCredential:onComplete:" + task.isSuccessful)
          if(!task.isSuccessful) {
            Log.w("SignInActivity", "signInWithCredential", task.exception)
            Toast.makeText(this@SignInActivity, "Authentication Failed.",
                Toast.LENGTH_SHORT).show()
          } else {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            finish()
          }
        })
  }

  private fun signIn() {
    var signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
    startActivityForResult(signInIntent, 9001)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 9001) {
      var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
      if(result.isSuccess) {
        var account = result.signInAccount as GoogleSignInAccount
        firebaseAuthGoogle(account)
      } else {
        Log.e("SignInActivity", "Google Sign-In Failed.")
      }
    }
  }

  override fun onClick(v: View) {
    if (v.id == R.id.sign_in_button)
      signIn()
  }

  override fun onConnectionFailed(connectionResult: ConnectionResult) {
    Log.d("SignInActivity", "onConnectionFailed:" + connectionResult)
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show()
  }
}