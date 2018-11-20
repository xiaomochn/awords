package com.xiaomo.funny.awords.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.taobao.weex.IWXRenderListener
import com.taobao.weex.WXSDKInstance
import com.taobao.weex.common.WXRenderStrategy
import com.umeng.analytics.MobclickAgent
import com.xiaomo.funny.awords.MyApp
import com.xiaomo.funny.awords.R
import com.xiaomo.funny.awords.util.ScreenUtil
import org.json.JSONObject
import java.util.*


/**
 * weex页面
 * */
class WXActivity : AppCompatActivity(), IWXRenderListener {
    internal var mWXSDKInstance: WXSDKInstance? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wx)
        mWXSDKInstance = WXSDKInstance(this)
        mWXSDKInstance!!.registerRenderListener(this)

        //远程路径
        var path = intent?.extras?.getString("url")
        var param = intent?.extras?.getString("param")
        var host = "http://10.5.6.243:1234/"
        if (param == null) {
            //data必须是json结构的字符串
            param = "{}"
        }
//            val host = "http://192.168.1.8:8081/"
//        http@ //10.5.119.243:1234/homevue/dist/
//            val host = "http://oqgi5s4fg.bkt.clouddn.com/homevue/"\
//        if (MyApp.getInstance().isDebug) {
//            host = "http://10.5.119.243:1234/homevue/dist/"
//        } else {
//            host = "http://financ.umoney.cc/aworld/app/v100/"
//        }

//            host = "http://financ.umoney.cc/"
//            val url = host + "dist/index.js"
//        host = "http://10.5.119.243:1234/homevue/dist/"
        if (path == null) {
            path = "dist/module/"
        }
//        val url = host + path + ".js"

            val url = "file://assets/dist/module/aworld/example.js"
        renderPageByURL(url, param)

    }


    protected fun renderPageByURL(url: String, jsonInitData: String?) {

        val options = HashMap<String, Any>()
        options.put(WXSDKInstance.BUNDLE_URL, url)
        mWXSDKInstance?.renderByUrl(
                "WXSample",
                url,
                options,
                jsonInitData,
                ScreenUtil.getDisplayWidth(this),
                ScreenUtil.getDisplayHeight(this),
                WXRenderStrategy.APPEND_ASYNC)
    }

    override fun onViewCreated(instance: WXSDKInstance, view: View) {
        setContentView(view)
    }

    override fun onRenderSuccess(instance: WXSDKInstance, width: Int, height: Int) {}
    override fun onRefreshSuccess(instance: WXSDKInstance, width: Int, height: Int) {}
    override fun onException(instance: WXSDKInstance, errCode: String, msg: String) {}
    override fun onResume() {
        super.onResume()
        if (mWXSDKInstance != null) {
            mWXSDKInstance!!.onActivityResume()
        }
        MyApp.getBus().register(this)
    }

    override fun onPause() {
        super.onPause()

        if (mWXSDKInstance != null) {
            mWXSDKInstance!!.onActivityPause()
        }
        MyApp.getBus().unregister(this)
    }

    override fun onStop() {
        super.onStop()
        if (mWXSDKInstance != null) {
            mWXSDKInstance!!.onActivityStop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mWXSDKInstance != null) {
            mWXSDKInstance!!.onActivityDestroy()
        }
    }
}