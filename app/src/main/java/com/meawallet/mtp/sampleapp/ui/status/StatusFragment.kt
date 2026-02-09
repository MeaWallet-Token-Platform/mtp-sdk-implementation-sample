package com.meawallet.mtp.sampleapp.ui.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.meawallet.mtp.sampleapp.databinding.FragmentStatusBinding
import com.meawallet.mtp.sampleapp.di.appContainer
import com.meawallet.mtp.sampleapp.ui.viewModelFactory

class StatusFragment : Fragment() {

    private var _binding: FragmentStatusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val appContainer by lazy { requireContext().appContainer }
    private val statusViewModel by viewModels<StatusViewModel> {
            viewModelFactory(StatusViewModel::class.java) {
                StatusViewModel(appContainer.tokenPlatform)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val sdkVersionTv: TextView = binding.statusSdkVersion
        statusViewModel.sdkVersion.observe(viewLifecycleOwner) {
            sdkVersionTv.text = it
        }

        val isInitializedTv: TextView = binding.statusIsInitialized
        statusViewModel.isInitialized.observe(viewLifecycleOwner) {
            isInitializedTv.text = it.toString()
        }

        val isRegisteredTv: TextView = binding.statusIsRegistered
        statusViewModel.isRegistered.observe(viewLifecycleOwner) {
            isRegisteredTv.text = it.toString()
        }

        val msgTokenTv: TextView = binding.statusMsgToken
        statusViewModel.getMsgToken(requireContext()).observe(viewLifecycleOwner) {
            msgTokenTv.text = it
        }

        val isSecureNfcSupportedTv: TextView = binding.statusSecureNcfSupported
        statusViewModel.isSecureNfcSupported.observe(viewLifecycleOwner) {
            isSecureNfcSupportedTv.text = it.toString()
        }

        val isSecureNfcEnabledTv: TextView = binding.statusSecureNcfEnabled
        statusViewModel.isSecureNfcEnabled.observe(viewLifecycleOwner) {
            isSecureNfcEnabledTv.text = it.toString()
        }

        val isDefaultPaymentAppTv: TextView = binding.statusDefaultPaymentApp
        statusViewModel.isDefaultPaymentApp(requireContext()).observe(viewLifecycleOwner) {
            isDefaultPaymentAppTv.text = it.toString()
        }

        val setDefaultPaymentAppBtn: MaterialButton = binding.statusDefaultPaymentAppBtn
        statusViewModel.isDefaultPaymentApp(requireContext()).observe(viewLifecycleOwner) {
            if (!it) {
                setDefaultPaymentAppBtn.visibility = View.VISIBLE
            } else {
                setDefaultPaymentAppBtn.visibility = View.INVISIBLE
            }
        }
        setDefaultPaymentAppBtn.setOnClickListener {
            statusViewModel.setDefaultApplication(requireActivity())
        }

        val isUserAuthTv: TextView = binding.statusUserAuthenticated
        statusViewModel.isUserAuthenticated().observe(viewLifecycleOwner) {
            isUserAuthTv.text = it.toString()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        statusViewModel.updateIsUserAuthenticated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}