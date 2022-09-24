package test.interview.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import test.interview.R
import test.interview.databinding.ActivityMainBinding
import test.interview.repository.database.Form

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewModel: FormViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private val bookList = arrayOf("Book A", "Book B", "Book C")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        val type = intent.getSerializableExtra("type")  as FormViewModel.FormTypes

        val form = try {
            Gson().fromJson(intent.getStringExtra("form_data"), Form::class.java)
        } catch (e: Exception) {
            null
        }
        viewModelFactory = if (form != null) {
            setView(form)
            ViewModelFactory(this, FormViewModel.FormTypes.EXISTING_FORM)
        } else {
            ViewModelFactory(this, FormViewModel.FormTypes.NEW_FORM)
        }

        viewModel = ViewModelProvider(this, viewModelFactory)[FormViewModel::class.java]

        if (form != null) {
            viewModel.setName(form.name)
            viewModel.setNumber(form.number.toString())
            viewModel.setBook(form.book)
            viewModel.setFormData(form)
        }

        viewModel.changeScreen.observe(this) {
            val intent = Intent(this, FormViewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        activityMainBinding.submitFieldButton.setOnClickListener {
            viewModel.isFormValid()
        }

        activityMainBinding.nameFieldEdit.doOnTextChanged { text, _, _, _ ->
            if (text!!.toString().isNotEmpty())
                viewModel.setName(text.toString())
        }
        activityMainBinding.numberFieldEdit.doOnTextChanged { text, _, _, _ ->
            if (text!!.toString().isNotEmpty())
                viewModel.setNumber(
                    text.toString()
                )
        }

        activityMainBinding.booksFieldSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, bookList)

        viewModel.formErrors.addOnListChangedCallback(object :
            OnListChangedCallback<ObservableArrayList<FormViewModel.FormErrors>>() {
            override fun onChanged(sender: ObservableArrayList<FormViewModel.FormErrors>?) {
            }

            override fun onItemRangeChanged(
                sender: ObservableArrayList<FormViewModel.FormErrors>?,
                positionStart: Int,
                itemCount: Int
            ) {
            }

            override fun onItemRangeInserted(
                sender: ObservableArrayList<FormViewModel.FormErrors>?,
                positionStart: Int,
                itemCount: Int
            ) {
                showError(sender)
            }

            override fun onItemRangeMoved(
                sender: ObservableArrayList<FormViewModel.FormErrors>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
            }

            override fun onItemRangeRemoved(
                sender: ObservableArrayList<FormViewModel.FormErrors>?,
                positionStart: Int,
                itemCount: Int
            ) {
                showError(sender)
            }
        })

        activityMainBinding.booksFieldSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.i(TAG, "onItemSelected: ${p0!!.selectedItem}")
                viewModel.setBook(p0.selectedItem.toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

    }

    private fun showError(sender: ObservableArrayList<FormViewModel.FormErrors>?) {
        activityMainBinding.nameFieldLayout.helperText = ""
        activityMainBinding.numberFieldLayout.helperText = ""
        activityMainBinding.spinnerError.visibility = View.GONE

        sender!!.forEach {
            when (it) {
                FormViewModel.FormErrors.MISSING_NAME -> {
                    activityMainBinding.nameFieldLayout.helperText =
                        getString(R.string.name_field_error)
                }
                FormViewModel.FormErrors.MISSING_NUMBER -> {
                    activityMainBinding.numberFieldLayout.helperText =
                        getString(R.string.number_field_error)
                }
                FormViewModel.FormErrors.MISSING_BOOK -> {
                    activityMainBinding.spinnerError.visibility = View.VISIBLE
                    activityMainBinding.spinnerError.text = getString(R.string.book_field_error)
                }
                FormViewModel.FormErrors.INVALID_NAME -> {
                    activityMainBinding.nameFieldLayout.helperText =
                        getString(R.string.invalid_name_field_error)
                }
                FormViewModel.FormErrors.INVALID_NUMBER -> {
                    activityMainBinding.numberFieldLayout.helperText =
                        getString(R.string.invalid_number_field_error)
                }
                else -> {
                    Log.i(TAG, "showError: else")
                }
            }
        }
    }

    private fun setView(form: Form) {
        Log.i(TAG, "setView: book list: $bookList")
        activityMainBinding.nameFieldEdit.setText(form.name)
        activityMainBinding.numberFieldEdit.setText(form.number.toString())

        activityMainBinding.booksFieldSpinner.post {
            activityMainBinding.booksFieldSpinner.setSelection(bookList.indexOf(form.book))
        }

    }


}