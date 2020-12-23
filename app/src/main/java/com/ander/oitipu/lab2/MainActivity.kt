package com.ander.oitipu.lab2

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ander.oitipu.lab2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val saveImagePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result)
                binding.drawingView.saveImage()
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.apply {
            initializePen()
            initializeEraser()
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            penSize = 10f
            setPenColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }

        binding.ivSettings.setOnClickListener {
            val dialog = SettingsBottomSheetDialogFragment.newInstance(
                binding.drawingView.penSize.toInt(),
                binding.drawingView.eraserSize.toInt(),
                binding.drawingView.getPenColor(),
                binding.drawingView.canvasColor
            )

            dialog.apply {
                show(supportFragmentManager, "SettingsDialog")

                eraserSeekBarCallback = {
                    binding.drawingView.eraserSize = it.toFloat()
                }

                penSeekBarCallback = {
                    binding.drawingView.penSize = it.toFloat()
                }

                penColorCallback = {
                    binding.drawingView.setPenColor(it)
                }

                lineCallback = {
                    binding.drawingView.isRectangleMode = false
                    binding.drawingView.isRoundMode = false
                    binding.drawingView.isTriangleMode = false
                    dialog.dismiss()
                }

                rectangleCallback = {
                    binding.drawingView.isRectangleMode = true
                    binding.drawingView.isRoundMode = false
                    binding.drawingView.isTriangleMode = false
                    dialog.dismiss()
                }

                circleCallback = {
                    binding.drawingView.isRectangleMode = false
                    binding.drawingView.isRoundMode = true
                    binding.drawingView.isTriangleMode = false
                    dialog.dismiss()
                }
                triangleCallback = {
                    binding.drawingView.isTriangleMode = true
                    binding.drawingView.isRectangleMode = false
                    binding.drawingView.isRoundMode = false
                    dialog.dismiss()
                }

                backgroundColorCallback = {
                    binding.drawingView.changeBackground(it)
                }

                onSaveClick = {
                    saveImagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }

        }


    }
}