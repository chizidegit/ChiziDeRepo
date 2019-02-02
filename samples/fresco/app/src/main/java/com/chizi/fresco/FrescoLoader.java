package com.chizi.fresco;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class FrescoLoader {
    private Context mContext;
    private boolean mCompatTemporaryDetach;
    private DraweeHolderDispatcher mDraweeHolderDispatcher;
    private DraweeHolder<DraweeHierarchy> mDraweeHolder;
    private Postprocessor mPostprocessor;
    private ControllerListener mControllerListener;

    private Uri mUri;
    private Uri mLowerUri;
    private ResizeOptions mResizeOptions;
    private float mDesiredAspectRatio;
    private boolean mUseFixedWidth;
    private boolean mAutoRotateEnabled = false;

    private int mFadeDuration;
    private Drawable mPlaceholderDrawable;
    private Drawable mRetryDrawable;
    private Drawable mFailureDrawable;
    private Drawable mProgressBarDrawable;
    private Drawable mBackgroundDrawable;

    private ScalingUtils.ScaleType mPlaceholderScaleType;
    private ScalingUtils.ScaleType mRetryScaleType;
    private ScalingUtils.ScaleType mFailureScaleType;
    private ScalingUtils.ScaleType mProgressScaleType;

    private ScalingUtils.ScaleType mActualImageScaleType;
    private PointF mActualImageFocusPoint;
    private ColorFilter mActualImageColorFilter;
    private RoundingParams mRoundingParams;

    private List<Drawable> mOverlays;
    private Drawable mPressedStateOverlay;

    private boolean mTapToRetryEnabled;
    private boolean mAutoPlayAnimations;
    private boolean mRetainImageOnFailure;
    private boolean mProgressiveRenderingEnabled;
    private boolean mLocalThumbnailPreviewsEnabled;

    private FrescoLoader(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDraweeHolderDispatcher = null;

        this.mDesiredAspectRatio = 0;
        this.mUseFixedWidth = true;
        this.mFadeDuration = GenericDraweeHierarchyBuilder.DEFAULT_FADE_DURATION;

        this.mPlaceholderDrawable = null;
        this.mPlaceholderScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;

        this.mRetryDrawable = null;
        this.mRetryScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;

        this.mFailureDrawable = null;
        this.mFailureScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;

        this.mProgressBarDrawable = null;
        this.mProgressScaleType = GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE;

        this.mActualImageScaleType = GenericDraweeHierarchyBuilder.DEFAULT_ACTUAL_IMAGE_SCALE_TYPE;
        this.mActualImageFocusPoint = null;
        this.mActualImageColorFilter = null;

        this.mBackgroundDrawable = null;
        this.mOverlays = null;
        this.mPressedStateOverlay = null;
        this.mRoundingParams = null;

        this.mTapToRetryEnabled = false;
        this.mAutoPlayAnimations = false;
        this.mRetainImageOnFailure = false;
        this.mProgressiveRenderingEnabled = false;
        this.mLocalThumbnailPreviewsEnabled = false;

        this.mPostprocessor = null;
        this.mControllerListener = null;
        this.mDraweeHolder = null;
    }


    public boolean hasHierarchy() {
        if (mDraweeHolder != null) {
            return mDraweeHolder.hasHierarchy();
        }
        return false;
    }

    public DraweeHierarchy getHierarchy() {
        if (mDraweeHolder != null) {
            return mDraweeHolder.getHierarchy();
        }
        return null;
    }

    public DraweeController getController() {
        if (mDraweeHolder != null) {
            return mDraweeHolder.getController();
        }
        return null;
    }

    public boolean hasController() {
        if (mDraweeHolder != null) {
            return mDraweeHolder.getController() != null;
        }
        return false;
    }

    public static FrescoLoader with(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        return new FrescoLoader(context);
    }

    public FrescoLoader load(Uri uri) {
        this.mUri = uri;
        return this;
    }

    public FrescoLoader load(String uri) {
        return load(Uri.parse(uri));
    }

    public FrescoLoader load(File file) {
        return load(Uri.fromFile(file));
    }

    public FrescoLoader load(int resourceId) {
        return load(
                new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(resourceId))
                        .build()
        );
    }

    public FrescoLoader lowerLoad(Uri uri) {
        this.mLowerUri = uri;
        return this;
    }

    public FrescoLoader lowerLoad(String uri) {
        return lowerLoad(Uri.parse(uri));
    }

    public FrescoLoader lowerLoad(File file) {
        return lowerLoad(Uri.fromFile(file));
    }

    public FrescoLoader lowerLoad(int resourceId) {
        return lowerLoad(
                new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                        .path(String.valueOf(resourceId))
                        .build()
        );
    }

    public FrescoLoader placeholder(Drawable placeholderDrawable) {
        this.mPlaceholderDrawable = placeholderDrawable;
        return this;
    }

    public FrescoLoader placeholder(int placeholderResId) {
        return placeholder(mContext.getResources().getDrawable(placeholderResId));
    }

    public FrescoLoader placeholderScaleType(ImageView.ScaleType scaleType) {
        this.mPlaceholderScaleType = convertToFrescoScaleType(scaleType, GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE);
        return this;
    }

    public FrescoLoader retry(Drawable retryDrawable) {
        this.mRetryDrawable = retryDrawable;
        return this;
    }

    public FrescoLoader retry(int retryResId) {
        return retry(mContext.getResources().getDrawable(retryResId));
    }

    public FrescoLoader retryScaleType(ImageView.ScaleType scaleType) {
        this.mRetryScaleType = convertToFrescoScaleType(scaleType, GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE);
        return this;
    }

    public FrescoLoader failure(Drawable failureDrawable) {
        this.mFailureDrawable = failureDrawable;
        return this;
    }

    public FrescoLoader failure(int failureResId) {
        return failure(mContext.getResources().getDrawable(failureResId));
    }

    public FrescoLoader failureScaleType(ImageView.ScaleType scaleType) {
        this.mFailureScaleType = convertToFrescoScaleType(scaleType, GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE);
        return this;
    }

    public FrescoLoader progressBar(Drawable placeholderDrawable) {
        this.mProgressBarDrawable = placeholderDrawable;
        return this;
    }

    public FrescoLoader progressBar(int progressResId) {
        return progressBar(mContext.getResources().getDrawable(progressResId));
    }

    public FrescoLoader progressBarScaleType(ImageView.ScaleType scaleType) {
        this.mPlaceholderScaleType = convertToFrescoScaleType(scaleType, GenericDraweeHierarchyBuilder.DEFAULT_SCALE_TYPE);
        return this;
    }

    public FrescoLoader backgroundDrawable(Drawable backgroundDrawable) {
        this.mBackgroundDrawable = backgroundDrawable;
        return this;
    }

    public FrescoLoader backgroundDrawable(int backgroundResId) {
        return backgroundDrawable(mContext.getResources().getDrawable(backgroundResId));
    }

    public FrescoLoader actualScaleType(ImageView.ScaleType scaleType) {
        this.mActualImageScaleType = convertToFrescoScaleType(scaleType, GenericDraweeHierarchyBuilder.DEFAULT_ACTUAL_IMAGE_SCALE_TYPE);
        return this;
    }

    public FrescoLoader focusPoint(PointF focusPoint) {
        this.mActualImageFocusPoint = focusPoint;
        return this;
    }

    public FrescoLoader overlays(List<Drawable> overlays) {
        this.mOverlays = overlays;
        return this;
    }

    public FrescoLoader overlay(Drawable overlay) {
        return overlays(overlay == null ? null : Collections.singletonList(overlay));
    }

    public FrescoLoader overlay(int resId) {
        return overlay(mContext.getResources().getDrawable(resId));
    }

    public FrescoLoader pressedStateOverlay(Drawable drawable) {
        if (drawable == null) {
            this.mPressedStateOverlay = null;
        } else {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawable);
            this.mPressedStateOverlay = stateListDrawable;
        }
        return this;
    }

    public FrescoLoader pressedStateOverlay(int resId) {
        return pressedStateOverlay(mContext.getResources().getDrawable(resId));
    }

    public FrescoLoader colorFilter(ColorFilter colorFilter) {
        this.mActualImageColorFilter = colorFilter;
        return this;
    }

    public FrescoLoader cornersRadius(int radius) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setCornersRadius(radius);
        return this;
    }

    public FrescoLoader border(int borderColor, float borderWidth) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setBorder(borderColor, borderWidth);
        return this;
    }

    public FrescoLoader borderColor(int borderColor) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setBorderColor(borderColor);
        return this;
    }

    public FrescoLoader borderWidth(float borderWidth) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setBorderWidth(borderWidth);
        return this;
    }

    public FrescoLoader roundAsCircle() {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setRoundAsCircle(true);
        return this;
    }

    public FrescoLoader cornersRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setCornersRadii(topLeft, topRight, bottomRight, bottomLeft);
        return this;
    }

    public FrescoLoader cornersRadii(float[] radii) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setCornersRadii(radii);
        return this;
    }

    public FrescoLoader overlayColor(int overlayColor) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setOverlayColor(overlayColor);
        return this;
    }

    public FrescoLoader padding(float padding) {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setPadding(padding);
        return this;
    }


    public FrescoLoader roundingMethodWithOverlayColor() {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setRoundingMethod(RoundingParams.RoundingMethod.OVERLAY_COLOR);
        return this;
    }

    public FrescoLoader roundingMethodWithBitmapOnly() {
        if (this.mRoundingParams == null) {
            this.mRoundingParams = new RoundingParams();
        }
        this.mRoundingParams.setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
        return this;
    }

    public FrescoLoader resize(Point point) {
        this.mResizeOptions = new ResizeOptions(point.x, point.y);
        return this;
    }

    public FrescoLoader resize(int targetWidth, int targetHeight) {
        this.mResizeOptions = new ResizeOptions(targetWidth, targetHeight);
        return this;
    }

    public FrescoLoader fadeDuration(int fadeDuration) {
        this.mFadeDuration = fadeDuration;
        return this;
    }

    public FrescoLoader desiredAspectRatioWithWidth(float desiredAspectRatio) {
        this.mUseFixedWidth = true;
        this.mDesiredAspectRatio = desiredAspectRatio;
        return this;
    }

    public FrescoLoader desiredAspectRatioWithHeight(float desiredAspectRatio) {
        this.mUseFixedWidth = false;
        this.mDesiredAspectRatio = desiredAspectRatio;
        return this;
    }

    public FrescoLoader autoRotateEnabled(boolean enabled) {
        this.mAutoRotateEnabled = enabled;
        return this;
    }

    public FrescoLoader autoPlayAnimations(boolean enabled) {
        this.mAutoPlayAnimations = enabled;
        return this;
    }

    public FrescoLoader retainImageOnFailure(boolean enabled) {
        this.mRetainImageOnFailure = enabled;
        return this;
    }

    public FrescoLoader progressiveRenderingEnabled(boolean enabled) {
        this.mProgressiveRenderingEnabled = enabled;
        return this;
    }

    public FrescoLoader localThumbnailPreviewsEnabled(boolean enabled) {
        this.mLocalThumbnailPreviewsEnabled = enabled;
        return this;
    }

    public FrescoLoader tapToRetryEnabled(boolean tapToRetryEnabled) {
        this.mTapToRetryEnabled = tapToRetryEnabled;
        return this;
    }

    public FrescoLoader compatTemporaryDetach(boolean compatTemporaryDetach) {
        this.mCompatTemporaryDetach = compatTemporaryDetach;
        return this;
    }

    public void into(ImageView targetView) {
        if (targetView == null) {
            return;
        }
        if (mUri == null) {
            return;
        }

        //we should use tag
        if (mDraweeHolder == null) {
            Object tag = targetView.getTag();
            if (tag instanceof DraweeHolder) {
                mDraweeHolder = (DraweeHolder<DraweeHierarchy>) tag;
            }
        }
        if (mDraweeHolder == null) {
            mDraweeHolder = DraweeHolder.create(null, targetView.getContext());
            if (mDraweeHolderDispatcher == null) {
                mDraweeHolderDispatcher = new DraweeHolderDispatcher();
            }
            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(targetView.getResources())
                    .setPlaceholderImage(mPlaceholderDrawable)
                    .setPlaceholderImageScaleType(mPlaceholderScaleType)
                    .setFailureImage(mFailureDrawable)
                    .setFailureImageScaleType(mFailureScaleType)
                    .setProgressBarImage(mProgressBarDrawable)
                    .setProgressBarImageScaleType(mProgressScaleType)
                    .setRetryImage(mRetryDrawable)
                    .setRetryImageScaleType(mRetryScaleType)
                    .setFadeDuration(mFadeDuration)
                    .setActualImageFocusPoint(mActualImageFocusPoint)
                    .setActualImageColorFilter(mActualImageColorFilter)
                    .setActualImageScaleType(mActualImageScaleType)
                    .setBackground(mBackgroundDrawable)
                    .setOverlays(mOverlays)
                    .setPressedStateOverlay(mPressedStateOverlay)
                    .setRoundingParams(mRoundingParams)
                    .build();

            //set hierarchy
            mDraweeHolder.setHierarchy(hierarchy);

            //image request
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mUri)
                    .setAutoRotateEnabled(mAutoRotateEnabled)
                    .setLocalThumbnailPreviewsEnabled(mLocalThumbnailPreviewsEnabled)
                    .setPostprocessor(mPostprocessor)
                    .setProgressiveRenderingEnabled(mProgressiveRenderingEnabled)
                    .setResizeOptions(mResizeOptions)
                    .build();

            //controller
            PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(mAutoPlayAnimations)
                    .setControllerListener(mControllerListener)
                    .setImageRequest(request)
                    .setOldController(mDraweeHolder.getController())
                    .setRetainImageOnFailure(mRetainImageOnFailure)
                    .setTapToRetryEnabled(mTapToRetryEnabled);


            //if set the mLowerUri, then pass this param
            if (mLowerUri != null) {
                controllerBuilder.setLowResImageRequest(ImageRequest.fromUri(mLowerUri));
            }
            //build controller
            DraweeController draweeController = controllerBuilder.build();
            //set controller
            mDraweeHolder.setController(draweeController);

            if (mCompatTemporaryDetach) {
                ViewCompat.addOnAttachStateChangeListener(targetView, mDraweeHolderDispatcher);
            } else {
                //if targetView is instanceof TemporaryDetachListener, set TemporaryDetachListener
                //you should override onSaveTemporaryDetachListener(TemporaryDetachListener l) to holder the param TemporaryDetachListener.
                //also override method onStartTemporaryDetach() and onFinishTemporaryDetach() to call the holder's onStartTemporaryDetach() and onFinishTemporaryDetach()
                if (targetView instanceof TemporaryDetachListener) {
                    ((TemporaryDetachListener) targetView).onSaveTemporaryDetachListener(mDraweeHolderDispatcher);
                }
                //if is already attached, call method onViewAttachedToWindow.
                if (isAttachedToWindow(targetView)) {
                    mDraweeHolderDispatcher.onViewAttachedToWindow(targetView);
                }
                //add attach state change listener
                targetView.addOnAttachStateChangeListener(mDraweeHolderDispatcher);
            }
            targetView.setOnTouchListener(mDraweeHolderDispatcher);
            targetView.setTag(mDraweeHolder);
        } else {
            //release original resource
            mDraweeHolder.onDetach();

            GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(targetView.getResources())
                    .setPlaceholderImage(mPlaceholderDrawable)
                    .setPlaceholderImageScaleType(mPlaceholderScaleType)
                    .setFailureImage(mFailureDrawable)
                    .setFailureImageScaleType(mFailureScaleType)
                    .setProgressBarImage(mProgressBarDrawable)
                    .setProgressBarImageScaleType(mProgressScaleType)
                    .setRetryImage(mRetryDrawable)
                    .setRetryImageScaleType(mRetryScaleType)
                    .setFadeDuration(mFadeDuration)
                    .setActualImageFocusPoint(mActualImageFocusPoint)
                    .setActualImageColorFilter(mActualImageColorFilter)
                    .setActualImageScaleType(mActualImageScaleType)
                    .setBackground(mBackgroundDrawable)
                    .setOverlays(mOverlays)
                    .setPressedStateOverlay(mPressedStateOverlay)
                    .setRoundingParams(mRoundingParams)
                    .build();

            //set hierarchy
            mDraweeHolder.setHierarchy(hierarchy);

            //image request
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(mUri)
                    .setAutoRotateEnabled(mAutoRotateEnabled)
                    .setLocalThumbnailPreviewsEnabled(mLocalThumbnailPreviewsEnabled)
                    .setPostprocessor(mPostprocessor)
                    .setProgressiveRenderingEnabled(mProgressiveRenderingEnabled)
                    .setResizeOptions(mResizeOptions)
                    .build();

            //controller
            PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(mAutoPlayAnimations)
                    .setControllerListener(mControllerListener)
                    .setImageRequest(request)
                    .setOldController(mDraweeHolder.getController())
                    .setRetainImageOnFailure(mRetainImageOnFailure)
                    .setTapToRetryEnabled(mTapToRetryEnabled);


            //if set the mLowerUri, then pass this param
            if (mLowerUri != null) {
                controllerBuilder.setLowResImageRequest(ImageRequest.fromUri(mLowerUri));
            }
            //build controller
            DraweeController draweeController = controllerBuilder.build();
            //set controller
            mDraweeHolder.setController(draweeController);
        }

        //compat for desiredAspectRatio
        if (mDesiredAspectRatio != 0) {
            ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
            if (layoutParams != null) {
                int width = layoutParams.width;
                int height = layoutParams.height;
                int newWidth = -1;
                int newHeight = -1;
                //mDesiredAspectRatio= width/height;
                if (mUseFixedWidth) {
                    //with must > 0 & height=0
                    if (width > 0 && height == 0) {
                        newWidth = width;
                        newHeight = (int) (width * 1.0 / mDesiredAspectRatio + 0.5);
                    }
                } else {
                    //height must > 0 & width=0
                    if (height > 0 && width == 0) {
                        newHeight = height;
                        newWidth = (int) (height * mDesiredAspectRatio + 0.5);
                    }
                }
                if (newWidth != -1 && newHeight != -1) {
                    layoutParams.width = newWidth;
                    layoutParams.height = newHeight;
                    targetView.requestLayout();
                }
            }
        }

        //set image drawable
        targetView.setImageDrawable(mDraweeHolder.getTopLevelDrawable());

    }

    private static boolean isAttachedToWindow(View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            return view.isAttachedToWindow();
        } else {
            return view.getWindowToken() != null;
        }
    }

    private static ScalingUtils.ScaleType convertToFrescoScaleType(ImageView.ScaleType scaleType, ScalingUtils.ScaleType defaultScaleType) {
        switch (scaleType) {
            case CENTER:
                return ScalingUtils.ScaleType.CENTER;
            case CENTER_CROP:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case CENTER_INSIDE:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case FIT_CENTER:
                return ScalingUtils.ScaleType.FIT_CENTER;
            case FIT_START:
                return ScalingUtils.ScaleType.FIT_START;
            case FIT_END:
                return ScalingUtils.ScaleType.FIT_END;
            case FIT_XY:
                return ScalingUtils.ScaleType.FIT_XY;
            case MATRIX:
                //NOTE this case
                //you should set FocusPoint to make sentence
                return ScalingUtils.ScaleType.FOCUS_CROP;
            default:
                return defaultScaleType;
        }
    }


    //if needed, let's your image view implement this interface
    //also it's not must be required to implement this interface
    public interface TemporaryDetachListener {

        void onSaveTemporaryDetachListener(TemporaryDetachListener listener);

        void onStartTemporaryDetach(View view);

        void onFinishTemporaryDetach(View view);
    }


    //DraweeHolder event dispatch
    private class DraweeHolderDispatcher implements View.OnAttachStateChangeListener, View.OnTouchListener, TemporaryDetachListener {

        @Override
        public void onViewAttachedToWindow(View v) {
            if (mDraweeHolder != null) {
                mDraweeHolder.onAttach();
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (mDraweeHolder != null) {
                mDraweeHolder.onDetach();
            }
        }

        @Override
        public void onSaveTemporaryDetachListener(TemporaryDetachListener listener) {
            //empty
        }

        @Override
        public void onStartTemporaryDetach(View view) {
            if (mDraweeHolder != null) {
                mDraweeHolder.onDetach();
            }
        }

        @Override
        public void onFinishTemporaryDetach(View view) {
            if (mDraweeHolder != null) {
                mDraweeHolder.onAttach();
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mDraweeHolder != null) {
                if (mDraweeHolder.onTouchEvent(event)) {
                    return true;
                }
            }
            return false;
        }
    }

}