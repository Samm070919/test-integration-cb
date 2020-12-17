package com.pagatodoholdings.posandroid.secciones.calendar.helpers

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SwipeHelper(context: Context?) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var recyclerView: RecyclerView? = null
    private var buttons: MutableList<UnderlayButton> = ArrayList()
    private var gestureDetector: GestureDetector? = null
    private var swipedPos = -1
    private var swipeThreshold = 0.5f
    private var buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>> = HashMap()
    private var recoverQueue: Queue<Int>? = null

    lateinit var swipedItem : View

    private val gestureListener: GestureDetector.SimpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (button in buttons) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }

    private val onTouchListener = View.OnTouchListener { view, e ->

        buttonsBuffer.clear()

        if (swipedPos < 0) {
            return@OnTouchListener false
        }

        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedViewHolder = recyclerView!!.findViewHolderForAdapterPosition(swipedPos)


try {
     swipedItem = swipedViewHolder!!.itemView

}catch(e : ParseException){
        var a = e.toString()
    }catch (i : IOException){
        var a = i.toString()
    }catch(e : OutOfMemoryError){
        var a = e.toString()
    }catch (i : Throwable){
        var a = i.toString()
    }catch(e : Exception){
        var a = e.toString()
    }catch (i : FirebaseAuthException){
        var a = i.toString()
    }catch(e : NullPointerException){
        var a = e.toString()
    }

        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) gestureDetector!!.onTouchEvent(e) else {
                recoverQueue!!.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }
        false
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipedPos != pos) recoverQueue!!.add(swipedPos)
        swipedPos = pos
        if (buttonsBuffer.containsKey(swipedPos)) buttons = buttonsBuffer[swipedPos]!! else buttons.clear()
        buttonsBuffer.clear()
        swipeThreshold = 0.5f * buttons.size * BUTTON_WIDTH
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<UnderlayButton> = ArrayList()
                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer)
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]!!
                }
                translationX = dX * buffer.size * BUTTON_WIDTH / itemView.width
                var a: Int = itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue!!.isEmpty()) {
            val pos = recoverQueue!!.poll()
            if (pos > -1) {
                recyclerView!!.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    private fun drawButtons(c: Canvas, itemView: View, buffer: List<UnderlayButton>, pos: Int, dX: Float) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                    c,
                    RectF(
                            left,
                            itemView.top.toFloat(),
                            right,
                            itemView.bottom.toFloat()
                    ),
                    pos
            )
            right = left
        }
    }

    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        this.recyclerView!!.setOnTouchListener(onTouchListener)
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(this.recyclerView)
    }

    abstract fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: List<UnderlayButton>?)

    interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    class UnderlayButton(private val text: String,
                         private val imageResId: Int,
                         private val color: Int,
                         private val clickListener: UnderlayButtonClickListener) {

        private var pos = 0
        private var clickRegion: RectF? = null

        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(pos)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {

            val p = Paint()
            p.color = color

            c.drawRect(rect, p)

            val textPaint = TextPaint()
            textPaint.textSize = Resources.getSystem().displayMetrics.density * 10
            textPaint.color = Color.WHITE

            val sl = StaticLayout(text,
                    textPaint,
                    rect.width().toInt(),
                    Layout.Alignment.ALIGN_CENTER,
                    1F,
                    1F,
                    false)
            c.save()

            val r = Rect()

            val y = rect.height() / 2f + r.height() / 2f - r.bottom - sl.height / 2
            c.translate(rect.left, rect.top + y)
            sl.draw(c)
            c.restore()

            clickRegion = rect
            this.pos = pos

        }

    }

    companion object {
        const val BUTTON_WIDTH = 210
    }

    init {
        buttons = ArrayList()
        gestureDetector = GestureDetector(context, gestureListener)
        buttonsBuffer = HashMap()
        recoverQueue = object : LinkedList<Int>() {
            override fun add(o: Int): Boolean {
                return if (contains(o)) false else super.add(o)
            }
        }
    }
}