package com.cepheuen.elegantnumberbutton.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.R;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

/**
 * Created by Ashik Vetrivelu on 10/08/16.
 */
public class ElegantNumberButton extends RelativeLayout {
    private Context context;

    private OnClickListener mListener;
    private int styleAttr;

    private AttributeSet attrs;
    private int backgroundColor;
    private float initialNumber;
    private float lastNumber;
    private String numberUnit;
    private Boolean hideSign;
    private float currentNumber;
    private float finalNumber;

    //layout
    private TextView textView;
    private TextView tvNumberUnit;
    public ImageView ivPlusButton;
    public ImageView ivMinusButton;
    public ImageView ivDirection;




    private OnValueChangeListener mOnValueChangeListener;



    public ElegantNumberButton(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ElegantNumberButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public ElegantNumberButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.attrs = attrs;
        this.styleAttr = defStyleAttr;
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        inflate(context, R.layout.layout, this);
        final Resources res = getResources();

        // [ATTRIBUTE]
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ElegantNumberButton,
                styleAttr, 0);
        backgroundColor = a.getColor(R.styleable.ElegantNumberButton_backgroundColor, res.getColor(R.color.white));
        initialNumber = a.getFloat(R.styleable.ElegantNumberButton_initialNumber, 0);
        finalNumber = a.getFloat(R.styleable.ElegantNumberButton_finalNumber, Float.MAX_VALUE);
        numberUnit = a.getString(R.styleable.ElegantNumberButton_numberUnit);
        hideSign = a.getBoolean(R.styleable.ElegantNumberButton_hideSign, true);
        int textColor = a.getColor(R.styleable.ElegantNumberButton_numberColor, res.getColor(android.R.color.black));
        int textSize = a.getDimensionPixelSize(R.styleable.ElegantNumberButton_numberTextSize,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, context.getResources().getDisplayMetrics()));
        int numberUnitSize = a.getDimensionPixelSize(R.styleable.ElegantNumberButton_numberUnitSize,textSize / 2);
        Drawable buttonBackground = a.getDrawable(R.styleable.ElegantNumberButton_buttonBackground);
        Drawable plusSignDrawable = a.getDrawable(R.styleable.ElegantNumberButton_plusSignDrawable);
        Drawable minusSignDrawable = a.getDrawable(R.styleable.ElegantNumberButton_minusSignDrawable);
        Drawable directionDrawable = a.getDrawable(R.styleable.ElegantNumberButton_directionDrawable);
        int buttonSize = a.getDimensionPixelSize(R.styleable.ElegantNumberButton_buttonSize,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, context.getResources().getDisplayMetrics()));

        // [LAYOUT]
        // layout variable initialize
        LinearLayout mLayout = findViewById(R.id.layout);
        ivMinusButton = findViewById(R.id.ivMinusButton);
        ivPlusButton = findViewById(R.id.ivPlusButton);
        textView = findViewById(R.id.number_counter);
        tvNumberUnit = findViewById(R.id.number_unit);
        ivDirection = findViewById(R.id.ivDirection);

        // layout:RootView
        mLayout.setBackgroundColor(backgroundColor);

        // layout:button
        if (minusSignDrawable != null)
            ivMinusButton.setImageDrawable(minusSignDrawable);
        if (plusSignDrawable != null)
            ivPlusButton.setImageDrawable(plusSignDrawable);
        if (buttonBackground != null) {
            ivPlusButton.setBackground(buttonBackground);
            ivPlusButton.setLayoutParams(new LinearLayout.LayoutParams((int)buttonSize,(int)buttonSize));
            ivMinusButton.setBackground(buttonBackground);
            ivMinusButton.setLayoutParams(new LinearLayout.LayoutParams((int)buttonSize,(int)buttonSize));
        }

        // layout:direction
        if (directionDrawable != null) {
            ivDirection.setImageDrawable(directionDrawable);
            ViewGroup.LayoutParams lp = ivDirection.getLayoutParams();
            lp.width = (int)buttonSize/4;
            lp.height = (int)buttonSize/4;
            ivDirection.setLayoutParams(lp);
        }

        // layout:text
        setNumber(String.valueOf(initialNumber), true);
//        textView.setText(String.valueOf(initialNumber));
        setNumber(String.valueOf(initialNumber), true);
        textView.setTextColor(textColor);
        if (!TextUtils.isEmpty(numberUnit)) {
            tvNumberUnit.setVisibility(VISIBLE);
            tvNumberUnit.setText(numberUnit);
            tvNumberUnit.setTextColor(textColor);
        }
        textView.setTextSize(COMPLEX_UNIT_PX,textSize);
        tvNumberUnit.setTextSize(COMPLEX_UNIT_PX,numberUnitSize);
        ViewGroup.LayoutParams p = tvNumberUnit.getLayoutParams();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)p;
        lp.setMargins(0, (textSize-numberUnitSize)*4/10, 0, 0);
        tvNumberUnit.setLayoutParams(lp);

        // [VARIABLE]
        currentNumber = initialNumber;
        lastNumber = initialNumber;

        // [LISTENER]
        ivMinusButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnLongPressDecrement = true;
                repeatUpdateHandler.post (new RptUpdater() );
                return false;
            }
        });
        ivMinusButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mOnLongPressDecrement = false;
//                        float num = Float.valueOf(textView.getText().toString());
                        float num = currentNumber;
                        setNumber(String.valueOf(Math.round((num - 0.1)*10.0f)/10.0f), true);
                }
                return false;
            }
        });
        ivPlusButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnLongPressIncrement = true;
                repeatUpdateHandler.post (new RptUpdater() );
                return false;
            }
        });
        ivPlusButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mOnLongPressIncrement = false;
//                        float num = Float.valueOf(textView.getText().toString());
                        float num = currentNumber;
                        setNumber(String.valueOf(Math.round((num + 0.1)*10.0f)/10.0f), true);
                }
                return false;
            }
        });
        a.recycle();
    }
    private boolean mOnLongPressIncrement = false;
    private boolean mOnLongPressDecrement = false;
    private Handler repeatUpdateHandler = new Handler();
    private int REP_DELAY = 100;
    class RptUpdater implements Runnable {
        @Override
        public void run() {
            if (mOnLongPressIncrement) {
//                float num = Float.valueOf(textView.getText().toString());
                float num = currentNumber;
                setNumber(String.valueOf(Math.round((num + 0.1)*10.0f)/10.0f), false);
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            } else if (mOnLongPressDecrement) {
//                float num = Float.valueOf(textView.getText().toString());
                float num = currentNumber;
                setNumber(String.valueOf(Math.round((num - 0.1)*10.0f)/10.0f), false);
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }

    private void callListener(View view) {
        if (mListener != null) {
            mListener.onClick(view);
        }

        if (mOnValueChangeListener != null) {
            if (lastNumber != currentNumber) {
                mOnValueChangeListener.onValueChange(this, lastNumber, currentNumber);
            }
        }
    }

    public String getNumber() {
        return String.valueOf(currentNumber);
    }

    public void setNumber(String number) {
        lastNumber = currentNumber;
        this.currentNumber = Float.parseFloat(number);
        if (this.currentNumber > finalNumber) {
            this.currentNumber = finalNumber;
        }
        if (this.currentNumber < initialNumber) {
            this.currentNumber = initialNumber;
        }
        textView.setText(String.valueOf(hideSign ? Math.abs(currentNumber) : currentNumber));
    }

    public void setNumber(String number, boolean notifyListener) {
        setNumber(number);
        if (notifyListener) {
            callListener(this);
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        mOnValueChangeListener = onValueChangeListener;
    }

    @FunctionalInterface
    public interface OnClickListener {
        void onClick(View view);
    }

    public interface OnValueChangeListener {
        void onValueChange(ElegantNumberButton view, float oldValue, float newValue);
    }

    public void setRange(float startingNumber, float endingNumber) {
        this.initialNumber = startingNumber;
        this.finalNumber = endingNumber;
    }
}
