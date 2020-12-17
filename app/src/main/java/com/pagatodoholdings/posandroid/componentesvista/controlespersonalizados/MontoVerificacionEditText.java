package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.R;

import java.text.NumberFormat;

public class MontoVerificacionEditText extends AppCompatEditText implements TextWatcher {
    public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

    private OnClickListener mClickListener;

    private Paint mLinesPaint;
    private Paint mStrokePaint;

    private boolean mMaskInput;

    private int defStyleAttr = 0;
    private int mMaxLength = 3;
    //Filtro de maximo numero de caracteres
    private int numberFilterChar =4;
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mTextColor;

    private float mLineStrokeSelected = 2; //2dp by default
    private float mLineStroke = 1; //1dp by default
    private float mSpace = 8; //24 dp by default, space between the lines
    private float mCharSize;
    private float mNumChars = 6;
    private float mLineSpacing = 10; //8dp by default, height of the text from our lines
    private String mBoxStyle;
    private String mMaskCharacter = "*";

    private static final String ROUNDED_BOX = "rounded_box";
    private static final String UNDERLINE = "underline";
    private static final String SQUARE_BOX = "square_box";
    private static final String ROUNDED_UNDERLINE = "rounded_underline";

    public MontoVerificacionEditText(Context context) {
        super(context);
    }

    public MontoVerificacionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MontoVerificacionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.defStyleAttr = defStyleAttr;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        getAttrsFromTypedArray(attrs);

        // Set the TextWatcher
        this.addTextChangedListener(this);

        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLineStrokeSelected = multi * mLineStrokeSelected;

        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);

        setBackgroundResource(0);
        mSpace = multi * mSpace; //convert to pixels for our density
        mNumChars = mMaxLength;
        setInputType(InputType.TYPE_CLASS_NUMBER);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(numberFilterChar)});

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
                //No implementation
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // When tapped, move cursor to end of text.


        super.setOnClickListener((View v) -> {
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
        });

    }

    private void getAttrsFromTypedArray(AttributeSet attributeSet) {
        final TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.MontoVerificacionEditText, defStyleAttr, 0);

        mPrimaryColor = a.getColor(R.styleable.MontoVerificacionEditText_oev_primary_color, ContextCompat.getColor(getContext(), R.color.colorBlueDark));
        mSecondaryColor = a.getColor(R.styleable.MontoVerificacionEditText_oev_secondary_color, ContextCompat.getColor(getContext(), R.color.colorBlueDark));
        mTextColor = a.getColor(R.styleable.MontoVerificacionEditText_oev_text_color, ContextCompat.getColor(getContext(), R.color.colorAzulImporte));
        mBoxStyle = a.getString(R.styleable.MontoVerificacionEditText_oev_box_style);
        mMaskInput = a.getBoolean(R.styleable.MontoVerificacionEditText_oev_mask_input, false);

        if (mBoxStyle != null && !mBoxStyle.isEmpty()) {
            switch (mBoxStyle) {
                case UNDERLINE:
                case ROUNDED_UNDERLINE:
                    mStrokePaint = new Paint(getPaint());
                    mStrokePaint.setStrokeWidth(4);
                    mStrokePaint.setStyle(Paint.Style.FILL);
                    break;

                case SQUARE_BOX:
                case ROUNDED_BOX:
                    mStrokePaint = new Paint(getPaint());
                    mStrokePaint.setStrokeWidth(4);
                    mStrokePaint.setStyle(Paint.Style.STROKE);
                    break;

                default:
                    mStrokePaint = new Paint(getPaint());
                    mStrokePaint.setStrokeWidth(4);
                    mStrokePaint.setStyle(Paint.Style.FILL);

                    mBoxStyle = UNDERLINE;
            }
        } else {
            mStrokePaint = new Paint(getPaint());
            mStrokePaint.setStrokeWidth(4);
            mStrokePaint.setStyle(Paint.Style.FILL);

            mBoxStyle = UNDERLINE;
        }

        a.recycle();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new IllegalArgumentException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        NumberFormat dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener("SIGMA MANAGER", "Error al obtener numberFormat"));
        definitionSizeCanvas();
        mLineSpacing = (float) (getHeight() * .6);

        int startX = 0;
        int bottom = getHeight() - getPaddingBottom();

        //Text Width
        Editable text = getText();
        text.replace(0,getText().length(), String.format("%03d", getText().length()== 0 ? 0 : Integer.parseInt(getText().toString())));
        text.replace(0,getText().length(), (getText().toString().contains("000") ? getText().toString().replace("0","") : getText().toString()));
        text.replace(0,getText().length(), String.format("%03d", getText().length()== 0 ? 0 : Integer.parseInt(getText().toString())));
        final String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener("MONTO VERIFICACION", "Error al obtener parametro fijo"));
        final String posicionMoneda  = SigmaBdManager.getParametroFijo("0022",new OnFailureListener.BasicOnFailureListener("MONTO VERIFICACION","Error al obtener parametro Fijo"));

        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(text, 0, textLength, textWidths);

        if(posicionMoneda.equals("0")) {
            canvas.drawText(parametroMoneda, 0, parametroMoneda.length(), (startX - (float) parametroMoneda.length() / 2), getCy(parametroMoneda.length()), setTextSizeForWidth(getWidthForText(parametroMoneda.length()), parametroMoneda));
            startX += 160;
        }else{
            startX += 10;
        }

        for (int i = 0; i < mNumChars; i++) {
            updateColorForLines(i == textLength);
            createRectForms(canvas,startX,bottom);
            defineDrawText(i,startX,canvas,textWidths,text);

            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
                createRectForms(canvas,startX,bottom);

            if(dbnumberformat.getMaximumFractionDigits() != 0 && ((mNumChars - 1) - i == 2)){
                    canvas.drawCircle((startX - 5), (bottom - 15), 10, getPaint());
                    startX += 15;
            }
        }//End For

        if(posicionMoneda.equals("1")) {
            startX += 10;
            canvas.drawText(parametroMoneda, 0, parametroMoneda.length(), (startX - (float) parametroMoneda.length() / 2), getCy(parametroMoneda.length()), setTextSizeForWidth(getWidthForText(parametroMoneda.length()), parametroMoneda));
        }
    }

    private void defineDrawText(int i, int startX, Canvas canvas, float[] textWidths,Editable text){
        if (getText().length() > i) {
            float middle = startX + mCharSize / 2;
            final Paint paint = getPaint();
            paint.setColor(mTextColor);
            if (mMaskInput) {
                canvas.drawText(getMaskText(), i, i + 1, middle - textWidths[0] / 2, mLineSpacing, paint);
            } else {
                canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, mLineSpacing, paint);
            }
        }

    }

    private void definitionSizeCanvas() {
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = ((availableWidth - 180) - (mSpace * (mNumChars - 1))) / mNumChars;
        }
    }

    private void createRectForms(Canvas canvas, int startX, int bottom) {
        int top = getPaddingTop();
        if(mBoxStyle.contains(ROUNDED_BOX)){
            try {
                canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8, 8, mLinesPaint);
                canvas.drawRoundRect(startX, top, startX + mCharSize, bottom, 8, 8, mStrokePaint);
            } catch (NoSuchMethodError err) {
                canvas.drawRect(startX, top, startX + mCharSize, bottom, mLinesPaint);
                canvas.drawRect(startX, top, startX + mCharSize, bottom, mStrokePaint);
            }
        }
    }

    private float getCy(final int lenght) {
        if (lenght == 1) {
            return (float) (getHeight() * .7);
        } else if (lenght > 1) {
            return (float) (getHeight() * .6);
        }
        return (float) (getHeight() * .6);
    }

    private float getWidthForText(final int length) {
        if (length == 1) {
            return 50F;
        } else if (length > 1) {
            return 150f;
        }
        return 90f;
    }

    private String getMaskText() {
        int length = String.valueOf(getText()).length();
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) {
            out.append(mMaskCharacter);
        }
        return out.toString();
    }

    /**
     * @param current Is the current char the next character to be input?
     */
    private void updateColorForLines(boolean current) {
        if (current) {
            mLinesPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
            mStrokePaint.setColor(mPrimaryColor);
        }else {
            mStrokePaint.setColor(mSecondaryColor);
            mLinesPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        }
    }

    /**
     * Sets the text size for a Paint object so a given string of text will be a
     * given width.
     *
     * @param desiredWidth
     *            the desired width
     * @param text
     *            the text that should be that width
     */
    private Paint setTextSizeForWidth(float desiredWidth,
                                      String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 50f;
        Paint paint = new Paint();

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
        paint.setColor(mTextColor);
        return paint;
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //No implementation
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //No implementation
    }

    @Override
    public void afterTextChanged(Editable s) {
        //No implementation
    }
}
