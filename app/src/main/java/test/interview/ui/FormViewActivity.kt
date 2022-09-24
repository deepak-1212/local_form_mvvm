package test.interview.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import test.interview.R
import test.interview.databinding.ActivityFormViewBinding
import test.interview.repository.database.Form

private const val TAG = "FormViewActivity"
class FormViewActivity : AppCompatActivity() {

    private lateinit var formViewBinding: ActivityFormViewBinding
    private lateinit var viewModel: FormViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var form: Form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        formViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_form_view)

        viewModelFactory = ViewModelFactory(this, FormViewModel.FormTypes.EXISTING_FORM)
        viewModel = ViewModelProvider(this, viewModelFactory)[FormViewModel::class.java]

        viewModel.getForm().observe(this) {
            form = it!!
            setFormView(it)
        }
        viewModel.changeScreen.observe(this) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()

        formViewBinding.updateFieldButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("form_data", Gson().toJson(form))
            startActivity(intent)
        }

        formViewBinding.deleteFieldButton.setOnClickListener {
            viewModel.delete(form)
        }

    }

    private fun setFormView(form: Form) {
        formViewBinding.nameView.text = String.format("Name: %s", form.name)
        formViewBinding.numberView.text = String.format("Number: %s", form.number)
        formViewBinding.bookView.text = String.format("Book selected: %s", form.book)
    }
}