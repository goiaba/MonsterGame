package edu.luc.etl.cs313.scala.uidemo
package view

import android.app.AlertDialog
import android.content.DialogInterface.OnClickListener
import android.content.{Context, DialogInterface}
import android.graphics.Paint.Style
import android.graphics.{BitmapFactory, Canvas, Color, Paint}
import android.util.{AttributeSet, DisplayMetrics}
import android.view.View
import edu.luc.etl.cs313.scala.uidemo.model._

/**
 * I see spots!
 *
 * @param context
 * @param attrs
 * @param defStyle
 *
 * @author <a href="mailto:android@callmeike.net">Blake Meike</a>
 */
class MonsterView(context: Context, attrs: AttributeSet, defStyle: Int) extends View(context, attrs, defStyle) {

  { setFocusableInTouchMode(true) }

  /** The model underlying this view. */
  private var monsterGame: MonsterGame = _
  private val squareSide: Int = getResources.getDisplayMetrics.densityDpi match {
    case DisplayMetrics.DENSITY_LOW => 36
    case DisplayMetrics.DENSITY_MEDIUM => 48
    case DisplayMetrics.DENSITY_HIGH => 72
    case DisplayMetrics.DENSITY_XHIGH => 96
    case _ => 36
  }
  private val cols: Int = (getWidth/squareSide)-1
  private val rows: Int = (getHeight/squareSide)-1

  def getSquareSide = squareSide
  def getNumberOfCols = cols
  def getNumberOfRows = rows

  /** @param context the rest of the application */
  def this(context: Context) = {
    this(context, null, 0)
    setFocusableInTouchMode(true)
  }

  /**
   * @param context
   * @param attrs
   */
  def this(context: Context, attrs: AttributeSet) = {
    this(context, attrs, 0)
    setFocusableInTouchMode(true)
  }

  /**
   * Injects the model underlying this view.
   *
   * @param monsterGame
   * */
  def setMonsterGame(monsterGame: MonsterGame): Unit = this.monsterGame = monsterGame

  /**
   * Calculate the square vertex coordinate of a given
   *  coordinate. Used to display the monster in specified
   *  cells of the matrix.
   *
   * @param cellPos the row or column of the cell to be
   *                 drawn.
   * @return the coordinate of the top left cell vertex.
   */
  def getTopLeftVertex(cellPos: Int): Float = {
    cellPos * squareSide
  }

  def getRowAndColFromCoordinates(x: Float, y: Float): (Int, Int) = {
    val row = (y / squareSide).toInt
    val col = (x / squareSide).toInt
    (row, col)
  }

  /** @see android.view.View#onDraw(android.graphics.Canvas) */
  override protected def onDraw(canvas: Canvas): Unit = {
    val paint = new Paint
    paint.setStyle(Style.STROKE)
    paint.setColor(Color.BLACK)

    canvas.drawRect(0, 0, getWidth - 1, getHeight - 1, paint)

    val col: Int = (getWidth/squareSide)
    val row: Int = (getHeight/squareSide)

    for (i <- 1 to row)
      canvas.drawLine(0, squareSide * i, getWidth, squareSide * i, paint)

    paint.setColor(Color.RED)
    for (i <- 1 to col)
      canvas.drawLine(squareSide * i, 0, squareSide * i, getHeight, paint)

    if (null == monsterGame) return

    paint.setStyle(Style.FILL)
    paint.setColor(Color.BLACK)
    for (monster <- monsterGame.getMonsters) {
      if (null != monster.cell) {
        val x = getTopLeftVertex(monster.cell.getCol())
        val y = getTopLeftVertex(monster.cell.getRow())
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources,
          if (monster.isVulnerable) R.drawable.vulnerable else R.drawable.mprotected), x, y, paint)
      }
    }
  }

  def showAlertDialog(timeElapsed: Long): Unit = {
    val message = "Time elapsed to kill all monsters on \"%s\" level: " +
      "%dms" format (monsterGame.getLevel().levelDesc, timeElapsed)

    val alertDialog: AlertDialog = new AlertDialog.Builder(getContext()).create()
    alertDialog.setTitle("Game Result")
    alertDialog.setMessage(message)
    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok", new OnClickListener {
      override def onClick(dialog: DialogInterface, which: Int): Unit = dialog.cancel()
    })
    alertDialog.show()
  }
}
