package com.atin.arcface.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.atin.arcface.R;
import com.atin.arcface.model.CircleWraper;
import com.atin.arcface.service.SingletonObject;
import com.atin.arcface.util.ConfigUtil;

public class FocusView extends View {
    private Paint mTransparentPaint;
    private Paint mSemiBlackPaint;
    private Paint mStrokePaint;
    private Path mPath = new Path();

    public FocusView(Context context) {
        super(context);
        initPaints();
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initPaints() {
        mTransparentPaint = new Paint();
        mTransparentPaint.setColor(Color.TRANSPARENT);
        mTransparentPaint.setStrokeWidth(10);

        mSemiBlackPaint = new Paint();
        mSemiBlackPaint.setColor(Color.TRANSPARENT);
        mSemiBlackPaint.setStrokeWidth(10);

        mStrokePaint = new Paint();
        mStrokePaint.setColor(getContext().getColor(R.color.colorPrimary));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();

        //Bán kính
        int radius = Math.min(canvas.getWidth() / 2, canvas.getHeight() / 2 - 45) - 50;

        //Vẽ vòng tròn rỗng ở giữa
        mPath.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2 - 45, radius, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        //Màu nền
        canvas.drawPath(mPath, mSemiBlackPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.parseColor("#99000000"));

        //Add đường tròn rỗng
        //canvas.drawPoint(canvas.getWidth() / 2, canvas.getHeight() / 2, mTransparentPaint);

        //Add viền tròn
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2 - 45, radius, mStrokePaint);

        CircleWraper circleWraper = new CircleWraper(canvas.getWidth() / 2, canvas.getHeight() / 2 - 45, radius);
        ConfigUtil.setCircleWraper(circleWraper);
    }
}