package promitech.kcluster.libgdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import promitech.kcluster.KCluster
import promitech.kcluster.OptimalSilhouetteCoefficient
import promitech.kcluster.max
import promitech.kcluster.space2d.Centroid2d
import promitech.kcluster.space2d.Point2d
import promitech.kcluster.space2d.Space2dPointOperations
import promitech.kcluster.stdoutPrint

class MyGdxGame : ApplicationAdapter() {

    lateinit var batch: SpriteBatch
    lateinit var renderer: ShapeRenderer
    lateinit var stage: Stage
    lateinit var clustersDrawPanel: ClustersDrawPanel
    lateinit var mySkin: Skin

    override fun create() {
        batch = SpriteBatch()
        renderer = ShapeRenderer()
        mySkin = Skin(Gdx.files.internal("uiskin.json"))

        stage = Stage()

        Gdx.input.inputProcessor = stage

        val buttonLayout = createButtons()
        clustersDrawPanel = ClustersDrawPanel(KCluster(ArrayList(), Space2dPointOperations()), renderer)

        val layout = Table()
        layout.setFillParent(true)
        layout.add(clustersDrawPanel).fill().expand().row()
        layout.add(buttonLayout).fillX().expandX().row()
        stage.addActor(layout)
    }

    private fun createButtons(): Table {
        val assignPointsToCentroidsButton = TextButton("assignPointsToCentroids", mySkin)
        assignPointsToCentroidsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("assignPointsToCentroidsClusters")
                clustersDrawPanel.kCluster.assignPointsToCentroidsClusters()
            }
        })

        val recalculateCentroidsButton = TextButton("recalculateCentroids", mySkin)
        recalculateCentroidsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("recalculate centroids")
                val recalculateCentroids = clustersDrawPanel.kCluster.recalculateCentroids()
                println("centroids change position: $recalculateCentroids")
                printCentroids()
            }
        })
        val fullStableClusterButton = TextButton("fullStableCluster", mySkin)
        fullStableClusterButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("calculate full stable cluster")
                var loops = clustersDrawPanel.kCluster.calculateForCentroidNumber(3)
                println("full stable clusters in $loops loops")
            }
        })

        val newClusterButton = TextButton("new cluster", mySkin)
        newClusterButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("generate new cluster")
                clustersDrawPanel.kCluster = generateNewCluster()
            }
        })
        val clearButton = TextButton("new cluster", mySkin)
        clearButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("generate new clear cluster")
                clustersDrawPanel.kCluster = KCluster(ArrayList(), Space2dPointOperations())
            }
        })
        val optimalClusterNumber = TextButton("optimal cluster number elbow", mySkin)
        optimalClusterNumber.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("generate optional cluster number")
                val newClusters = clustersDrawPanel.kCluster.copy()

                for (k in 1 .. Math.min(10, newClusters.maxAllowedClusters())) {
                    newClusters.calculateForCentroidNumber(k)
                    println("cluster k = $k : " + newClusters.elbow())
                }
            }
        })
        val optimalClusterNumber2 = TextButton("optimal cluster number SilhouetteCoefficient", mySkin)
        optimalClusterNumber2.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                println("optimal cluster number SilhouetteCoefficient")
                val newClusters = clustersDrawPanel.kCluster.copy()
                val optimalSilhouetteCoefficient = OptimalSilhouetteCoefficient(newClusters, Space2dPointOperations())
                val optimalClusterNumberList = optimalSilhouetteCoefficient.calculate()
                optimalClusterNumberList.stdoutPrint()
                val max = optimalClusterNumberList.max()
                if (max != null) {
                    println("the best cluster number: " + max.clusterNumber)
                    clustersDrawPanel.kCluster.calculateForCentroidNumber(max.clusterNumber)
                }
            }
        })


        val buttonsLayout = Table()
        buttonsLayout.add(assignPointsToCentroidsButton).fillX().expandX()
        buttonsLayout.add(recalculateCentroidsButton).fillX().expandX()
        buttonsLayout.add(fullStableClusterButton).fillX().expandX()
        buttonsLayout.add(newClusterButton).fillX().expandX()
        buttonsLayout.add(clearButton).fillX().expandX()
        buttonsLayout.add(optimalClusterNumber).fillX().expandX()
        buttonsLayout.add(optimalClusterNumber2).fillX().expandX()
        return buttonsLayout
    }

    private fun printCentroids() {
        clustersDrawPanel.kCluster.forEachCentroid { centroid, clusterIndex ->
            println("centroid[$clusterIndex] = " + centroid.x + ", " + centroid.y)
        }
    }

    fun generateNewCluster(): KCluster<Point2d, Centroid2d> {
        val points = Point2d.randomPoints(80,
            20, 20,
            clustersDrawPanel.width.toInt() - 20,clustersDrawPanel.height.toInt() - 20
        )
        val kCluster = KCluster(points, Space2dPointOperations())
        kCluster.setRandomCentroids(3)
        return kCluster
    }

    override fun render() {
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ScreenUtils.clear(0F, 0F, 0F, 1F)

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        batch.dispose()
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        stage.getViewport().update(width, height, true);
    }
}