package com.ander.oitipu.lab2

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.os.bundleOf
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ander.oitipu.lab2.databinding.FragmentSettingBottomSheetBinding
import kotlinx.android.synthetic.main.fragment_setting_bottom_sheet.*

class SettingsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(brushSize: Int, eraserSize: Int, penColor: Int, backgroundColor: Int) =
            SettingsBottomSheetDialogFragment().apply {
                arguments = bundleOf(
                    Pair("brushSize", brushSize),
                    Pair("eraserSize", eraserSize),
                    Pair("penColor", penColor),
                    Pair("backgroundColor", backgroundColor)
                )
            }
    }

    private lateinit var binding: FragmentSettingBottomSheetBinding

    var penSeekBarCallback: ((Int) -> Unit)? = null
    var eraserSeekBarCallback: ((Int) -> Unit)? = null
    var penColorCallback: ((Int) -> Unit)? = null
    var backgroundColorCallback: ((Int) -> Unit)? = null

    var lineCallback: (() -> Unit)? = null
    var rectangleCallback: (() -> Unit)? = null
    var circleCallback: (() -> Unit)? = null
    var triangleCallback: (() -> Unit)? = null

    var onSaveClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            sbEraser.apply {
                progress = requireArguments().getInt("eraserSize")
                max = 100

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        eraserSeekBarCallback?.invoke(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })

            }

            sbPen.apply {
                progress = requireArguments().getInt("brushSize")
                max = 100

                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        penSeekBarCallback?.invoke(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }

            btnChangeColor.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireArguments().getInt("penColor"))

                setOnClickListener {
                    ColorPickerDialogBuilder.with(btn_change_color.context)
                        .setTitle("Choose color")
                        .initialColor(R.color.colorPrimaryDark)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener {
                            penColorCallback?.invoke(it)
                            binding.btnChangeColor.backgroundTintList = ColorStateList.valueOf(it)
                        }.build()
                        .show()
                }
            }

            btnCircle.setOnClickListener {
                circleCallback?.invoke()
            }

            btnLine.setOnClickListener {
                lineCallback?.invoke()
            }

            btnRectangle.setOnClickListener {
                rectangleCallback?.invoke()
            }

//             btnTriangle.setOnClickListener {
//                triangleCallback?.invoke()
//            }

            btnChangeFill.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireArguments().getInt("backgroundColor"))

                setOnClickListener {
                    ColorPickerDialogBuilder.with(btn_change_color.context)
                        .setTitle("Choose background color")
                        .initialColor(R.color.colorPrimaryDark)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener {
                            backgroundColorCallback?.invoke(it)
                            binding.btnChangeFill.backgroundTintList = ColorStateList.valueOf(it)
                        }.build()
                        .show()
                }

            }

            btnSave.setOnClickListener {
                onSaveClick?.invoke()
            }

        }

    }


}