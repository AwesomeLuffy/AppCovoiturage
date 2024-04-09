package com.yrenard.applicationcovoiturage.model.data

//noinspection SuspiciousImport
import android.R
import com.yrenard.applicationcovoiturage.R as RR
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.Fragment

/**
 * Object class just to set all extensions of the Fragment class for multiple use and not copy paste everything
 */
object ExtendFragment {
    //Input value is the adition of all type
    //Example TEXT = 1 / VARIATION_PASSWORD = 128 / VARIATION_PERSON_NAME = 96 so 225 (actual type for password)
    //Here it's TEXT = 1 / NUMBER = 2 / FLAG_SIGNED = 4096 / VARIATION_PERSON_NAME = 96
    private const val INPUT_TYPE_NUMBER_SIGNED =
                InputType.TYPE_CLASS_TEXT +
                InputType.TYPE_CLASS_NUMBER +
                InputType.TYPE_NUMBER_FLAG_SIGNED +
                InputType.TYPE_TEXT_VARIATION_PERSON_NAME

    /**
     * Function that return an spinner adapter from a list of object
     * @param T the type of the object array
     * @property listToAdapt ArrayList<T> the list that have to be in the spinner (have to be the same type has the @param
     * @return ArrayAdapter<T> return an adapter
     */
    fun <T> Fragment.getSpinner(listToAdapt: java.util.ArrayList<T>): ArrayAdapter<T> {
        val spinnerAdapater = ArrayAdapter<T>(this.requireContext(),
            R.layout.simple_spinner_item,
            listToAdapt
        )

        spinnerAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return spinnerAdapater
    }

    /**
     * Function that auto bind a value from shared preferences file
     * @property textView: EditText the edit edit text where the value will be bind
     * @property field: String the name field in the shared preferences file
     */
    fun Fragment.bindDefaultValueFromSPFile(textView: EditText, field: String){
        val valueOfField: String = FichierManager.lireConfig(
            this.requireContext(),
            field,
            SharedPreferenceType.NORMAL
        ) ?: ""
        if(valueOfField != ""){
            textView.setText(valueOfField)
        }
    }

    /**
     * Function that return True if there an error
     * @property listField Array<EditText> the array list of EditText the function have to check
     * @return Boolean
     */
    fun Fragment.verifyFieldForTravelButton(listField: Array<EditText>) : Boolean{
        var isError : Boolean = false

        for(field : EditText in listField){
            if(field.text.isEmpty()){
                field.error = this.getString(com.yrenard.applicationcovoiturage.R.string.errEmptyField)
                isError = true
            }
            else{
                if(field.inputType == INPUT_TYPE_NUMBER_SIGNED){
                    try{
                        if(Integer.parseInt(field.text.toString()) < 1) {
                            field.error = this.getString(com.yrenard.applicationcovoiturage.R.string.errNumberRange)
                            isError = true
                            continue
                        }
                    }
                    catch (e: NumberFormatException){
                        try {
                            if(field.text.toString().toFloat() < 0.0F ){
                                field.error = this.getString(RR.string.errNumberRangeGTE)
                                isError = true
                            }
                        }
                        catch (e: NumberFormatException){
                            field.error = this.getString(RR.string.errNaN)
                            isError = true
                        }

                    }
                }
            }
        }
        return isError
    }
}