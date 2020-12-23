package com.ander.oitipu.lab2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import java.io.File
import java.io.FileOutputStream

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var canvasColor = Color.WHITE

    var penSize = 10f
        set(value) {
            field = value
            initializePen()
        }

    var eraserSize = 10f
        set(value) {
            field = value
            initializeEraser()
        }

    private var startRect = PointF(0f, 0f)
    private var endRect = PointF(0f, 0f)

    private var isDrawMode = true
    var isRectangleMode = false
    var isRoundMode = false
    var isTriangleMode = false

    private val path = Path()
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = penSize
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        }

        bitmap?.let {
            canvas = Canvas(it)
        }

        canvas?.drawColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas?.drawBitmap(it, 0f, 0f, bitmapPaint)
        }
        canvas?.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isDrawMode) {
                    startRect.x = event.x
                    startRect.y = event.y
                    endRect.x = event.x
                    endRect.y = event.y
                } else {
                    path.reset()
                    path.moveTo(event.x, event.y)
                    canvas?.drawPath(path, paint)
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

                if (!isDrawMode) {
                    isRoundMode = false
                    isRectangleMode = false
                    isTriangleMode = false

                    endRect.x = event.x
                    endRect.y = event.y

                    val localPaint = Paint().apply {
                        color = canvasColor
                        strokeWidth = eraserSize
                    }

                    canvas?.drawLine(startRect.x, startRect.y, endRect.x, endRect.y, localPaint)

                    startRect.x = endRect.x
                    startRect.y = endRect.y

                } else {
                    endRect.x = event.x
                    endRect.y = event.y

                    if (!(isRectangleMode || isRoundMode || isTriangleMode)) {
                        canvas?.drawLine(startRect.x, startRect.y, endRect.x, endRect.y, paint)
                        startRect.x = endRect.x
                        startRect.y = endRect.y
                    }
                }

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                when {
                    isRectangleMode -> canvas?.drawRect(
                        startRect.x,
                        startRect.y,
                        endRect.x,
                        endRect.y,
                        paint
                    )
                    isRoundMode -> canvas?.drawOval(
                        startRect.x,
                        startRect.y,
                        endRect.x,
                        endRect.y,
                        paint
                    )
                    isTriangleMode -> canvas?.let { drawTriangle(it, paint, (startRect.x + endRect.x)/2, (startRect.y + endRect.y) / 2,endRect.x - startRect.x  ) }
                }

                invalidate()
            }
        }

        return true
    }

    fun drawTriangle(canvas: Canvas, paint: Paint?, x: Float, y: Float, width: Float) {
        val halfWidth = width / 2
        val path = Path()
        path.moveTo(x.toFloat(), (y - halfWidth).toFloat()) // Top
        path.lineTo((x - halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom left
        path.lineTo((x + halfWidth).toFloat(), (y + halfWidth).toFloat()) // Bottom right
        path.lineTo(x.toFloat(), (y - halfWidth).toFloat()) // Back to Top
        path.close()
        canvas.drawPath(path, paint!!)
    }

    fun initializePen() {
        isDrawMode = true
        paint.apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = penSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        }
    }

    fun initializeEraser() {
        isDrawMode = false
        paint.apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = eraserSize
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }

    fun clear() {
        canvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun changeBackground(color: Int = Color.WHITE) {
        canvasColor = color
        canvas?.drawColor(color)
        invalidate()
    }

    fun setPenColor(@ColorInt color: Int) {
        paint.color = color
    }

    fun getPenColor() = paint.color

    fun loadImage(bitmap: Bitmap) {
        this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas?.setBitmap(this.bitmap)
        bitmap.recycle()
        invalidate()
    }

    fun saveImage() {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val file = File(imagesDir, "image.png")
        val outputStream = FileOutputStream(file)
        bitmap?.compress(CompressFormat.PNG, 100, outputStream)
        outputStream.close()
    }


}