package promitech.kcluster.libgdx

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import promitech.kcluster.KCluster
import promitech.kcluster.space2d.Centroid2d
import promitech.kcluster.space2d.Point2d

class ClustersDrawPanel(
    var kCluster: KCluster<Point2d, Centroid2d>,
    val renderer: ShapeRenderer
): Widget() {

    init {
        addListener(object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                kCluster.addPoint(Point2d(x.toInt() - 20, y.toInt() + 5))
            }
        })
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()

        drawPoints()

        batch.begin()

        super.draw(batch, parentAlpha)
    }

    private fun drawPoints() {
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        kCluster.forEachPoint { point, clusterIndex ->
            renderer.color = colorByClusterIndex(clusterIndex)
            renderer.circle(toScreenX(point), toScreenY(point), 5f)
        }
        renderer.end()

        renderer.begin(ShapeRenderer.ShapeType.Line)
        kCluster.forEachCentroid { centroid, clusterIndex ->
            renderer.color = colorByClusterIndex(clusterIndex)
            renderer.rect(toScreenX(centroid) - 5, toScreenY(centroid) - 5, 10f, 10f)
        }
        renderer.end()
    }

    private fun toScreenX(point: Point2d): Float {
        return (point.x + 20).toFloat()
    }

    private fun toScreenY(point: Point2d): Float {
        return (point.y + 20).toFloat()
    }

    private fun toScreenX(point: Centroid2d): Float {
        return (point.x + 20).toFloat()
    }

    private fun toScreenY(point: Centroid2d): Float {
        return (point.y + 20).toFloat()
    }

    private fun colorByClusterIndex(clusterIndex: Int): Color {
        return when (clusterIndex) {
            0 -> Color.GRAY
            1 -> Color.GREEN
            2 -> Color.RED
            3 -> Color.CYAN
            4 -> Color.YELLOW
            else -> Color.BLUE
        }
    }
}