package com.oldpods.app.ui.login

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.oldpods.app.common.PrefsManager
import com.oldpods.app.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var etEmail: EditText
    private lateinit var etCode: EditText
    private lateinit var btnSendCode: Button
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private val authRepository = AuthRepository()
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmail = view.findViewById(R.id.et_email)
        etCode = view.findViewById(R.id.et_code)
        btnSendCode = view.findViewById(R.id.btn_send_code)
        btnLogin = view.findViewById(R.id.btn_login)
        progressBar = view.findViewById(R.id.progress_bar)
        tvStatus = view.findViewById(R.id.tv_status)

        btnSendCode.setOnClickListener { sendVerificationCode() }
        btnLogin.setOnClickListener { login() }
    }

    private fun sendVerificationCode() {
        val email = etEmail.text.toString().trim()
        if (email.isEmpty()) {
            etEmail.error = "请输入邮箱"
            return
        }

        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = authRepository.sendVerificationCode(email)
            result.onSuccess {
                Toast.makeText(context, "验证码已发送", Toast.LENGTH_SHORT).show()
                startCountDown()
            }.onFailure {
                Toast.makeText(context, "发送失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun startCountDown() {
        btnSendCode.isEnabled = false
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                btnSendCode.text = "${millisUntilFinished / 1000}s"
            }
            override fun onFinish() {
                btnSendCode.isEnabled = true
                btnSendCode.text = "发送验证码"
            }
        }.start()
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val code = etCode.text.toString().trim()

        if (email.isEmpty()) { etEmail.error = "请输入邮箱"; return }
        if (code.isEmpty()) { etCode.error = "请输入验证码"; return }

        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = authRepository.loginWithCode(email, code)
            result.onSuccess { response ->
                if (response.isSuccess()) {
                    PrefsManager.saveUserInfo(
                        requireContext(),
                        response.userId ?: "",
                        response.email ?: email,
                        response.uuid ?: ""
                    )
                    // Fetch user meta
                    val metaResult = authRepository.getUserMeta(email, response.uuid ?: "")
                    metaResult.onSuccess { meta ->
                        if (meta.isSuccess()) {
                            meta.data?.let { data ->
                                PrefsManager.apply {
                                    setUserNickname(requireContext(), data.nickname ?: "")
                                    setGender(requireContext(), data.gender ?: "")
                                    setLevel(requireContext(), data.level ?: "")
                                    setLevelIntegral(requireContext(), data.levelIntegral ?: "")
                                    setVipLevel(requireContext(), data.vipLevel ?: "")
                                    setVipExpDate(requireContext(), data.vipExpDate ?: "")
                                    setDescription(requireContext(), data.description ?: "")
                                }
                            }
                        }
                    }
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, response.msg ?: "登录失败", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(context, "登录失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}
