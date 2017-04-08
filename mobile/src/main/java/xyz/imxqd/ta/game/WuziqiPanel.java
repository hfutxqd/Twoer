package xyz.imxqd.ta.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import xyz.imxqd.ta.R;
import xyz.imxqd.ta.media.AudioPlayer;

/**
 * Created by imxqd on 17-4-3.
 */

public class WuziqiPanel extends View {
    private static final String TAG = "WuziqiPanel";

    public static final int TYPE_BLACK = 0x0001;
    public static final int TYPE_WHITE = 0x0010;
    private static final int TYPE_UNDEFINED = 0x0100;

    @IntDef({
            TYPE_BLACK, TYPE_WHITE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }

    private int mPanelWidth;       //棋盘宽度
    private float mLineHeight;      //棋盘单行间距
    private int MAX_LINE;//棋盘行列数

    private Paint mPaint = new Paint();
    private int mPanelLineColor;    //棋盘线的颜色

    private Bitmap mWhitePiece;     //白棋的图片
    private Bitmap mBlackPiece;     //黑棋的图片

    //棋子占行距的比例
    private final float RATIO_PIECE_OF_LINE_HEIGHT = 3 * 1.0f / 4;


    private int mDisableType = TYPE_UNDEFINED;
    //是否将要下白棋
    private boolean mIsWhite = false;
    //已下的白棋的列表
    private ArrayList<Point> mWhitePieceArray = new ArrayList<>();
    //已下的黑棋的列表
    private ArrayList<Point> mBlackPieceArray = new ArrayList<>();

    // 防止误触，类似于光标的棋子
    private Point mPieceHolder;

    private Bitmap mHolderBitmap;

    private MediaPlayer mPlayer;

    //游戏是否结束
    private boolean mIsGameOver;

    private final int INIT_WIN = -1;            //游戏开始时的状态
    public static final int WHITE_WIN = 0;      //白棋赢
    public static final int BLACK_WIN = 1;      //黑棋赢
    public static final int NO_WIN = 2;         //和棋

    private int mGameWinResult = INIT_WIN;      //初始化游戏结果

    private OnGameStatusChangeListener listener;//游戏状态监听器

    private int MAX_COUNT_IN_LINE;    //多少颗棋子相邻时赢棋

    //设置游戏状态监听器
    public void setOnGameStatusChangeListener(OnGameStatusChangeListener listener) {
        this.listener = listener;
    }

    public WuziqiPanel(Context context) {
        this(context, null);
    }

    public WuziqiPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取xml中自定义的属性值并对相应的属性赋值
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WuziqiPanel);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attrName = a.getIndex(i);
            switch (attrName) {
                //棋盘背景
                case R.styleable.WuziqiPanel_panel_background:
                    Drawable panelBackground = a.getDrawable(attrName);
                    setBackground(panelBackground);
                    break;
                //棋盘线的颜色
                case R.styleable.WuziqiPanel_panel_line_color:
                    mPanelLineColor = a.getColor(attrName, 0x88000000);
                    break;
                //白棋图片
                case R.styleable.WuziqiPanel_white_piece_img:
                    BitmapDrawable whitePieceBitmap = (BitmapDrawable) a.getDrawable(attrName);
                    mWhitePiece = whitePieceBitmap.getBitmap();
                    break;
                //黑棋图片
                case R.styleable.WuziqiPanel_black_piece_img:
                    BitmapDrawable blackPieceBitmap = (BitmapDrawable) a.getDrawable(attrName);
                    mBlackPiece = blackPieceBitmap.getBitmap();
                    break;
                case R.styleable.WuziqiPanel_max_count_line:
                    MAX_LINE = a.getInteger(attrName, 10);
                    break;
                case R.styleable.WuziqiPanel_max_win_count_piece:
                    MAX_COUNT_IN_LINE = a.getInteger(attrName, 5);
                    break;
            }
        }
        init();
    }

    //初始化游戏数据
    private void init() {
        Drawable drawable = getResources().getDrawable(R.drawable.stone_holder);
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        mHolderBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mHolderBitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        mPlayer = MediaPlayer.create(getContext(), R.raw.hit);

        mPaint.setColor(mPanelLineColor);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖动
        mPaint.setStyle(Paint.Style.FILL);
        if (mWhitePiece == null) {
            mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_white);
        }
        if (mBlackPiece == null) {
            mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_black);
        }
    }

    public List<Point> getWhitePieces() {
        return mWhitePieceArray;
    }

    public List<Point> getBlackPieces() {
        return mBlackPieceArray;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //解决嵌套在ScrollView中时等情况出现的问题
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * RATIO_PIECE_OF_LINE_HEIGHT);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
        mHolderBitmap = Bitmap.createScaledBitmap(mHolderBitmap, pieceWidth + 4, pieceWidth + 4, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieceHolder(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    //重新开始游戏
    public void restartGame() {
        mWhitePieceArray.clear();
        mBlackPieceArray.clear();
        mPieceHolder = null;
        mIsGameOver = false;
        mGameWinResult = INIT_WIN;
        invalidate();
    }

    //检查游戏是否结束
    private void checkGameOver() {
        Log.d(TAG, "checkGameOver: ");
        boolean whiteWin = checkFiveInLine(mWhitePieceArray);
        boolean blackWin = checkFiveInLine(mBlackPieceArray);
        boolean noWin = checkNoWin(whiteWin, blackWin);
        //如果游戏结束,获取游戏结果mGameWinResult
        if (whiteWin) {
            mGameWinResult = WHITE_WIN;
        } else if (blackWin) {
            mGameWinResult = BLACK_WIN;
        } else if (noWin) {
            mGameWinResult = NO_WIN;
        }
        if (whiteWin || blackWin || noWin) {
            mIsGameOver = true;
            //回调游戏状态接口
            if (listener != null) {
                listener.onGameOver(mGameWinResult);
            }
        }
    }

    //检查是否五子连珠
    private boolean checkFiveInLine(List<Point> points) {
        for (Point point : points) {
            int x = point.x;
            int y = point.y;

            boolean checkHorizontal = checkHorizontalFiveInLine(x, y, points);
            boolean checkVertical = checkVerticalFiveInLine(x, y, points);
            boolean checkLeftDiagonal = checkLeftDiagonalFiveInLine(x, y, points);
            boolean checkRightDiagonal = checkRightDiagonalFiveInLine(x, y, points);
            if (checkHorizontal || checkVertical || checkLeftDiagonal || checkRightDiagonal) {
                return true;
            }
        }

        return false;
    }

    //检查向右斜的线上有没有相同棋子的五子连珠
    private boolean checkRightDiagonalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //检查向左斜的线上有没有相同棋子的五子连珠
    private boolean checkLeftDiagonalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //检查竖线上有没有相同棋子的五子连珠
    private boolean checkVerticalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //检查横线上有没有相同棋子的五子连珠
    private boolean checkHorizontalFiveInLine(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }

        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    //检查是否和棋
    private boolean checkNoWin(boolean whiteWin, boolean blackWin) {
        if (whiteWin || blackWin) {
            return false;
        }
        int maxPieces = MAX_LINE * MAX_LINE;
        //如果白棋和黑棋的总数等于棋盘格子数,说明和棋
        if (mWhitePieceArray.size() + mBlackPieceArray.size() == maxPieces) {
            return true;
        }
        return false;
    }

    private void drawPieceHolder(Canvas canvas) {
        if (mPieceHolder != null) {
            canvas.drawBitmap(mHolderBitmap,
                    (mPieceHolder.x + (1 - mHolderBitmap.getHeight() / mLineHeight) / 2) * mLineHeight,
                    (mPieceHolder.y + (1 - mHolderBitmap.getHeight() / mLineHeight) / 2) * mLineHeight + 1, null);
        }
    }

    //绘制棋子
    private void drawPiece(Canvas canvas) {
        for (int i = 0, n = mWhitePieceArray.size(); i < n; i++) {
            Point whitePoint = mWhitePieceArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,
                    (whitePoint.y + (1 - RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight, null);
        }
        for (int i = 0, n = mBlackPieceArray.size(); i < n; i++) {
            Point blackPoint = mBlackPieceArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight,
                    (blackPoint.y + (1 - RATIO_PIECE_OF_LINE_HEIGHT) / 2) * mLineHeight, null);
        }
    }

    //绘制棋盘
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);//画横线
            canvas.drawLine(y, startX, y, endX, mPaint);//画竖线
        }

    }

    /**
     * 指定棋盘上的棋子
     * @param whites 白色棋子
     * @param blacks 黑色棋子
     */
    public void initPanel(@NonNull ArrayList<Point> whites,@NonNull ArrayList<Point> blacks) {
        mWhitePieceArray = whites;
        mBlackPieceArray = blacks;
        checkGameOver();
        if (blacks.size() > whites.size()) {
            // 上一次是黑子落子
            mIsWhite = true;
            mPieceHolder = blacks.get(blacks.size() - 1);
        } else {
            // 上一次是白子落子
            mIsWhite = false;
            mPieceHolder = whites.get(whites.size() - 1);
        }
        invalidate();
    }


    /**
     * 落子
     * @param type 棋子类型
     * @param p 落点
     * @return 落子是否成功
     */
    public boolean addPiece(@Type int type, Point p) {
        Log.d(TAG, "addPiece#type: " + type);
        Log.d(TAG, "addPiece#isWhite: " + mIsWhite);
        if (mWhitePieceArray.contains(p) || mBlackPieceArray.contains(p)) {
            return false;
        }
        if (mIsWhite && type == TYPE_WHITE) {
            Log.d(TAG, "addWhitePiece: " + p);
            mPlayer.start();
            mWhitePieceArray.add(p);
            mIsWhite = false;
            mPieceHolder = p;
            invalidate();
            return true;
        } else if (!mIsWhite && type == TYPE_BLACK) {
            Log.d(TAG, "addBlackPiece: " + p);
            mPlayer.start();
            mBlackPieceArray.add(p);
            mIsWhite = true;
            mPieceHolder = p;
            invalidate();
            return true;
        }

        return false;
    }

    /**
     * 手动指定当前落子者
     * @param type
     */
    public void setCurrentPlaer(@Type int type) {
        if (type == TYPE_BLACK) {
            mIsWhite = false;
        } else {
            mIsWhite = true;
        }
    }

    /**
     * 设置某种棋子不能通过屏幕点击的方式添加
     * @param type 棋子类型
     */
    public void setTouchDisable(int type) {
        mDisableType = type;
    }

    /**
     * 撤回上一次落子
     */
    public void undo() {
        if (mIsWhite && mBlackPieceArray.size() > 0) {
            mBlackPieceArray.remove(mBlackPieceArray.size() - 1);
        } else if (!mIsWhite && mWhitePieceArray.size() > 0) {
            mWhitePieceArray.remove(mBlackPieceArray.size() - 1);
        }
        mIsWhite = !mIsWhite;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }
        if (mDisableType == (TYPE_BLACK | TYPE_WHITE)) {
            return false;
        }
        if (mIsWhite && mDisableType == TYPE_WHITE) {
            return false;
        } else if (!mIsWhite && mDisableType == TYPE_BLACK) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x, y);
            if (mWhitePieceArray.contains(p) || mBlackPieceArray.contains(p)) {
                return false;
            }

            if (!p.equals(mPieceHolder)) {
                mPieceHolder = p;
                invalidate();
                return true;
            } else {
                if (mIsWhite) {
                    addPiece(TYPE_WHITE, p);
                    if (listener != null) {
                        listener.onPlacePiece(TYPE_WHITE, p);
                    }

                } else {
                    addPiece(TYPE_BLACK, p);
                    if (listener != null) {
                        listener.onPlacePiece(TYPE_BLACK, p);
                    }
                }
            }
            invalidate();

            return true;
        }
        return true;
    }

    //根据触摸点获取最近的格子位置
    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }


    /**
     * 当View被销毁时需要保存游戏数据
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    //保存游戏数据
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhitePieceArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackPieceArray);
        return bundle;
    }

    //恢复游戏数据
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhitePieceArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackPieceArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
