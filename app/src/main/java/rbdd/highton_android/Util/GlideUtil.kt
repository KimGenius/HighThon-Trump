package rbdd.highton_android.Util

import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Created by young on 2017-11-05/오전 1:02
 * This Project is HighThon-Trump
 */
object GlideUtil {
    fun setGliding(context: View?, img: Int, view: ImageView) {
        Glide.with(context).load(img).into(view)
    }
    fun setGliding(context: Activity?, img: Int, view: ImageView) {
        Glide.with(context).load(img).into(view)
    }
}