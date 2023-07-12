package com.meawallet.mtp.sampleapp.ui.addcard

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.R
import com.meawallet.mtp.sampleapp.helpers.AlertDialogHelper
import com.meawallet.mtp.sampleapp.listeners.DismissAlertDialogListener
import com.meawallet.mtp.sampleapp.utils.downloadBackgroundImage
import com.meawallet.mtp.sampleapp.utils.hasBackgroundImage

class AddCardActivity : AppCompatActivity() {

    companion object {
        private val TAG = AddCardActivity::class.java.simpleName

        const val CARD_IN_ACTION_RECEIPT: String = "card_in_action_receipt"

        private val FIRST_DIGITIZATION_METHOD = DigitizationMethodEnum.PAN
    }

    private lateinit var addCardViewModel: AddCardViewModel

    private var cardDigitizationActivityState: CardDigitizationActivityState? = null
    private var cardInAction: MeaCard? = null

    private lateinit var digiMethodDropdown: AutoCompleteTextView
    private lateinit var digiMethodContainer: TextInputLayout

    private lateinit var initDigitizationBtn: MaterialButton
    private lateinit var initializeViewSet: MutableSet<View>

    private lateinit var cvc2InputContainer: TextInputLayout
    private lateinit var cvc2Input: TextInputEditText
    private lateinit var completeDigitizationBtn: MaterialButton
    private lateinit var cardEligibilityReceiptTv: TextView
    private lateinit var cardTermsConditionsCheck: MaterialCheckBox
    private lateinit var cardTermsConditionsTitle: TextView
    private lateinit var completeViewSet: MutableSet<View>

    private lateinit var additionalAuthDecisionTv: TextView
    private lateinit var additionalAuthDecisionTitleTv: TextView
    private lateinit var authMethodDropdown: AutoCompleteTextView
    private lateinit var authMethodContainer: TextInputLayout
    private lateinit var initAuthenticationBtn: MaterialButton
    private lateinit var initAuthViewSet: MutableSet<View>

    private lateinit var authCodeInputContainer: TextInputLayout
    private lateinit var authCodeInput: TextInputEditText
    private lateinit var completeAuthenticationBtn: MaterialButton
    private lateinit var completeAuthViewSet: MutableSet<View>

    private lateinit var circularProgressIndicator: CircularProgressIndicator

    private lateinit var currentDigitizationParamsFragment: DigitizationParamProviderFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        if (!MeaTokenPlatform.isInitialized()) {
            MeaTokenPlatform.initialize(this)
        }

        addCardViewModel =
            ViewModelProvider(this)[AddCardViewModel::class.java]

        inflateAndSetupViews()

        handleIntent(intent)

        restoreDigitizationState()

        loadFirstDigitizationMethodFragment()

        addCardViewModel.getDigitizationState().observe(this) {
            cardDigitizationActivityState = it
            renderUi(it)
        }

        setupUiEventListeners()
    }

    private fun inflateAndSetupViews() {
        digiMethodDropdown = findViewById(R.id.card_digitization_method)
        digiMethodContainer = findViewById(R.id.card_digitization_method_container)
        setupDigitizationMethodDropdown()

        initDigitizationBtn = findViewById(R.id.card_initialize_digitization)
        initializeViewSet =
            mutableSetOf(digiMethodDropdown, digiMethodContainer, initDigitizationBtn)

        cvc2InputContainer = findViewById(R.id.card_cvc2_code_container)
        cvc2Input = findViewById(R.id.card_cvc2_code)
        completeDigitizationBtn = findViewById(R.id.card_complete_digitization)
        cardEligibilityReceiptTv = findViewById(R.id.card_eligibility_receipt)
        cardTermsConditionsCheck = findViewById(R.id.card_get_tc_checkbox)
        cardTermsConditionsTitle = findViewById(R.id.card_get_tc_title)
        completeViewSet = mutableSetOf(cvc2InputContainer, cvc2Input,
            completeDigitizationBtn, cardTermsConditionsCheck, cardTermsConditionsTitle)

        additionalAuthDecisionTv = findViewById(R.id.card_additional_auth)
        additionalAuthDecisionTitleTv = findViewById(R.id.card_additional_auth_title)
        authMethodDropdown = findViewById(R.id.card_authentication_method)
        authMethodContainer = findViewById(R.id.card_authentication_method_container)
        initAuthenticationBtn = findViewById(R.id.card_initialize_authentication)
        initAuthViewSet = mutableSetOf(additionalAuthDecisionTitleTv,additionalAuthDecisionTv,
            authMethodDropdown, authMethodContainer, initAuthenticationBtn)

        authCodeInputContainer = findViewById(R.id.card_authentication_code_container)
        authCodeInput = findViewById(R.id.card_authentication_code)
        completeAuthenticationBtn = findViewById(R.id.card_complete_authentication)
        completeAuthViewSet = mutableSetOf(authCodeInputContainer, authCodeInput, completeAuthenticationBtn)

        circularProgressIndicator = findViewById(R.id.card_progress_indicator)
    }

    private fun setupUiEventListeners() {
        initDigitizationBtn.setOnClickListener {

            if (currentDigitizationParamsFragment.isInputValid()) {
                val params = currentDigitizationParamsFragment.getDigitizatonParams()
                addCardViewModel.initDigitization(
                    params,
                    object : MeaInitializeDigitizationListener {
                        override fun onFailure(error: MeaError) {
                            addCardViewModel.notifyFailedAction()

                            val message = "Epic failure with initialize digitization: ${error.name}:${error.message}"

                            AlertDialogHelper.showErrorMessageDialog(this@AddCardActivity, message)
                        }

                        override fun onSuccess(
                            p0: MeaEligibilityReceipt,
                            p1: String?,
                            p2: Boolean
                        ) {
                            cardInAction = addCardViewModel.getCardByEligibilityReceipt(p0.value)

                            addCardViewModel.setDigitizationState(CardDigitizationActivityState.Initialized)
                        }
                    })
            }
        }

        completeDigitizationBtn.setOnClickListener {
            cardInAction?.let {
                addCardViewModel.completeDigitization(it, cvc2Input.text.toString(), object : MeaCompleteDigitizationListener {
                    override fun onFailure(error: MeaError) {
                        addCardViewModel.notifyFailedAction()

                        val message =
                            when(error.code) {
                                MeaErrorCode.CARD_DIGITIZATION_DECLINED -> {
                                    "Digitization was declined by the issuer of the card"
                                }
                                MeaErrorCode.INCORRECT_INPUT_DATA -> {
                                    "Incorrect input data. Double-check what you have entered."
                                }
                                else -> {
                                    "Epic fail: ${error.name}:${error.message}"
                                }
                            }

                        AlertDialogHelper.showErrorMessageDialog(this@AddCardActivity, message)
                    }

                    override fun onSuccess(card: MeaCard) {
                        cardInAction = card
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.Finished)

                        downloadCardImage(card)

                        AlertDialogHelper.showSuccessMessageDialog(this@AddCardActivity, "Digitization completed", object : DismissAlertDialogListener {
                            override fun onDialogDismiss() {
                                finish()
                            }

                            override fun onOkButtonClick() {
                                finish()
                            }

                        })
                    }

                    override fun onRequireAdditionalAuthentication(card: MeaCard) {
                        cardInAction = card
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.AuthRequired)
                    }
                })

            }
        }

        cardTermsConditionsCheck.setOnCheckedChangeListener { _, tcAccepted ->
            addCardViewModel.setTermsAndConditionsAccepted(tcAccepted) }

        cardTermsConditionsTitle.setOnClickListener {
            displayTermsAndConditions()
        }

        initAuthenticationBtn.setOnClickListener {
            val selectedAuthMethod = authMethodDropdown.text.toString()
            val authMethodId = getAuthMethodIdFromSelection(selectedAuthMethod)

            authMethodId?.let { methodId ->
                cardInAction?.let {
                    addCardViewModel.initializeAdditionalAuthentication(
                        it.id,
                        methodId, object :
                            MeaListener {
                            override fun onFailure(error: MeaError) {
                                addCardViewModel.notifyFailedAction()

                                val message = "Epic fail: ${error.name}:${error.message}"

                                AlertDialogHelper.showErrorMessageDialog(this@AddCardActivity, message)
                            }

                            override fun onSuccess() {
                                addCardViewModel
                                    .setDigitizationState(CardDigitizationActivityState.AuthInitialized)

                                AlertDialogHelper.showSuccessMessageDialog(this@AddCardActivity, "Authentication initialized. Enter authentication code.")
                            }
                        })
                }
            }
        }

        completeAuthenticationBtn.setOnClickListener {
            cardInAction?.let {
                addCardViewModel.completeAdditionalAuthentication(it.id, authCodeInput.text.toString(), object : MeaCompleteAuthenticationListener {
                    override fun onFailure(error: MeaError) {
                        addCardViewModel.notifyFailedAction()

                        val message = "Epic fail: ${error.name}:${error.message}"

                        AlertDialogHelper.showErrorMessageDialog(this@AddCardActivity, message)
                    }

                    override fun onSuccess(card: MeaCard) {
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.Finished)

                        AlertDialogHelper.showSuccessMessageDialog(this@AddCardActivity, "Digitization completed", object : DismissAlertDialogListener {
                            override fun onDialogDismiss() {
                                finish()
                            }

                            override fun onOkButtonClick() {
                                finish()
                            }

                        })
                    }
                })
            }
        }
    }
    
    private fun setupDigitizationMethodDropdown() {
        val digitizationMethods = arrayListOf(
            DigitizationMethodEnum.PAN.value,
            DigitizationMethodEnum.CARD_ID.value) // "Encrypted PAN", "Receipt", "E2E encrypted data")
        val dmAdapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.dropdown_menu_item, digitizationMethods)

        digiMethodDropdown.setAdapter(dmAdapter)
        digiMethodDropdown.setText(digitizationMethods[0], false)

        digiMethodDropdown.setOnItemClickListener { _, _, i, _ ->
            loadDigitizationParamsFragment(
                DigitizationMethodEnum.fromValue(digitizationMethods[i]))
        }
    }

    private fun loadFirstDigitizationMethodFragment() {
        loadDigitizationParamsFragment(FIRST_DIGITIZATION_METHOD)
    }

    private fun loadDigitizationParamsFragment(digitizationMethod: DigitizationMethodEnum) {
        val fragmentToLoad = ParamProviderFragmentFactory.createFragment(digitizationMethod)
        currentDigitizationParamsFragment = fragmentToLoad

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.card_digitization_params_container,
                fragmentToLoad)
        }
    }

    private fun setupAuthMethodDropdown() {
        cardInAction?.let {
            val authMethods = mutableListOf<String>()
            it.digitizationAuthenticationMethods?.forEach { method -> authMethods.add(method.type.name) }
            val dmAdapter: ArrayAdapter<String> = ArrayAdapter(this, R.layout.dropdown_menu_item, authMethods)
            authMethodDropdown.setAdapter(dmAdapter)

            if (authMethods.size > 0) {
                authMethodDropdown.setText(authMethods[0], false)
            }
        }
    }

    private fun getAuthMethodIdFromSelection(selection: String): String? {
        val authMethods = mutableListOf<MeaAuthenticationMethod>()

        cardInAction?.let {
            it.digitizationAuthenticationMethods?.forEach { method -> authMethods.add(method) }
        }

        if (authMethods.size == 0) {
            return null
        }

        return authMethods.firstOrNull { it.type.name == selection }?.id
    }

    private fun handleIntent(intent: Intent) {
        val eligibilityReceipt = intent.getStringExtra(CARD_IN_ACTION_RECEIPT)
        eligibilityReceipt?.let {
            cardInAction = addCardViewModel.getCardByEligibilityReceipt(it)
        }
    }

    private fun restoreDigitizationState() {
        if (cardInAction == null) {
            addCardViewModel.setDigitizationState(CardDigitizationActivityState.New)
            return
        }
        when (cardInAction?.state) {
            MeaCardState.DIGITIZATION_STARTED -> {
                 addCardViewModel.setDigitizationState(CardDigitizationActivityState.Initialized)
            }
            else -> {
                when (cardInAction?.yellowPathState) {
                    MeaCardYellowPathState.REQUIRE_ADDITIONAL_AUTHENTICATION -> {
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.AuthRequired)
                    }
                    MeaCardYellowPathState.AUTHENTICATION_INITIALIZED -> {
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.AuthInitialized)
                    }
                    else -> {
                        addCardViewModel.setDigitizationState(CardDigitizationActivityState.Finished)
                    }
                }
            }
        }
    }

    private fun displayTermsAndConditions() {
        cardInAction?.termsAndConditionsAssetId?.let {
            addCardViewModel.setDigitizationState(CardDigitizationActivityState.InProgress)

            addCardViewModel.getTermsAndConditions(it, object : MeaGetAssetListener {
                override fun onFailure(error: MeaError) {
                    addCardViewModel.notifyFailedAction()

                    val message = "Epic failure with showing Terms & Conditions: ${error.name}:${error.message}"
                    AlertDialogHelper.showErrorMessageDialog(this@AddCardActivity, message)
                }

                override fun onSuccess(mediaContentArray: Array<out MeaMediaContent>) {
                    addCardViewModel.restoreFromLoadingState()

                    var tcText = ""
                    for (media in mediaContentArray) {
                        Log.d(TAG,"Received media. Type = ${media.type}")

                        if (media.type == MeaMediaContent.Type.PLAIN_TEXT) {
                            tcText = String(Base64.decode(media.base64DataString, Base64.DEFAULT))
                            break
                        }
                    }
                    if (tcText.isNotEmpty()) {
                        AlertDialogHelper.showTermsAndConditionsDialog(this@AddCardActivity, tcText, null)
                    } else {
                        tcText = "Terms and Conditions are not published, but we kindly ask you to agree anyways"
                        AlertDialogHelper.showSuccessMessageDialog(this@AddCardActivity, tcText, null)
                    }
                }
            })
        }
    }

    private fun renderUi(state: CardDigitizationActivityState) {
        when (state) {
            is CardDigitizationActivityState.New -> {
                renderInitializeFields()
            }
            is CardDigitizationActivityState.Initialized -> {
                renderCompleteFields()
            }
            is CardDigitizationActivityState.Finished -> {
                renderFinishedFields()
            }
            is CardDigitizationActivityState.InProgress -> {
                renderProgressIndicator()
            }
            is CardDigitizationActivityState.AuthRequired -> {
                renderAuthRequiredFields()
            }
            is CardDigitizationActivityState.AuthInitialized -> {
                renderAuthInitializedFields()
            }
        }
    }

    private fun renderInitializeFields() {
        setViewsEnabled(true, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(true)
        setViewsEnabled(false, completeViewSet)
        setViewsEnabled(false, initAuthViewSet)
        setViewsEnabled(false, completeAuthViewSet)

        setViewsVisible(false, initAuthViewSet)
        setViewsVisible(false, completeAuthViewSet)

        circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun renderCompleteFields() {
        setViewsEnabled(false, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(false)
        setViewsEnabled(true, completeViewSet)
        setViewsEnabled(false, initAuthViewSet)
        setViewsEnabled(false, completeAuthViewSet)

        setViewsVisible(false, initAuthViewSet)
        setViewsVisible(false, completeAuthViewSet)

        completeDigitizationBtn.isEnabled = addCardViewModel.termsAnConditionsAccepted
        cvc2InputContainer.isEnabled = addCardViewModel.termsAnConditionsAccepted

        cardEligibilityReceiptTv.text = cardInAction?.eligibilityReceipt
        circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun renderFinishedFields() {
        setViewsEnabled(false, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(false)
        setViewsEnabled(false, completeViewSet)
        setViewsEnabled(false, initAuthViewSet)
        setViewsEnabled(false, completeAuthViewSet)

        circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun renderAuthRequiredFields() {
        setViewsEnabled(false, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(false)
        setViewsEnabled(false, completeViewSet)
        setViewsEnabled(true, initAuthViewSet)
        setViewsEnabled(false, completeAuthViewSet)

        setViewsVisible(true, initAuthViewSet)
        setViewsVisible(true, completeAuthViewSet)

        setupAuthMethodDropdown()

        additionalAuthDecisionTv.text = "REQUIRED"
        circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun renderAuthInitializedFields() {
        setViewsEnabled(false, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(false)
        setViewsEnabled(false, completeViewSet)
        setViewsEnabled(false, initAuthViewSet)
        setViewsEnabled(true, completeAuthViewSet)

        setViewsVisible(true, initAuthViewSet)
        setViewsVisible(true, completeAuthViewSet)

        circularProgressIndicator.visibility = View.INVISIBLE
    }

    private fun renderProgressIndicator() {
        setViewsEnabled(false, initializeViewSet)
        currentDigitizationParamsFragment.setFieldsEnabled(false)
        setViewsEnabled(false, completeViewSet)
        setViewsEnabled(false, initAuthViewSet)
        setViewsEnabled(false, completeAuthViewSet)

        circularProgressIndicator.visibility = View.VISIBLE
    }

    private fun setViewsEnabled(enabled: Boolean, viewSet: Set<View>) {
        for (item in viewSet) {
            item.isEnabled = enabled
        }
    }

    private fun setViewsVisible(visible: Boolean, viewSet: Set<View>) {
        for (item in viewSet) {
            item.visibility =
                if (visible) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
        }
    }

    private fun downloadCardImage(card: MeaCard) {
        if (card.hasBackgroundImage()) {
            card.downloadBackgroundImage(this)
        } else {
            Log.d(TAG,"This card (cardId = ${card.id}) doesn't have background image")
        }
    }

}