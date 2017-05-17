package activity.liuxing.tv.com.tvliuxing.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import activity.liuxing.tv.com.tvliuxing.R;

/**
 * Created by Administrator on 2017/5/5.
 */

@SuppressLint("AppCompatCustomView")
public class CircleTextImageViewTWO extends ImageView {
    private static final ScaleType SCALE_TYPE;
    private static final Bitmap.Config BITMAP_CONFIG;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private static final int DEFAULT_FILL_COLOR = 0;
    private static final int DEFAULT_TEXT_COLOR = -16777216;
    private static final int DEFAULT_TEXT_SIZE = 22;
    private static final int DEFAULT_TEXT_PADDING = 4;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;
    private final RectF mDrawableRect;
    private final RectF mBorderRect;
    private final Matrix mShaderMatrix;
    private final Paint mBitmapPaint;
    private final Paint mBorderPaint;
    private final Paint mFillPaint;
    private final Paint mTextPaint;
    private int mBorderColor;
    private int mBorderWidth;
    private int mFillColor;
    private String mTextString;
    private String mTextStringTwo;
    private int mTextColor;
    private int mTextSize;
    private int mTextSizeTwo;
    private int mTextPadding;
    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mDrawableRadius;
    private float mBorderRadius;
    private ColorFilter mColorFilter;
    private boolean mReady;
    private boolean mSetupPending;
    private boolean mBorderOverlay;

    public CircleTextImageViewTWO(Context context) {
        super(context);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mFillPaint = new Paint();
        this.mTextPaint = new Paint();
        this.mBorderColor = -16777216;
        this.mBorderWidth = 0;
        this.mFillColor = 0;
        this.mTextColor = -16777216;
        this.mTextSize = 22;
        this.mTextSizeTwo = 11;
        this.mTextPadding = 4;
        this.init();
    }

    public CircleTextImageViewTWO(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextImageViewTWO(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDrawableRect = new RectF();
        this.mBorderRect = new RectF();
        this.mShaderMatrix = new Matrix();
        this.mBitmapPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mFillPaint = new Paint();
        this.mTextPaint = new Paint();
        this.mBorderColor = -16777216;
        this.mBorderWidth = 0;
        this.mFillColor = 0;
        this.mTextColor = -16777216;
        this.mTextSize = 22;
        this.mTextSizeTwo = 11;
        this.mTextPadding = 4;
        TypedArray a = context.obtainStyledAttributes(attrs, com.thinkcool.circletextimageview.R.styleable.CircleTextImageView, defStyle, 0);
        this.mBorderWidth = a.getDimensionPixelSize(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_border_width, 0);
        this.mBorderColor = a.getColor(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_border_color, -16777216);
        this.mBorderOverlay = a.getBoolean(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_border_overlay, false);
        this.mFillColor = a.getColor(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_fill_color, 0);
        this.mTextString = a.getString(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_text_text);
        this.mTextStringTwo = a.getString(R.styleable.CircleTextImageView_citv_text_text);
        this.mTextColor = a.getColor(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_text_color, -16777216);
        this.mTextSize = a.getDimensionPixelSize(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_text_size, 22);
        this.mTextSizeTwo = a.getDimensionPixelSize(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_text_size, 11);
        this.mTextPadding = a.getDimensionPixelSize(com.thinkcool.circletextimageview.R.styleable.CircleTextImageView_citv_text_padding, 4);
        a.recycle();
        this.init();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        this.mReady = true;
        if (this.mSetupPending) {
            this.setup();
            this.mSetupPending = false;
        }

    }

    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", new Object[]{scaleType}));
        }
    }

    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.mBitmap != null || !TextUtils.isEmpty(this.mTextString)) {
            if (this.mFillColor != 0) {
                canvas.drawCircle((float) this.getWidth() / 2.0F, (float) this.getHeight() / 2.0F, this.mDrawableRadius, this.mFillPaint);
            }

            if (this.mBitmap != null) {
                canvas.drawCircle((float) this.getWidth() / 2.0F, (float) this.getHeight() / 2.0F, this.mDrawableRadius, this.mBitmapPaint);
            }

            if (this.mBorderWidth != 0) {
                canvas.drawCircle((float) this.getWidth() / 2.0F, (float) this.getHeight() / 2.0F, this.mBorderRadius, this.mBorderPaint);
            }

            if (!TextUtils.isEmpty(this.mTextString)) {
                this.mTextPaint.setTextSize(this.mTextSize);
                Paint.FontMetricsInt fm = this.mTextPaint.getFontMetricsInt();
                canvas.drawText(this.mTextString, (float) (this.getWidth() / 2) - this.mTextPaint.measureText(this.mTextString) / 2.0F, (float) ((this.getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2)) - 10, this.mTextPaint);
            }

            if (!TextUtils.isEmpty(this.mTextStringTwo)) {
                this.mTextPaint.setTextSize(this.mTextSizeTwo);
                Paint.FontMetricsInt fm = this.mTextPaint.getFontMetricsInt();
                canvas.drawText(this.mTextStringTwo, (float) (this.getWidth() / 2) - this.mTextPaint.measureText(this.mTextString) / 2.0F, (float) ((this.getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2)) + 20, this.mTextPaint);
            }

        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.setup();
    }

    public String getTextString() {
        return this.mTextString;
    }

    public void setText(@StringRes int TextResId) {
        this.setText(this.getResources().getString(TextResId));
    }

    public void setText(String textString) {
        this.mTextString = textString;
        this.invalidate();
    }

    public String getmTextStringTwo() {
        return mTextStringTwo;
    }

    public void setmTextStringTwo(String mTextStringTwo) {
        this.mTextStringTwo = mTextStringTwo;
        this.invalidate();
    }

    public int getTextColor() {
        return this.mTextColor;
    }

    public void setTextColor(@ColorInt int mTextColor) {
        this.mTextColor = mTextColor;
        this.mTextPaint.setColor(mTextColor);
        this.invalidate();
    }

    public void setTextColorResource(@ColorRes int colorResource) {
        this.setTextColor(this.getResources().getColor(colorResource));
    }

    public int getTextSize() {
        return this.mTextSize;
    }

    public void setTextSize(int textSize) {
        this.mTextSize = textSize;
        this.mTextPaint.setTextSize((float) textSize);
        this.invalidate();
    }

    public int getmTextSizeTwo() {
        return this.mTextSizeTwo;
    }

    public void setmTextSizeTwo(int textSize) {
        this.mTextSizeTwo = textSize;
        this.mTextPaint.setTextSize((float) mTextSizeTwo);
        this.invalidate();
    }

    public int getTextPadding() {
        return this.mTextPadding;
    }

    public void setTextPadding(int mTextPadding) {
        this.mTextPadding = mTextPadding;
        this.invalidate();
    }

    public int getBorderColor() {
        return this.mBorderColor;
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor != this.mBorderColor) {
            this.mBorderColor = borderColor;
            this.mBorderPaint.setColor(this.mBorderColor);
            this.invalidate();
        }
    }

    public void setBorderColorResource(@ColorRes int borderColorRes) {
        this.setBorderColor(this.getContext().getResources().getColor(borderColorRes));
    }

    public int getFillColor() {
        return this.mFillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        if (fillColor != this.mFillColor) {
            this.mFillColor = fillColor;
            this.mFillPaint.setColor(fillColor);
            this.invalidate();
        }
    }

    public void setFillColorResource(@ColorRes int fillColorRes) {
        this.setFillColor(this.getContext().getResources().getColor(fillColorRes));
    }

    public int getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth != this.mBorderWidth) {
            this.mBorderWidth = borderWidth;
            this.setup();
        }
    }

    public boolean isBorderOverlay() {
        return this.mBorderOverlay;
    }

    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay != this.mBorderOverlay) {
            this.mBorderOverlay = borderOverlay;
            this.setup();
        }
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.mBitmap = bm;
        this.setup();
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        this.mBitmap = this.getBitmapFromDrawable(drawable);
        this.setup();
    }

    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        this.mBitmap = this.getBitmapFromDrawable(this.getDrawable());
        this.setup();
    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.mBitmap = uri != null ? this.getBitmapFromDrawable(this.getDrawable()) : null;
        this.setup();
    }

    public void setColorFilter(ColorFilter cf) {
        if (cf != this.mColorFilter) {
            this.mColorFilter = cf;
            this.mBitmapPaint.setColorFilter(this.mColorFilter);
            this.invalidate();
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap bitmap;
                if (drawable instanceof ColorDrawable) {
                    bitmap = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
                }

                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    private void setup() {
        if (!this.mReady) {
            this.mSetupPending = true;
        } else if (this.getWidth() != 0 || this.getHeight() != 0) {
            if (this.mBitmap == null && TextUtils.isEmpty(this.mTextString)) {
                this.invalidate();
            } else {
                this.mTextPaint.setAntiAlias(true);
                this.mTextPaint.setColor(this.mTextColor);
                this.mTextPaint.setTextSize((float) this.mTextSize);
                this.mBorderPaint.setStyle(Paint.Style.STROKE);
                this.mBorderPaint.setAntiAlias(true);
                this.mBorderPaint.setColor(this.mBorderColor);
                this.mBorderPaint.setStrokeWidth((float) this.mBorderWidth);
                this.mFillPaint.setStyle(Paint.Style.FILL);
                this.mFillPaint.setAntiAlias(true);
                this.mFillPaint.setColor(this.mFillColor);
                this.mBorderRect.set(0.0F, 0.0F, (float) this.getWidth(), (float) this.getHeight());
                this.mBorderRadius = Math.min((this.mBorderRect.height() - (float) this.mBorderWidth) / 2.0F, (this.mBorderRect.width() - (float) this.mBorderWidth) / 2.0F);
                this.mDrawableRect.set(this.mBorderRect);
                if (!this.mBorderOverlay) {
                    this.mDrawableRect.inset((float) this.mBorderWidth, (float) this.mBorderWidth);
                }

                this.mDrawableRadius = Math.min(this.mDrawableRect.height() / 2.0F, this.mDrawableRect.width() / 2.0F);
                if (this.mBitmap != null) {
                    this.mBitmapShader = new BitmapShader(this.mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    this.mBitmapHeight = this.mBitmap.getHeight();
                    this.mBitmapWidth = this.mBitmap.getWidth();
                    this.mBitmapPaint.setAntiAlias(true);
                    this.mBitmapPaint.setShader(this.mBitmapShader);
                    this.updateShaderMatrix();
                }

                this.invalidate();
            }
        }
    }

    private void updateShaderMatrix() {
        float dx = 0.0F;
        float dy = 0.0F;
        this.mShaderMatrix.set((Matrix) null);
        float scale;
        if ((float) this.mBitmapWidth * this.mDrawableRect.height() > this.mDrawableRect.width() * (float) this.mBitmapHeight) {
            scale = this.mDrawableRect.height() / (float) this.mBitmapHeight;
            dx = (this.mDrawableRect.width() - (float) this.mBitmapWidth * scale) * 0.5F;
        } else {
            scale = this.mDrawableRect.width() / (float) this.mBitmapWidth;
            dy = (this.mDrawableRect.height() - (float) this.mBitmapHeight * scale) * 0.5F;
        }

        this.mShaderMatrix.setScale(scale, scale);
        this.mShaderMatrix.postTranslate((float) ((int) (dx + 0.5F)) + this.mDrawableRect.left, (float) ((int) (dy + 0.5F)) + this.mDrawableRect.top);
        this.mBitmapShader.setLocalMatrix(this.mShaderMatrix);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (!TextUtils.isEmpty(this.mTextString)) {
            int textMeasuredSize = (int) this.mTextPaint.measureText(this.mTextString);
            textMeasuredSize += 2 * this.mTextPadding;
            if (widthMeasureSpecMode == -2147483648 && heightMeasureSpecMode == -2147483648 && (textMeasuredSize > this.getMeasuredWidth() || textMeasuredSize > this.getMeasuredHeight())) {
                this.setMeasuredDimension(textMeasuredSize, textMeasuredSize);
            }
        }

    }

    static {
        SCALE_TYPE = ScaleType.CENTER_CROP;
        BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    }
}