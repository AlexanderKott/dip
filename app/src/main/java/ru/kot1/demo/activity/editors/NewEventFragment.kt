package ru.kot1.demo.activity.editors

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import dagger.hilt.android.AndroidEntryPoint
import ru.kot1.demo.R
import ru.kot1.demo.databinding.FragmentNewJobBinding
import ru.kot1.demo.databinding.FragmentNewPostBinding
import ru.kot1.demo.dto.Coords
import ru.kot1.demo.enumeration.AttachmentType
import ru.kot1.demo.util.AndroidUtils
import ru.kot1.demo.viewmodel.EditEventViewModel
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


@AndroidEntryPoint
class NewEventFragment : Fragment() {
    private val viewModel: EditEventViewModel by activityViewModels()

    private val photoRequestCode = 1
    private val cameraRequestCode = 2
    private val musicRequestCode = 3
    private val videoRequestCode = 4


    private var fragmentBinding: FragmentNewPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.new_event)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_record, menu)
        menu.findItem(R.id.signout).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                fragmentBinding?.let {
                    if (it.edit.text.toString().trim().isBlank()) {
                        Toast.makeText(
                            requireContext(),
                            R.string.enter_anything,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        return false
                    }

                    viewModel.prepareEventText(it.edit.text.toString())
                    viewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                    activity?.supportFragmentManager?.popBackStack()


                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        arguments?.getLong("event")?.let { id ->
                if (id != 0L) viewModel.loadEvent(id) else {
                    viewModel.prepareForNew()
                }
            }

        viewModel.eventText.observe(viewLifecycleOwner) { text ->
            binding.edit.setText(text)
        }

        viewModel.eventUI.observe(viewLifecycleOwner) { uiObject ->
        }

        viewModel.attach.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.previewPanel.visibility = View.GONE
            } else {
                binding.previewPanel.visibility = View.VISIBLE
                when (it.dataType) {
                    AttachmentType.IMAGE -> {
                        if (it.uri!= null) {
                        binding.previewPhoto.setImageURI(it.uri)
                        } else {
                            binding.previewPhoto.setImageResource(R.drawable.ic_image)
                        }
                    }
                    AttachmentType.VIDEO -> binding.previewPhoto.setImageResource(R.drawable.ic_video)
                    AttachmentType.AUDIO -> binding.previewPhoto.setImageResource(R.drawable.ic_music)
                }
            }
        }


        binding.edit.requestFocus()

        binding.mp3File.setOnClickListener {
            MaterialFilePicker()
                .withSupportFragment(this@NewEventFragment)
                .withCloseMenu(true)
                .withPath(Environment.getExternalStorageDirectory().absolutePath)
                .withRootPath(Environment.getExternalStorageDirectory().absolutePath)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.(mp3)$"))
                .withFilterDirectories(false)
                .withTitle("Select mp3 file")
                .withRequestCode(musicRequestCode)
                .start()
        }

        binding.mp4File.setOnClickListener {
            MaterialFilePicker()
                .withSupportFragment(this@NewEventFragment)
                .withCloseMenu(true)
                .withPath(Environment.getExternalStorageDirectory().absolutePath)
                .withRootPath(Environment.getExternalStorageDirectory().absolutePath)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.(mp4)$"))
                .withFilterDirectories(false)
                .withTitle("Select mp4 file")
                .withRequestCode(videoRequestCode)
                .start()
        }


        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .start(photoRequestCode)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .cameraOnly()
                .start(cameraRequestCode)
        }

        binding.removeAttach.setOnClickListener {
            viewModel.prepareTargetAttach(null, null)
            binding.previewPanel.isVisible = false
        }



        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == ImagePicker.RESULT_ERROR) {
            fragmentBinding?.let {
                Snackbar.make(it.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG).show()
            }
            return
        }
        if (resultCode == Activity.RESULT_OK &&
            (requestCode == photoRequestCode || requestCode == cameraRequestCode)
        ) {
            data?.let { viewModel.prepareTargetAttach(it.data, AttachmentType.IMAGE) }
            return
        }


        if (resultCode == Activity.RESULT_OK && requestCode == musicRequestCode) {
            data?.let {
                viewModel.prepareTargetAttach(
                    File(it.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)).toUri(),
                    AttachmentType.AUDIO
                )
            }
            return
        }

        if (resultCode == Activity.RESULT_OK && requestCode == videoRequestCode) {
            data?.let {
                viewModel.prepareTargetAttach(
                    File(it.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)).toUri(),
                    AttachmentType.VIDEO
                )
            }
            return
        }

    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}


interface EventDateBack {
    fun getDate(value : String)
}

class DatePickerFragment(
                         var backValue : EventDateBack) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val dateFormat: DateFormat = SimpleDateFormat("MM/dd/yyyy")
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = day
        calendar[Calendar.MONTH] = month
        calendar[Calendar.YEAR] = year

        val tempDate =  dateFormat.format(calendar.time)
            backValue.getDate(tempDate)
        }

    }
