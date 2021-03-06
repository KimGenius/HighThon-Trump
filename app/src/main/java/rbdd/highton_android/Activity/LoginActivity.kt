package rbdd.highton_android.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import rbdd.highton_android.Connect.Connector
import rbdd.highton_android.Manager.GoogleSignInManager
import rbdd.highton_android.Model.Login
import rbdd.highton_android.R
import rbdd.highton_android.Util.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by root1 on 2017. 11. 5..
 */
class LoginActivity : BaseActivity() {


    private lateinit var googleSignIn: GoogleSignInManager

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setBtn()

    }

    private fun setBtn() {

        joinBtn.setOnClickListener {
            goNextActivity(MainActivity::class.java,true)
        }


        loginBtn.setOnClickListener {
            Connector.api.authBasic(idEdit.text.toString(), pwEdit.text.toString()).enqueue(object : Callback<Login> {
                override fun onResponse(call: Call<Login>?, response: Response<Login>?) {
                    if (response!!.isSuccessful) {
                        response.body().run {
                            Log.d("repsones", response.body()!!.access_token)
                            if (this!!.msg != "") {
                                showToast(this!!.msg)
                            } else {
                                saveCookie(response.body()!!.access_token)
                                goNextActivity(MainActivity::class.java, true)
                            }
                        }
                    } else {
                        showToast("로그인인 실패")
                    }
               }

                override fun onFailure(call: Call<Login>?, t: Throwable?) {
                    showToast("서버 연결 실패")
                }

            })
        }

        //val facebookSignin = FacebookSignInManager()
        //faceCall = CallbackManager.Factory.create()
        //facebookSignin.facebookSignIn(this@LoginActivity, facebookBtn, faceCall)
        googleBtn.setOnClickListener {
            googleSignIn = GoogleSignInManager(this@LoginActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("req", requestCode.toString())
        when (requestCode) {
            GOOGLESIGNINCODE -> {
                val account = googleSignIn.googleSignInResult(data!!)
                account?.let {
                    Connector.api.authGoogle(account?.id!!, account?.displayName!!, account?.email!!)
                            .enqueue(object : Callback<Login>{
                                override fun onFailure(call: Call<Login>?, t: Throwable?) {
                                    t?.printStackTrace()
                                }

                                override fun onResponse(call: Call<Login>?, response: Response<Login>?) {
                                    if (response?.code() == 201){
                                        saveCookie(response?.body()?.access_token!!)
                                        goNextActivity(MainActivity::class.java, true)
                                    }
                                }
                            })
                }
            }
            FACEBOOKSIGNCODE -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}