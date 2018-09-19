package com.xiaomo.funny.awords.weex.bll.common.module;


import android.content.Context;

import com.qingmei2.rximagepicker.entity.Result;
import com.qingmei2.rximagepicker.entity.sources.Camera;
import com.qingmei2.rximagepicker.entity.sources.Gallery;

import io.reactivex.Observable;

public interface MyImagePicker {

    @Gallery
        //打开相册选择图片
    Observable<Result> openGallery(Context context);

    @Camera
        //打开相机拍照
    Observable<Result> openCamera(Context context);
}