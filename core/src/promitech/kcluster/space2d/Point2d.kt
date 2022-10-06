package promitech.kcluster.space2d

import kotlin.random.Random

class Point2d(val x: Int, val y: Int) {

    companion object {
        fun randomPoints(number: Int, minX: Int, minY: Int, maxX: Int, maxY: Int): ArrayList<Point2d> {
            val list = ArrayList<Point2d>(number)
            for (i in 1 .. number) {
                list.add(Point2d(Random.nextInt(maxX - minX) + minX, Random.nextInt(maxY - minY) + minY))
            }
            return list
        }
    }
}