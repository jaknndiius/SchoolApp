import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class ImageDisplayView : View, OnTouchListener {
    var mContext: Context
    var mCanvas: Canvas? = null
    var mBitmap: Bitmap? = null
    var mPaint: Paint? = null
    var lastX = 0
    var lastY = 0
    var sourceBitmap: Bitmap? = null
    var mMatrix: Matrix? = null
    var sourceWidth = 0.0f
    var sourceHeight = 0.0f
    var bitmapCenterX = 0f
    var bitmapCenterY = 0f
    var scaleRatio = 0f
    var totalScaleRatio = 0f
    var displayWidth = 0.0f
    var displayHeight = 0.0f
    var displayCenterX = 0
    var displayCenterY = 0
    var startX = 0f
    var startY = 0f
    var oldDistance = 0.0f
    var oldPointerCount = 0
    var isScrolling = false
    var distanceThreshold = 3.0f

    companion object {
        private const val TAG = "ImageDisplayView"
        var MAX_SCALE_RATIO = 5.0f
        var MIN_SCALE_RATIO = 0.1f
    }

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        init()
    }

    private fun init() {
        mPaint = Paint()
        mMatrix = Matrix()
        lastX = -1
        lastY = -1
        setOnTouchListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w > 0 && h > 0) {
            newImage(w, h)
            redraw()
        }
    }

    fun newImage(width: Int, height: Int) {
        val img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas()
        canvas.setBitmap(img)
        mBitmap = img
        mCanvas = canvas
        displayWidth = width.toFloat()
        displayHeight = height.toFloat()
        displayCenterX = width / 2
        displayCenterY = height / 2
    }

    fun drawBackground(canvas: Canvas?) {
        canvas?.drawColor(Color.BLACK)
    }

    override fun onDraw(canvas: Canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap!!, 0f, 0f, null)
        }
    }

    fun setImageData(image: Bitmap?) {
        recycle()
        sourceBitmap = image
        sourceWidth = sourceBitmap!!.width.toFloat()
        sourceHeight = sourceBitmap!!.height.toFloat()
        bitmapCenterX = (sourceBitmap!!.width / 2).toFloat()
        bitmapCenterY = (sourceBitmap!!.height / 2).toFloat()
        scaleRatio = 1.0f
        totalScaleRatio = 1.0f
    }

    fun recycle() {
        if (sourceBitmap != null) {
            sourceBitmap!!.recycle()
        }
    }

    fun redraw() {
        if (sourceBitmap == null) {
            Log.d(TAG, "sourceBitmap is null in redraw().")
            return
        }
        drawBackground(mCanvas)
        val originX = (displayWidth - sourceBitmap!!.width.toFloat()) / 2.0f
        val originY = (displayHeight - sourceBitmap!!.height.toFloat()) / 2.0f
        mCanvas!!.translate(originX, originY)
        mCanvas!!.drawBitmap(sourceBitmap!!, mMatrix!!, mPaint)
        mCanvas!!.translate(-originX, -originY)
        invalidate()
    }

    override fun onTouch(v: View, ev: MotionEvent): Boolean {
        val action = ev.action
        val pointerCount = ev.pointerCount
        Log.d(TAG, "Pointer Count : $pointerCount")
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (pointerCount == 1) {
                    val curX = ev.x
                    val curY = ev.y
                    startX = curX
                    startY = curY
                } else if (pointerCount == 2) {
                    oldDistance = 0.0f
                    isScrolling = true
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointerCount == 1) {
                    if (isScrolling) {
                        return true
                    }
                    val curX = ev.x
                    val curY = ev.y
                    if (startX == 0.0f) {
                        startX = curX
                        startY = curY
                        return true
                    }
                    val offsetX = startX - curX
                    val offsetY = startY - curY
                    if (oldPointerCount == 2) {
                    } else {
                        Log.d(
                            TAG,
                            "ACTION_MOVE : $offsetX, $offsetY"
                        )
                        if (totalScaleRatio > 1.0f) {
                            moveImage(-offsetX, -offsetY)
                        }
                        startX = curX
                        startY = curY
                    }
                } else if (pointerCount == 2) {
                    val x1 = ev.getX(0)
                    val y1 = ev.getY(0)
                    val x2 = ev.getX(1)
                    val y2 = ev.getY(1)
                    val dx = x1 - x2
                    val dy = y1 - y2
                    val distance = Math.sqrt(dx * dx + dy * dy.toDouble()).toFloat()
                    var outScaleRatio = 0.0f
                    if (oldDistance == 0.0f) {
                        oldDistance = distance
                    }
                    if (distance > oldDistance) {
                        if (distance - oldDistance < distanceThreshold) {
                            return true
                        }
                        outScaleRatio = scaleRatio + oldDistance / distance * 0.05f
                    } else if (distance < oldDistance) {
                        if (oldDistance - distance < distanceThreshold) {
                            return true
                        }
                        outScaleRatio = scaleRatio - distance / oldDistance * 0.05f
                    }
                    if (outScaleRatio < MIN_SCALE_RATIO || outScaleRatio > MAX_SCALE_RATIO) {
                        Log.d(
                            TAG,
                            "Invalid scaleRatio : $outScaleRatio"
                        )
                    } else {
                        Log.d(
                            TAG,
                            "Distance : $distance, ScaleRatio : $outScaleRatio"
                        )
                        scaleImage(outScaleRatio)
                    }
                    oldDistance = distance
                }
                oldPointerCount = pointerCount
            }
            MotionEvent.ACTION_UP -> {
                if (pointerCount == 1) {
                    val curX = ev.x
                    val curY = ev.y
                    val offsetX = startX - curX
                    val offsetY = startY - curY
                    if (oldPointerCount == 2) {
                    } else {
                        moveImage(-offsetX, -offsetY)
                    }
                } else {
                    isScrolling = false
                }
                return true
            }
        }
        return true
    }

    private fun scaleImage(inScaleRatio: Float) {
        Log.d(TAG, "scaleImage() called : $inScaleRatio")
        mMatrix!!.postScale(inScaleRatio, inScaleRatio, bitmapCenterX, bitmapCenterY)
        mMatrix!!.postRotate(0f)
        totalScaleRatio = totalScaleRatio * inScaleRatio
        redraw()
    }

    private fun moveImage(offsetX: Float, offsetY: Float) {
        Log.d(
            TAG,
            "moveImage() called : $offsetX, $offsetY"
        )
        mMatrix!!.postTranslate(offsetX, offsetY)
        redraw()
    }
}