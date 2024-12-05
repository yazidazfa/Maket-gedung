package com.xperiencelabs.arapp

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.material.setExternalTexture
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.VideoNode

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    lateinit var placeButton: ExtendedFloatingActionButton
    lateinit var resetButton: ExtendedFloatingActionButton
    private lateinit var modelNode: ArModelNode
    private lateinit var mediaPlayer:MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED

        }

        mediaPlayer = MediaPlayer.create(this,R.raw.ad)
        placeButton = findViewById(R.id.place)
        resetButton = findViewById(R.id.resetButton)

        placeButton.setOnClickListener {
            placeModel()
        }

        resetButton.setOnClickListener {
            resetModel()
        }

        modelNode = ArModelNode(sceneView.engine,PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/untitled4.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(0.0f)
            )
            {
                sceneView.planeRenderer.isVisible = true
                val materialInstance = it.materialInstances[0]
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
                resetButton.visibility = if (it != null) View.VISIBLE else View.GONE
            }
        }
        sceneView.addChild(modelNode)

    }

   private fun placeModel(){
       modelNode.anchor()
       sceneView.planeRenderer.isVisible = false
       resetButton.visibility = View.VISIBLE
   }

    private fun resetModel() {
        modelNode.detachAnchor()  // Unplace the model
        sceneView.removeChild(modelNode) // Remove the model from the scene

        // Create a new instance of ArModelNode
        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/untitled4.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(0.0f)
            ) {
                sceneView.planeRenderer.isVisible = true // Ensure the plane is visible again
            }

            onAnchorChanged = {
                placeButton.isGone = it != null
                resetButton.visibility = if (it != null) View.VISIBLE else View.GONE
            }
        }
        // Add the new model node to the scene
        sceneView.addChild(modelNode)

        // Manage button visibility
        placeButton.isGone = false  // Show the place button
        resetButton.visibility = View.GONE  // Hide the reset button
    }
    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}