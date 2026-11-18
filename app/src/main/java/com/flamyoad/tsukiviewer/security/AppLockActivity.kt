package com.flamyoad.tsukiviewer.security

import android.app.Activity
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.databinding.ActivityAppLockBinding

class AppLockActivity : AppCompatActivity() {
    private val appLock get() = (application as MyApplication).appLock
    private val keyguard by lazy { getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager }
    private val biometricManager by lazy { BiometricManager.from(this) }
    private val changingSetting get() = intent.hasExtra(EXTRA_ENABLED)

    private lateinit var binding: ActivityAppLockBinding
    private lateinit var prompt: BiometricPrompt
    private val promptState by viewModels<PromptState>()
    private var credentialPending = false
    private var autoPrompt = false

    private val credentialResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        credentialPending = false
        if (result.resultCode == Activity.RESULT_OK) {
            completeAuthentication()
        } else {
            authenticationCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        super.onCreate(savedInstanceState)
        binding = ActivityAppLockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        credentialPending = savedInstanceState?.getBoolean(STATE_CREDENTIAL_PENDING) == true
        autoPrompt = savedInstanceState == null

        prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    promptState.pending = false
                    completeAuthentication()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    promptState.pending = false
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            if (Build.VERSION.SDK_INT < 30 && hasDeviceCredential()) {
                                confirmDeviceCredential()
                                return
                            }
                        }
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> {
                            authenticationCancelled()
                            return
                        }
                    }
                    showError(errString)
                }
            }
        )

        if (changingSetting) {
            binding.title.setText(R.string.app_lock_confirm_title)
            binding.description.setText(
                if (intent.getBooleanExtra(EXTRA_ENABLED, false)) {
                    R.string.app_lock_enable_description
                } else {
                    R.string.app_lock_disable_description
                }
            )
            binding.unlock.setText(R.string.app_lock_confirm)
        }
        binding.unlock.setOnClickListener { authenticate() }
        binding.securitySettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(this, R.string.app_lock_setup_required, Toast.LENGTH_LONG).show()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (changingSetting) finish() else moveTaskToBack(true)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        appLock.activityStarted()
    }

    override fun onPostResume() {
        super.onPostResume()
        appLock.activityResumed()
        if (!changingSetting && !appLock.isLocked) {
            finish()
            return
        }
        updateAvailability()
        if (autoPrompt) {
            autoPrompt = false
            authenticate()
        }
    }

    override fun onStop() {
        appLock.activityStopped(isChangingConfigurations)
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_CREDENTIAL_PENDING, credentialPending)
    }

    private fun authenticate() {
        if (promptState.pending || credentialPending || isFinishing) return
        if (!canAuthenticate()) {
            showError(getText(R.string.app_lock_setup_required))
            return
        }

        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(binding.title.text)
        if (Build.VERSION.SDK_INT >= 30) {
            builder.setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
        } else if (hasBiometrics()) {
            builder.setAllowedAuthenticators(BIOMETRIC_WEAK)
                .setNegativeButtonText(getText(
                    if (hasDeviceCredential()) R.string.app_lock_use_credential else android.R.string.cancel
                ))
        } else {
            confirmDeviceCredential()
            return
        }

        setMessage(null)
        promptState.pending = true
        updateAvailability()
        prompt.authenticate(builder.build())
    }

    @Suppress("DEPRECATION")
    private fun confirmDeviceCredential() {
        val credentialIntent = keyguard.createConfirmDeviceCredentialIntent(binding.title.text, null)
        if (credentialIntent == null) {
            showError(getText(R.string.app_lock_setup_required))
            return
        }
        credentialPending = true
        updateAvailability()
        try {
            credentialResult.launch(credentialIntent)
        } catch (_: ActivityNotFoundException) {
            credentialPending = false
            showError(getText(R.string.app_lock_setup_required))
        }
    }

    private fun hasDeviceCredential(): Boolean =
        if (Build.VERSION.SDK_INT >= 23) keyguard.isDeviceSecure else keyguard.isKeyguardSecure

    private fun hasBiometrics(): Boolean =
        biometricManager.canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

    private fun canAuthenticate(): Boolean =
        if (Build.VERSION.SDK_INT >= 30) {
            biometricManager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL) ==
                BiometricManager.BIOMETRIC_SUCCESS
        } else {
            hasBiometrics() || hasDeviceCredential()
        }

    private fun updateAvailability() {
        val available = canAuthenticate()
        binding.securitySettings.isVisible = !available
        binding.unlock.isVisible = available
        binding.unlock.isEnabled = !promptState.pending && !credentialPending
        if (!available) {
            setMessage(getText(R.string.app_lock_setup_required))
        } else if (binding.message.text == getText(R.string.app_lock_setup_required)) {
            setMessage(null)
        }
    }

    private fun authenticationCancelled() {
        if (changingSetting) finish() else updateAvailability()
    }

    private fun completeAuthentication() {
        if (isFinishing) return
        if (changingSetting) {
            val enabled = intent.getBooleanExtra(EXTRA_ENABLED, false)
            if (!appLock.setEnabledAfterAuthentication(enabled)) {
                showError(getText(R.string.app_lock_save_failed))
                return
            }
        } else {
            appLock.authenticated()
        }
        finish()
    }

    private fun showError(message: CharSequence) {
        updateAvailability()
        setMessage(message)
    }

    private fun setMessage(message: CharSequence?) {
        binding.message.text = message
        binding.message.isVisible = !message.isNullOrEmpty()
    }

    class PromptState : ViewModel() {
        var pending = false
    }

    companion object {
        private const val EXTRA_ENABLED = "app_lock_enabled"
        private const val STATE_CREDENTIAL_PENDING = "credential_pending"

        internal fun unlockIntent(context: Context) = Intent(context, AppLockActivity::class.java)

        internal fun settingsIntent(context: Context, enabled: Boolean) =
            unlockIntent(context).putExtra(EXTRA_ENABLED, enabled)
    }
}
