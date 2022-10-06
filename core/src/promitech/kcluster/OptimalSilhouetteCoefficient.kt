package promitech.kcluster

fun List<ClusterSValue>.stdoutPrint() {
    println("ClusterSValue.size: " + this.size)
    for (clusterSValue in this) {
        println("cluster ${clusterSValue.clusterNumber} S: ${clusterSValue.sValue}")
    }
}

fun List<ClusterSValue>.max(): ClusterSValue? {
    return this.maxBy { cv -> cv.sValue }
}

data class ClusterSValue(val clusterNumber: Int, val sValue: Double)

class OptimalSilhouetteCoefficient<KP, CNTRD>(
    private val kCluster: KCluster<KP, CNTRD>,
    val elementOperations: ElementOperations<KP, CNTRD>
) where CNTRD: Centroid {

    fun calculate(): List<ClusterSValue> {
        val sValues = ArrayList<ClusterSValue>()

        for (clusterNumber in 2 .. Math.min(10, kCluster.maxAllowedClusters())) {
            kCluster.calculateForCentroidNumber(clusterNumber)
            println("cluster number $clusterNumber")
            val si = silhouetteCoefficient()
            sValues.add(ClusterSValue(clusterNumber, si))
        }
        return sValues
    }

    private fun silhouetteCoefficient(): Double {
        val tmpAvr = Avr()
        val sAvr = Avr()

        kCluster.forEachPoint { point, pointClusterIndex ->
            val a = a(tmpAvr, point, pointClusterIndex)
            val b = b(tmpAvr, point, pointClusterIndex)
            val s = (b - a) / Math.max(a, b)
            sAvr.add(s)
        }
        return sAvr.value
    }

    private fun a(tmpAvr: Avr, point: KP, pointClusterIndex: Int): Double {
        tmpAvr.reset()
        kCluster.forEachPoint(pointClusterIndex, { p ->
            tmpAvr.add(elementOperations.distanceBetweenPoints(point, p))
        })
        return tmpAvr.value
    }

    private fun b(tmpAvr: Avr, point: KP, pointClusterIndex: Int): Double {
        val theClosestClusterIndex = theClosestCluster(point, pointClusterIndex)
        if (theClosestClusterIndex == 0) {
            error("there is not closest cluster to cluster: ${pointClusterIndex}")
        }

        tmpAvr.reset()
        kCluster.forEachPoint { p, pIndex ->
            if (pIndex == theClosestClusterIndex) {
                tmpAvr.add(elementOperations.distanceBetweenPoints(point, p))
            }
        }
        return tmpAvr.value
    }

    private fun theClosestCluster(point: KP, pointClusterIndex: Int): Int {
        var theBestDistance = Double.MAX_VALUE
        var theBestClusterIndex = 0

        kCluster.forEachCentroid { centroid, clusterIndex ->
            if (clusterIndex != pointClusterIndex) {
                val distance = kCluster.elementOperations.distance(point, centroid)
                if (distance < theBestDistance) {
                    theBestDistance = distance
                    theBestClusterIndex = clusterIndex
                }
            }
        }
        return theBestClusterIndex
    }
}