package promitech.kcluster.space2d

import promitech.kcluster.ElementOperations
import kotlin.math.sqrt

class Space2dPointOperations: ElementOperations<Point2d, Centroid2d> {

    override fun createCentroid(): Centroid2d {
        return Centroid2d(0, 0)
    }

    override fun createCentroidFromCentroid(centroid: Centroid2d): Centroid2d {
        return Centroid2d(centroid.x, centroid.y)
    }

    override fun setPosition2(centroid: Centroid2d, c2: Centroid2d) {
        centroid.x = c2.x
        centroid.y = c2.y
    }

    override fun isTheSamePosition(c1: Centroid2d, c2: Centroid2d): Boolean {
        return c1.x == c2.x && c1.y == c2.y
    }

    override fun avr(centroid: Centroid2d, point: Point2d) {
        centroid.x = (point.x + centroid.x) / 2
        centroid.y = (point.y + centroid.y) / 2
    }

    override fun setPosition(centroid: Centroid2d, point: Point2d) {
        centroid.x = point.x
        centroid.y = point.y
    }

    override fun distance(point: Point2d, centroid: Centroid2d): Double {
        val dx = point.x - centroid.x
        val dy = point.y - centroid.y
        return sqrt((dx * dx).toDouble() + (dy * dy).toDouble())
    }

    override fun distanceBetweenPoints(p1: Point2d, p2: Point2d): Double {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt((dx * dx).toDouble() + (dy * dy).toDouble())
    }

    override fun createCentroid(point: Point2d): Centroid2d {
        return Centroid2d(point.x, point.y)
    }
}