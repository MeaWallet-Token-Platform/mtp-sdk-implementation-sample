package com.meawallet.mtp.sampleapp.ui.addcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meawallet.mtp.MeaInitializeDigitizationParameters
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.utils.RandomPanBuilder
import java.util.Calendar

class InitWithPanFragment : DigitizationParamProviderFragment() {

    companion object {

        val instance: InitWithPanFragment by lazy { InitWithPanFragment() }
    }

    private lateinit var cardPanInput: TextInputEditText
    private lateinit var cardPanInputContainer: TextInputLayout
    private lateinit var cardPanGetBtn: MaterialButton
    private lateinit var cardHolderNameInput: TextInputEditText
    private lateinit var cardHolderNameInputContainer: TextInputLayout
    private lateinit var cardHolderNameGetBtn: MaterialButton
    private lateinit var cardMonthInput: NumberPicker
    private lateinit var cardYearInput: NumberPicker
    private lateinit var initializeViewSet: MutableSet<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentRoot = inflater.inflate(R.layout.fragment_init_with_pan, container, false)
        inflateAndSetupViews(fragmentRoot)

        return fragmentRoot
    }

    private fun inflateAndSetupViews(fragmentRoot: View) {
        cardPanInput = fragmentRoot.findViewById(R.id.card_pan)
        cardPanInputContainer = fragmentRoot.findViewById(R.id.card_pan_container)
        cardPanGetBtn = fragmentRoot.findViewById(R.id.card_pan_get)
        cardHolderNameInput = fragmentRoot.findViewById(R.id.card_holder_name)
        cardHolderNameInputContainer = fragmentRoot.findViewById(R.id.card_holder_name_container)
        cardHolderNameGetBtn = fragmentRoot.findViewById(R.id.card_holder_name_get)
        cardMonthInput = fragmentRoot.findViewById(R.id.card_month)
        cardYearInput = fragmentRoot.findViewById(R.id.card_year)
        initializeViewSet =
            mutableSetOf(cardPanInput, cardPanInputContainer, cardPanGetBtn, cardHolderNameInput,
                cardHolderNameInputContainer, cardHolderNameGetBtn, cardMonthInput, cardYearInput)

        cardMonthInput.minValue = 1
        cardMonthInput.maxValue = 12
        cardMonthInput.value = getCurrentMonth()

        cardYearInput.minValue = getCurrentYear()
        cardYearInput.maxValue = getCurrentYear() + 5
        cardYearInput.value = getCurrentYear() + 2

        cardPanGetBtn.setOnClickListener {
            cardPanInput.setText(RandomPanBuilder.getRandomPan())
        }

        cardHolderNameGetBtn.setOnClickListener {
            cardHolderNameInput.setText("Mingo Bingo")
        }
    }

    private fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR) % 100 // get last 2 digits of a year
    }

    private fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1  // month count starts at 0
    }

    override fun isInputValid(): Boolean {
        return true
    }

    override fun getDigitizatonParams(): MeaInitializeDigitizationParameters {
        val pan = cardPanInput.text.toString()
        val month = cardMonthInput.value
        val year = cardYearInput.value
        val cardHolderName = cardHolderNameInput.text.toString()

        return MeaInitializeDigitizationParameters.withPan(pan, month, year, cardHolderName)
    }

    override fun getViewSet(): Set<View> {
        return initializeViewSet
    }
}