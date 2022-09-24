package test.interview.ui

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import test.interview.repository.FormRepository
import test.interview.repository.database.Form
import test.interview.repository.database.FormDatabase
import test.interview.utils.checkName
import test.interview.utils.checkNumber

private const val TAG = "FormViewModel"

class FormViewModel(context: Context, private val type: FormTypes) : ViewModel() {

    enum class FormErrors {
        MISSING_NAME,
        MISSING_NUMBER,
        MISSING_BOOK,
        INVALID_NAME,
        INVALID_NUMBER,
    }

    enum class FormTypes {
        NEW_FORM,
        EXISTING_FORM
    }

    private lateinit var formData: Form

    val dao = FormDatabase.getDatabase(context).getDao()
    private val respository = FormRepository(dao)

    val formErrors = ObservableArrayList<FormErrors>()

    private val _name = MutableLiveData<String>()
    val name: LiveData<String>
        get() = _name

    private val _number = MutableLiveData<String>()
    val number: LiveData<String>
        get() = _number

    private val _bookSelected = MutableLiveData<String>()
    val bookSelected: MutableLiveData<String>
        get() = _bookSelected

    private val _changeScreen = MutableLiveData<Boolean>()
    val changeScreen: LiveData<Boolean> = _changeScreen

    fun setName(value: String) {
        _name.value = value
        formErrors.remove(FormErrors.MISSING_NAME)
        formErrors.remove(FormErrors.INVALID_NAME)
        Log.i(TAG, "setName: $value")
    }

    fun setNumber(value: String) {
        _number.value = value
        formErrors.remove(FormErrors.MISSING_NUMBER)
        formErrors.remove(FormErrors.INVALID_NUMBER)
    }

    fun setBook(value: String) {
        _bookSelected.value = value
        formErrors.remove(FormErrors.MISSING_BOOK)
    }

    fun setFormData(value: Form) {
        formData = value
    }

    fun isFormValid() {
        formErrors.clear()
        val tempErrors = ArrayList<FormErrors>()
        if (name.value == null) {
            tempErrors.add(FormErrors.MISSING_NAME)
        } else if (!name.value.toString().checkName()) {
            tempErrors.add(FormErrors.INVALID_NAME)
        }

        if (number.value == null) {
            tempErrors.add(FormErrors.MISSING_NUMBER)
        } else if (number.value.toString().length != 10 || !number.value.toString().checkNumber()) {
            tempErrors.add(FormErrors.INVALID_NUMBER)
        }

        if (bookSelected.value == null) {
            tempErrors.add(FormErrors.MISSING_BOOK)
        }

        if (tempErrors.isNotEmpty())
            formErrors.addAll(tempErrors)
        else {
            if (type == FormTypes.NEW_FORM) {
                insert(
                    Form(
                        name = name.value.toString(),
                        number = number.value!!.toLong(),
                        book = bookSelected.value.toString()
                    )
                )
            } else if (type == FormTypes.EXISTING_FORM) {
                formData.name = name.value.toString()
                formData.number = number.value!!.toLong()
                formData.book = bookSelected.value!!
                update(
                    formData
                )
            }
            _changeScreen.value = true
        }

    }

    fun getForm() = respository.formData

    private fun insert(form: Form) {
        CoroutineScope(Main).launch {
            withContext(CoroutineScope(IO).coroutineContext) {
                respository.insert(form)
            }
            _changeScreen.value = true
        }

    }

    fun update(form: Form) {
        CoroutineScope(IO).launch {
            respository.update(form)
        }
    }

    fun delete(form: Form) {
        CoroutineScope(Main).launch {
            withContext(CoroutineScope(IO).coroutineContext) {
                respository.delete(form)
            }
            _changeScreen.value = true
        }
    }

}