package rbdd.highton_android.Activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import rbdd.highton_android.Connect.Connector
import rbdd.highton_android.Fragment.ListFragement
import rbdd.highton_android.Fragment.MainFragement
import rbdd.highton_android.Manager.GoogleSignInManager
import rbdd.highton_android.Model.MyPAge
import rbdd.highton_android.R
import rbdd.highton_android.Util.BaseActivity
import rbdd.highton_android.Util.GlideUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d("item", item.itemId.toString())
        when (item.title) {
            "최근 알림" -> {
                goNextActivity(AlimActivity::class.java, false)
            }
            "활동 기록" -> {
                goNextActivity(HistoryActivity::class.java, false)
            }
            "비밀번호 변경" -> {

            }
            "로그아웃" -> {
                goNextActivity(LoginActivity::class.java, true)
            }
            "회원탈퇴" -> {

            }
        }
        return true
    }

    lateinit var googleSignIn: GoogleSignInManager
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, topBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        val navV= nav_view.inflateHeaderView(R.layout.nav_header_main)
        Connector.api.mypage(getCookie()).enqueue(object : Callback<MyPAge> {
            override fun onResponse(call: Call<MyPAge>?, response: Response<MyPAge>?) {
                if(response!!.isSuccessful) {
                    val navUserName = navV.findViewById<TextView>(R.id.userName)
                    navUserName.text = response.body()!!.name
                    val userEmail = navV.findViewById<TextView>(R.id.userEmail)
                    userEmail.text = response.body()!!.email
                }else {
                    Log.d("mypage","클라이언트 오류")
                }
            }

            override fun onFailure(call: Call<MyPAge>?, t: Throwable?) {
                Log.d("mypage","서버 오류")
            }

        })
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        GlideUtil.setGliding(this, R.drawable.ic_home_on, homeBtn)
        GlideUtil.setGliding(this, R.drawable.ic_list_off, listBtn)

        mainPager.adapter = MainPagerAdapter(supportFragmentManager)
        homeBtn.setOnClickListener {
            GlideUtil.setGliding(this, R.drawable.ic_list_off, listBtn)
            GlideUtil.setGliding(this, R.drawable.ic_home_on, homeBtn)
            mainPager.currentItem = 0
        }
        listBtn.setOnClickListener {
            GlideUtil.setGliding(this, R.drawable.ic_list_on, listBtn)
            GlideUtil.setGliding(this, R.drawable.ic_home_off, homeBtn)
            mainPager.currentItem = 1
        }
        mainPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        GlideUtil.setGliding(this@MainActivity, R.drawable.ic_list_off, listBtn)
                        GlideUtil.setGliding(this@MainActivity, R.drawable.ic_home_on, homeBtn)
                    }
                    1 -> {
                        GlideUtil.setGliding(this@MainActivity, R.drawable.ic_list_on, listBtn)
                        GlideUtil.setGliding(this@MainActivity, R.drawable.ic_home_off, homeBtn)
                    }
                }
            }

        })

    }

    class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        lateinit var fm: FragmentManager

        init {
            this.fm = fm
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> MainFragement(fm)
                1 -> ListFragement()
                else -> null
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getItemPosition(`object`: Any?): Int {
            return PagerAdapter.POSITION_NONE
        }

    }
}
