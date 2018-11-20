package com.xiaomo.funny.awords;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXDomObject;
import com.taobao.weex.dom.WXStyle;
import com.taobao.weex.ui.component.WXComponent;
import com.taobao.weex.ui.component.WXComponentProp;
import com.taobao.weex.ui.component.WXVContainer;
import com.taobao.weex.utils.WXResourceUtils;
import com.taobao.weex.utils.WXUtils;
import com.xiaomo.funny.awords.securitykeyboard.SecurityEditText;
import com.xiaomo.funny.awords.securitykeyboard.SecurityKeyboardManager;
import com.xiaomo.funny.awords.securitykeyboard.SecurityKeyboardUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zangrui on 2017/2/8.
 */

public class LKLSecurityEditText extends WXComponent<SecurityEditText> {

    private String securityName = "";

    /**
     * 密码键盘
     */
    private SecurityEditText mPassEdit;
    /**
     * 密码键盘管理类
     */
    private SecurityKeyboardManager manager;

    private final InputMethodManager mInputMethodManager;

    private String mLastValue = "";
    private String mBeforeText = "";

    public LKLSecurityEditText(WXSDKInstance instance, WXDomObject dom, WXVContainer parent) {
        super(instance, dom, parent);
        this.mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected SecurityEditText initComponentHostView(@NonNull Context context) {
        mPassEdit = new SecurityEditText(context);
        mPassEdit.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

        return this.mPassEdit;
    }

    @JSMethod
    public void managerDestroy() {
        if (manager != null) {
            manager.onDestory();
        }

    }

    @JSMethod
    public void passEditClear() {
        this.mPassEdit.clear();
    }

    @Override
    @JSMethod
    public void onActivityStop() {
        passEditClear();
        mPassEdit.clear();
        super.onActivityStop();
    }

    @Override
    @JSMethod
    public void onActivityDestroy() {
        mPassEdit.clear();
        mPassEdit.onDestroy();
        managerDestroy();
        super.onActivityDestroy();
    }

    @JSMethod
    public void focus() {
        SecurityEditText host = getHostView();
        if (host != null && !host.hasFocus()) {
            if (getParent() != null) {
                getParent().ignoreFocus();
            }
            host.requestFocus();
            host.setFocusable(true);
            host.setFocusableInTouchMode(true);
            showSoftKeyboard();
        }
    }

    @JSMethod
    public void blur() {
        SecurityEditText host = getHostView();
        if (host != null && host.hasFocus()) {
            if (getParent() != null) {
                getParent().interceptFocus();
            }
            host.clearFocus();
            hideSoftKeyboard();
        }
    }



    @Override
    protected boolean setProperty(String key, Object param) {
        switch (key) {
            case "securityName":
                String securityName ="passwordInput";
                if (securityName != null)
                    setSecurityName(securityName);
                return true;
            case Constants.Name.PLACEHOLDER:
                String placeholder = WXUtils.getString(param, null);
                if (placeholder != null)
                    setPlaceholder(placeholder);
                return true;
            case Constants.Name.PLACEHOLDER_COLOR:
                String placeholder_color = WXUtils.getString(param, null);
                if (placeholder_color != null)
                    setPlaceholderColor(placeholder_color);
                return true;
            case Constants.Name.COLOR:
                String color = WXUtils.getString(param, null);
                if (color != null)
                    setColor(color);
                return true;
            case "fontsize":
                String fontsize = WXUtils.getString(param, null);
                if (fontsize != null)
                    setFontSize(fontsize);
                return true;
        }
        return super.setProperty(key, param);
    }

    @WXComponentProp(name = "securityName")
    public void setSecurityName(String securityName) {
        if (securityName == null || getHostView() == null) {
            return;
        }
        this.securityName = securityName;
        mPassEdit.setSecurityManager(manager = SecurityKeyboardUtil.lklPassword(mPassEdit, securityName));
    }

    @WXComponentProp(name = Constants.Name.PLACEHOLDER)
    public void setPlaceholder(String placeholder) {
        if (placeholder == null || getHostView() == null) {
            return;
        }
        ((SecurityEditText) getHostView()).setHint(placeholder);
    }

    @WXComponentProp(name = Constants.Name.PLACEHOLDER_COLOR)
    public void setPlaceholderColor(String color) {
        if (getHostView() != null && !TextUtils.isEmpty(color)) {
            int colorInt = WXResourceUtils.getColor(color);
            if (colorInt != Integer.MIN_VALUE) {
                ((SecurityEditText) getHostView()).setHintTextColor(colorInt);
            }
        }
    }

    @WXComponentProp(name = Constants.Name.COLOR)
    public void setColor(String color) {
        if (getHostView() != null && !TextUtils.isEmpty(color)) {
            int colorInt = WXResourceUtils.getColor(color);
            if (colorInt != Integer.MIN_VALUE) {
                getHostView().setTextColor(colorInt);
            }
        }
    }

    @WXComponentProp(name = Constants.Name.FONT_SIZE)
    public void setFontSize(String fontSize) {
        if (getHostView() != null && fontSize != null) {
            getHostView().setTextSize(TypedValue.COMPLEX_UNIT_PX, WXStyle.getFontSize(getDomObject().getStyles(), getInstance().getViewPortWidth()));
        }
    }


    private void fireEvent(String event, String value) {
        if (event != null) {
            Map<String, Object> params = new HashMap<>(2);
            params.put("value", value);
            params.put("timeStamp", System.currentTimeMillis());

            Map<String, Object> domChanges = new HashMap<>();
            Map<String, Object> attrsChanges = new HashMap<>();
            attrsChanges.put("value", value);
            domChanges.put("attrs", attrsChanges);

            WXSDKManager.getInstance().fireEvent(getInstanceId(), getDomObject().getRef(), event, params, domChanges);
        }
    }

    private boolean showSoftKeyboard() {
        if (getHostView() == null) {
            return false;
        } else {
            getHostView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.showSoftInput(getHostView(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, 16);
        }
        return true;
    }

    private void hideSoftKeyboard() {
        if (getHostView() != null) {
            getHostView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.hideSoftInputFromWindow(getHostView().getWindowToken(), 0);
                }
            }, 16);
        }
    }


}
