package promitech.kcluster

class  KCluster<KP, CNTRD>(
    val points: ArrayList<KP>,
    val elementOperations: ElementOperations<KP, CNTRD>
) where CNTRD: Centroid {

    val centroids = ArrayList<CNTRD>()
    val pointsCentroidsIndex = com.badlogic.gdx.utils.IntArray(points.size)

    init {
        for (i in 0 .. points.size-1) {
            pointsCentroidsIndex.add(0)
        }
    }

    fun addPoint(point: KP) {
        points.add(point)
        pointsCentroidsIndex.add(0)
    }

    fun calculateForCentroidNumber(centroidNumber: Int): Int {
        if (centroidNumber > points.size) {
            throw IllegalArgumentException("incorrect centroid number ${centroidNumber} max: ${points.size}")
        }
        setRandomCentroids(centroidNumber)
        assignPointsToCentroidsClusters()

        var loops = 0
        while (recalculateCentroids()) {
            assignPointsToCentroidsClusters()
            loops++
            if (loops > 1000) {
                break;
            }
        }
        return loops
    }

    fun assignPointsToCentroidsClusters() {
        var theBestDistance = Double.MAX_VALUE
        var theBestCentroidIndex: Int = 0
        var tmpDistance = 0.0

        for (pointIndex in 0 .. points.size-1) {
            val point = points.get(pointIndex)

            theBestDistance = Double.MAX_VALUE
            theBestCentroidIndex = 0

            for (centroidIndex in 1 .. centroids.size-1) {
                val centroid = centroids[centroidIndex]
                tmpDistance = elementOperations.distance(point, centroid)
                if (tmpDistance < theBestDistance) {
                    theBestDistance = tmpDistance
                    theBestCentroidIndex = centroidIndex
                }
            }

            if (theBestCentroidIndex != 0) {
                pointsCentroidsIndex.set(pointIndex, theBestCentroidIndex)
            }
        }
    }


    fun recalculateCentroids(): Boolean {
        val tmpCentroid = elementOperations.createCentroid()
        var changeCentroidsPosition = false

        for (centroidIndex in 1 .. centroids.size-1) {
            val centroid = centroids[centroidIndex]

            var foundFirst = false

            for (pointIndex in 0 .. points.size-1) {
                val point = points.get(pointIndex)
                if (pointsCentroidsIndex.get(pointIndex) == centroidIndex) {
                    if (!foundFirst) {
                        elementOperations.setPosition(tmpCentroid, point)
                        foundFirst = true
                    } else {
                        elementOperations.avr(tmpCentroid, point)
                    }
                }
            }

            if (!elementOperations.isTheSamePosition(tmpCentroid, centroid)) {
                changeCentroidsPosition = true
            }
            elementOperations.setPosition2(centroid, tmpCentroid)
        }
        return changeCentroidsPosition
    }

    fun setRandomCentroids(clusterNumber: Int) {
        centroids.clear()
        centroids.add(elementOperations.createCentroid())
        for (i in 1 .. clusterNumber) {
            if (i < points.size) {
                centroids.add(elementOperations.createCentroid(points[i - 1]))
            }
        }
        for (i in 0 .. pointsCentroidsIndex.size-1) {
            pointsCentroidsIndex.set(i, 0)
        }
    }

    inline fun forEachPoint(action: (point: KP, clusterIndex: Int) -> Unit) {
        for (pointIndex in 0 .. points.size-1) {
            val point = points.get(pointIndex)
            action.invoke(point, pointsCentroidsIndex.get(pointIndex))
        }
    }

    inline fun forEachPoint(clusterIndex: Int, action: (point: KP) -> Unit) {
        for (pointIndex in 0 .. points.size-1) {
            if (clusterIndex == pointsCentroidsIndex.get(pointIndex)) {
                action.invoke(points.get(pointIndex))
            }
        }
    }

    inline fun forEachCentroid(action: (centroid: CNTRD, clusterIndex: Int) -> Unit) {
        for (centroidIndex in 1 .. centroids.size-1) {
            val centroid = centroids[centroidIndex]
            action.invoke(centroid, centroidIndex)
        }
    }

    fun copy(): KCluster<KP, CNTRD> {
        val newCluster = KCluster(ArrayList(points), elementOperations)
        for (centroid in centroids) {
            newCluster.centroids.add(elementOperations.createCentroidFromCentroid(centroid))
        }
        newCluster.pointsCentroidsIndex.clear()
        newCluster.pointsCentroidsIndex.addAll(pointsCentroidsIndex)
        return newCluster
    }

    fun elbow(): Double {
        val allAvr = Avr()
        val avrInCluster = Avr()

        forEachCentroid { centroid, clusterIndex ->
            avrInCluster.reset()
            forEachPoint(clusterIndex, { point ->
                val distance = elementOperations.distance(point, centroid)
                avrInCluster.add(distance)
            })
            allAvr.add(avrInCluster.value)
        }
        return allAvr.value
    }

    fun maxAllowedClusters(): Int {
        return points.size
    }

}