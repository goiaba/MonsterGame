package edu.luc.cs.comp413.scala.monstergame
package view

import android.app.AlertDialog
import android.content.DialogInterface.OnClickListener
import android.content.{Context, DialogInterface}
import android.graphics.Paint.Style
import android.graphics.{BitmapFactory, Canvas, Color, Paint}
import android.util.{AttributeSet, DisplayMetrics, Log}
import android.view.View
import edu.luc.cs.comp413.scala.monstergame.model._

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

  /**
   * This value is used to calculate the exact size of each rectangle that represents cells
   *  from the model. Each cell will have sides as close as possible of the value chosen here.
   */
  private val cellApproximateSideSize: Int = getResources.getDisplayMetrics.densityDpi match {
    case DisplayMetrics.DENSITY_LOW => 36
    case DisplayMetrics.DENSITY_MEDIUM => 48
    case DisplayMetrics.DENSITY_HIGH => 72
    case DisplayMetrics.DENSITY_XHIGH => 96
    case _ => 36
  }

  /**
   * This lazy initialized member will be populated with the exact rectangle side width that represents
   *  a cell in the model
   */
  private lazy val cellWidthSideSize = getWidth/math.floor(getWidth/cellApproximateSideSize).toFloat

  /**
   * This lazy initialized member will be populated with the exact rectangle side height that represents
   *  a cell in the model
   */
  private lazy val cellHeightSideSize = getHeight/math.floor(getHeight/cellApproximateSideSize).toFloat

  /**
   * Represents the number of rows in the model
   *
   * @return the number of rows in the model
   */
  lazy val getNumberOfRows: Int = (getHeight/cellHeightSideSize).toInt

  /**
   * Represents the number of cols in the model
   *
   * @return the number of cols in the model
   */
  lazy val getNumberOfCols: Int = (getWidth/cellWidthSideSize).toInt

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



  def getRowAndColFromCoordinates(x: Float, y: Float): (Int, Int) = {
    val row = (y / cellHeightSideSize).toInt
    val col = (x / cellWidthSideSize).toInt
    (row, col)
  }

  /** @see android.view.View#onDraw(android.graphics.Canvas) */
  override protected def onDraw(canvas: Canvas): Unit = {
    val paint = new Paint
    paint.setStyle(Style.STROKE)
    paint.setColor(Color.BLACK)

    canvas.drawRect(0, 0, getWidth - 1, getHeight - 1, paint)

    for (i <- 1 to getNumberOfRows)
      canvas.drawLine(0, cellHeightSideSize * i, getWidth, cellHeightSideSize * i, paint)

    for (i <- 1 to getNumberOfCols)
      canvas.drawLine(cellWidthSideSize * i, 0, cellWidthSideSize * i, getHeight, paint)

    if (null == monsterGame) return

    paint.setStyle(Style.FILL)
    for (monster <- monsterGame.getMonsters) {
      if (null != monster.getCell) {
        val x = getLeftVertex(monster.getCell.getCol)
        val y = getTopVertex(monster.getCell.getRow)
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources,
        if (monster.isVulnerable) R.drawable.vulnerable else R.drawable.mprotected), x, y, paint)
      }
    }
  }

  /**
   * Draws an alert dialog after the game ends containing game results. Used by
   *  controller class
   *
   * @param timeElapsed the time elapsed by the user to kill all monsters
   */
  def showAlertDialog(timeElapsed: Long): Unit = {
    val message = "Time elapsed to kill all monsters on \"%s\" level: " +
      "%dms" format (monsterGame.getLevel.levelDesc, timeElapsed)

    val alertDialog: AlertDialog = new AlertDialog.Builder(getContext).create()
    alertDialog.setTitle("Game Result")
    alertDialog.setMessage(message)
    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"Ok", new OnClickListener {
      override def onClick(dialog: DialogInterface, which: Int): Unit = dialog.cancel()
    })
    alertDialog.show()
  }

  /**
   * Calculate the square vertex coordinate of a given coordinate. Used to display
   *  the monster in a specified cell of the matrix.
   *
   * @param cellPos the row of the cell to be drawn.
   * @return the vertical (y) coordinate of top cell vertex.
   */
  private def getTopVertex(cellPos: Int): Float = cellPos * cellHeightSideSize

  /**
   * Calculate the square vertex coordinate of a given coordinate. Used to display
   *  the monster in a specified cell of the matrix.
   *
   * @param cellPos the column of the cell to be drawn.
   * @return the horizontal (x) coordinate of left cell vertex.
   */
  private def getLeftVertex(cellPos: Int): Float = cellPos * cellWidthSideSize



}
