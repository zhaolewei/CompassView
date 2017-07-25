package zlw.com.compassuilib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by 赵乐玮 on 2017/7/22.
 */

public class CompassView extends View {
    private static final double CONVERSION_ANGLE_CONST = Math.PI / 180;  //转换角所用的常量
    private static final String TAG = CompassView.class.getSimpleName();

    private Paint mPaint; // 绘制普通线条
    private Paint mKeyPaint;//每30度绘制一个关键线
    private Paint mTextPaint; //绘制方向文字
    private Paint mTmpPaint; //辅助点
    private Paint mOrientationTextPoint; //辅助点
    private Paint mAngleTextPoint; //辅助点
    private Paint mMainPaint; //指针线
    private Paint mMidpointTextPoint; //中间显示当前方向文字


    private float rotate = 0f; //顺时针旋转角度
    private float textW = 0, textH = 0;
    private float textOriW = 0, textOriH = 0;
    private float textAngleW1, textAngleW2;


    private int width, height;

    private static final float DIVIDE_COUNT = 120; //将圆划分为120等份
    private double lineRateSize = 1 / 22d;
    private int lineColor;
    private int keyLinecolor;
    private int mainLinecolor;
    private int edgeTextColor;
    private int orientationTextColor;
    private int angleTextColor;

    private int edgeTextSize;
    private int orientationTextSize;
    private int angleTextSize;

    private int oriTextMargin;
    private int rowPitch;
    private int mainLineLength;

    private boolean isDebug = true;
    private int edgeTextMargin;

    //外置接口
    public void setRotate(float rotate) {
        this.rotate = (int) -rotate;
        invalidate();
    }


    public CompassView(Context context) {
        super(context);
        init();
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompassView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        lineColor = Color.parseColor("#00cccc");
        keyLinecolor = Color.parseColor("#ffffee");
        mainLinecolor = Color.parseColor("#ffffee");
        edgeTextColor = Color.parseColor("#00ffff");
        orientationTextColor = Color.parseColor("#00cccc");
        angleTextColor = Color.parseColor("#ffffff");


//                edgeTextSize = 18;
//        edgeTextMargin = 30;
//        orientationTextSize = 20;
//        oriTextMargin = 35;
//        angleTextSize = 28;
//        rowPitch = 26;
//        mainLineLength = 15;

        //渐进公式


        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mKeyPaint = new Paint();
        mKeyPaint.setStrokeWidth(1);
        mKeyPaint.setColor(keyLinecolor);
        mKeyPaint.setAntiAlias(true);

        mMainPaint = new Paint();
        mMainPaint.setStrokeWidth(4);
        mMainPaint.setColor(mainLinecolor);
        mMainPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(edgeTextColor);
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(edgeTextSize);
        mTextPaint.setAntiAlias(true);

        if (isDebug) {
            mTmpPaint = new Paint();
            mTmpPaint.setColor(Color.parseColor("#ff0000"));
            mTmpPaint.setStrokeWidth(8);
            mTmpPaint.setAntiAlias(true);
        }

        mOrientationTextPoint = new Paint();
        mOrientationTextPoint.setColor(orientationTextColor);
        mOrientationTextPoint.setStrokeWidth(3);
        mOrientationTextPoint.setTextSize(orientationTextSize);
        mOrientationTextPoint.setAntiAlias(true);

        mAngleTextPoint = new Paint();
        mAngleTextPoint.setColor(angleTextColor);
        mAngleTextPoint.setStrokeWidth(2);
        mAngleTextPoint.setTextSize(angleTextSize);
        mAngleTextPoint.setAntiAlias(true);

        mMidpointTextPoint = new Paint();
        mMidpointTextPoint.setColor(angleTextColor);
        mMidpointTextPoint.setStrokeWidth(2);
        mMidpointTextPoint.setTextSize(angleTextSize * 22 / 28);
        mMidpointTextPoint.setAntiAlias(true);

        initWH();
    }

    void initSize() {
        edgeTextSize = 38 - (1080 - width) / 30;
        edgeTextMargin = 50 - (1080 - width) / 30;
        orientationTextSize = 42 - (1080 - width) / 30;
        oriTextMargin = 92 - (1080 - width) / 12;
        angleTextSize = 60 - (1080 - width) * 4 / 75;
        rowPitch = 56 - (1080 - width) / 20;
        mainLineLength = 35 - (1080 - width) / 30;
    }

    private void initWH() {
        String test = "120°";
        Rect rect = new Rect();
        mTextPaint.getTextBounds(test, 0, test.length(), rect);
        textW = rect.width();//文字宽
        textH = rect.height();//文字高

        test = "120";
        rect = new Rect();
        mAngleTextPoint.getTextBounds(test, 0, test.length(), rect);
        textAngleW1 = rect.width();//文字宽

        test = "东北";
        rect = new Rect();
        mMidpointTextPoint.getTextBounds(test, 0, test.length(), rect);
        textAngleW2 = rect.width();//文字宽


        test = "东";
        rect = new Rect();
        mOrientationTextPoint.getTextBounds(test, 0, test.length(), rect);
        textOriW = rect.width();//文字宽
        textOriH = rect.height();//文字高
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        Log.d("lwz", "width: " + width + " ;height :" + height);
        height = width;
        initSize();
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float ax = 360 / DIVIDE_COUNT;
        for (int i = 0; i < DIVIDE_COUNT; i++) {
            float rotateAngle = (ax * i - rotate + 360) % 360; //当前i所对应的角度旋转后的角度
            //绘制普通线
            canvas.drawLine(getRotatePointX(rotateAngle, 80, height / 2), getRotatePointY(rotateAngle, 80, height / 2),
                    getRotatePointX(rotateAngle, (float) (width * lineRateSize + 80), height / 2), getRotatePointY(rotateAngle, (float) (width * lineRateSize + 80), height / 2), mPaint);
            if ((rotateAngle + rotate) % 30 == 0) {
                //绘制关键线
                canvas.drawLine(getRotatePointX(rotateAngle, 75, height / 2), getRotatePointY(rotateAngle, 75, height / 2),
                        getRotatePointX(rotateAngle, (float) (width * lineRateSize + 80), height / 2),
                        getRotatePointY(rotateAngle, (float) (width * lineRateSize + 80), height / 2), mKeyPaint);

                //绘制文字
                float genAngle = (((270 - rotateAngle < 0 ? (270 - rotateAngle + 360) : (270 - rotateAngle)) - rotate) + 360) % 360;//转换起始角度，以最上方为0度

                String strA;//显示的角度
                if (genAngle < 10) {
                    strA = " " + (int) genAngle + "°";
                } else if (genAngle < 100) {
                    strA = " " + (int) genAngle + "°";
                } else {
                    strA = "" + (int) genAngle + "°";

                }
                canvas.drawText(strA, getRotatePointX(rotateAngle, 80 - edgeTextMargin, height / 2) - textW / 2, getRotatePointY(rotateAngle, 80 - edgeTextMargin, height / 2) + textH / 2, mTextPaint);
                if (isDebug) {
                    canvas.drawPoint(getRotatePointX(rotateAngle, 80 - edgeTextMargin, height / 2), getRotatePointY(rotateAngle, 80 - edgeTextMargin, height / 2), mTmpPaint);
                }


                //绘制东西南北文字
                String strOri;//显示的方向
                if ((int) genAngle == 0) {
                    strOri = "北";
                    drawOriText(canvas, strOri, rotateAngle);
                } else if (genAngle == 90) {
                    strOri = "东";
                    drawOriText(canvas, strOri, rotateAngle);
                } else if (genAngle == 180) {
                    strOri = "南";
                    drawOriText(canvas, strOri, rotateAngle);
                } else if (genAngle == 270) {
                    strOri = "西";
                    drawOriText(canvas, strOri, rotateAngle);
                }
            }
        }
        //绘制度数
        canvas.drawText((int) Math.abs(rotate) + "°", width / 2 - textAngleW1 / 2, height / 2, mAngleTextPoint);

        canvas.drawLine(width / 2, 80 - mainLineLength,
                width / 2, (float) (width * lineRateSize + 80),
                mMainPaint);

        //绘制当前方向
        String currentOri;
        if ((int) Math.abs(rotate) == 0) {
            currentOri = "北";
        } else if ((int) Math.abs(rotate) == 90) {
            currentOri = "东";
        } else if ((int) Math.abs(rotate) == 180) {
            currentOri = "南";
        } else if ((int) Math.abs(rotate) == 270) {
            currentOri = "西";
        } else if ((int) Math.abs(rotate) < 90) {
            currentOri = "东北";
        } else if ((int) Math.abs(rotate) < 180) {
            currentOri = "东南";
        } else if ((int) Math.abs(rotate) < 270) {
            currentOri = "西南";
        } else {
            currentOri = "西北";
        }

        canvas.drawText(currentOri, width / 2 - textAngleW2 / 2, height / 2 + rowPitch, mMidpointTextPoint);
        if (isDebug) {
            canvas.drawPoint(width / 2, height / 2 + rowPitch / 2, mTmpPaint);
        }

    }

    private void drawOriText(Canvas canvas, String strOri, float rotateAngle) {
        canvas.drawText(strOri, getRotatePointX(rotateAngle, 80 + oriTextMargin, height / 2) - textOriW / 2, getRotatePointY(rotateAngle, 80 + oriTextMargin, height / 2) + textOriH / 2, mOrientationTextPoint);
        if (isDebug) {
            canvas.drawPoint(getRotatePointX(rotateAngle, 80 + oriTextMargin, height / 2), getRotatePointY(rotateAngle, 80 + oriTextMargin, height / 2), mTmpPaint);
        }
    }

    //坐标旋转公式
    private float getRotatePointX(float a, float x, float y) {
        return (float) ((x - width / 2) * Math.cos(CONVERSION_ANGLE_CONST * a) + (y - height / 2) * Math.sin(CONVERSION_ANGLE_CONST * a)) + width / 2;
    }

    private float getRotatePointY(float a, float x, float y) {
        return (float) ((y - height / 2) * Math.cos(CONVERSION_ANGLE_CONST * a) - (x - width / 2) * Math.sin(CONVERSION_ANGLE_CONST * a)) + height / 2;
    }

}
