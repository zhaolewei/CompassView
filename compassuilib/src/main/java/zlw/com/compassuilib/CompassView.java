package zlw.com.compassuilib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
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
    private float textW = 0, textH = 0; //TODO:测量
    private float textOriW = 0, textOriH = 0; //TODO:测量
    private float textAngleW = 0; //TODO:测量

    private double lineRateSize = 3d / 66d;

    private static final float DIVIDE_COUNT = 120; //将圆划分为120等份

    private int width, height;


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
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#00cccc"));
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mKeyPaint = new Paint();
        mKeyPaint.setStrokeWidth(1);
        mKeyPaint.setColor(Color.parseColor("#ffffee"));
        mKeyPaint.setAntiAlias(true);

        mMainPaint = new Paint();
        mMainPaint.setStrokeWidth(4);
        mMainPaint.setColor(Color.parseColor("#ffffee"));
        mMainPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.parseColor("#00ffff"));
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(18);
        mTextPaint.setAntiAlias(true);

        mTmpPaint = new Paint();
        mTmpPaint.setColor(Color.parseColor("#ff0000"));
        mTmpPaint.setStrokeWidth(4);
        mTmpPaint.setAntiAlias(true);


        mOrientationTextPoint = new Paint();
        mOrientationTextPoint.setColor(Color.parseColor("#00cccc"));
        mOrientationTextPoint.setStrokeWidth(3);
        mOrientationTextPoint.setTextSize(20);
        mOrientationTextPoint.setAntiAlias(true);

        mAngleTextPoint = new Paint();
        mAngleTextPoint.setColor(Color.parseColor("#ffffff"));
        mAngleTextPoint.setStrokeWidth(2);
        mAngleTextPoint.setTextSize(28);
        mAngleTextPoint.setAntiAlias(true);

        mMidpointTextPoint = new Paint();
        mMidpointTextPoint.setColor(Color.parseColor("#ffffff"));
        mMidpointTextPoint.setStrokeWidth(2);
        mMidpointTextPoint.setTextSize(22);
        mMidpointTextPoint.setAntiAlias(true);

        initWH();
    }

    private void initWH() {
        String test = "2700";
        Rect rect = new Rect();
        mPaint.getTextBounds(test, 0, test.length(), rect);
        textW = rect.width();//文字宽
        textH = rect.height();//文字高

        rect = new Rect();
        mAngleTextPoint.getTextBounds(test, 0, test.length(), rect);
        textAngleW = rect.width();//文字宽

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
                        getRotatePointX(rotateAngle, (float) (width * lineRateSize + 80), height / 2), getRotatePointY(rotateAngle, (float) (width * lineRateSize + 80), height / 2), mKeyPaint);

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
                canvas.drawText(strA, getRotatePointX(rotateAngle, 50, height / 2) - textW / 2, getRotatePointY(rotateAngle, 50, height / 2) + textH / 2, mTextPaint);
//                canvas.drawPoint(getRotatePointX(rotateAngle, 50, height / 2), getRotatePointY(rotateAngle, 50, height / 2), mTmpPaint);


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
        canvas.drawText((int) Math.abs(rotate) + "°", width / 2 - textAngleW / 2 + 8, height / 2, mAngleTextPoint);

        canvas.drawLine(width / 2, 80, width / 2, 120, mMainPaint);

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

        canvas.drawText(currentOri, width / 2 - textAngleW / 2 + 12, height / 2 + 26, mMidpointTextPoint);

    }

    private void drawOriText(Canvas canvas, String strOri, float rotateAngle) {
        canvas.drawText(strOri, getRotatePointX(rotateAngle, 115, height / 2) - textOriW / 2, getRotatePointY(rotateAngle, 115, height / 2) + textOriH / 2, mOrientationTextPoint);
//        canvas.drawPoint(getRotatePointX(rotateAngle, 120, height / 2), getRotatePointY(rotateAngle, 120, height / 2), mTmpPaint);
    }

    //坐标旋转公式
    private float getRotatePointX(float a, float x, float y) {
        return (float) ((x - width / 2) * Math.cos(CONVERSION_ANGLE_CONST * a) + (y - height / 2) * Math.sin(CONVERSION_ANGLE_CONST * a)) + width / 2;
    }

    private float getRotatePointY(float a, float x, float y) {
        return (float) ((y - height / 2) * Math.cos(CONVERSION_ANGLE_CONST * a) - (x - width / 2) * Math.sin(CONVERSION_ANGLE_CONST * a)) + height / 2;
    }

}
