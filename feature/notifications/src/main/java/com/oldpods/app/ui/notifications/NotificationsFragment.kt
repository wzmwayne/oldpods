package com.oldpods.app.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.oldpods.app.common.PrefsManager
import com.oldpods.app.model.ApiMode

class NotificationsFragment : Fragment() {

    interface LoginCallback {
        fun navigateToLogin()
    }

    private var loginCallback: LoginCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginCallback) {
            loginCallback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginCallback = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvNickname = view.findViewById<TextView>(R.id.tv_nickname)
        val tvEmail = view.findViewById<TextView>(R.id.tv_email)
        val tvLevel = view.findViewById<TextView>(R.id.tv_level)
        val tvVip = view.findViewById<TextView>(R.id.tv_vip)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val radioGroup = view.findViewById<RadioGroup>(R.id.rg_api_mode)
        val radioWeb = view.findViewById<RadioButton>(R.id.rb_web)
        val radioApp = view.findViewById<RadioButton>(R.id.rb_app)

        when (PrefsManager.getApiMode(requireContext())) {
            ApiMode.WEB -> radioWeb.isChecked = true
            ApiMode.APP -> radioApp.isChecked = true
        }

        updateLoginState(tvNickname, tvEmail, tvLevel, tvVip, btnLogin, btnLogout)

        btnLogin.setOnClickListener {
            loginCallback?.navigateToLogin()
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确定") { _, _ ->
                    PrefsManager.clearUserInfo(requireContext())
                    updateLoginState(tvNickname, tvEmail, tvLevel, tvVip, btnLogin, btnLogout)
                }
                .setNegativeButton("取消", null)
                .show()
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = if (checkedId == R.id.rb_web) ApiMode.WEB else ApiMode.APP
            PrefsManager.setApiMode(requireContext(), mode)
            AlertDialog.Builder(requireContext())
                .setTitle("切换数据接口")
                .setMessage("已切换为${mode.displayName}，需要重启应用才能生效。")
                .setPositiveButton("确定", null)
                .show()
        }
    }

    private fun updateLoginState(
        tvNickname: TextView, tvEmail: TextView,
        tvLevel: TextView, tvVip: TextView,
        btnLogin: Button, btnLogout: Button
    ) {
        if (PrefsManager.isLoggedIn(requireContext())) {
            tvNickname.text = PrefsManager.getUserNickname(requireContext()).ifEmpty { "未设置昵称" }
            tvEmail.text = PrefsManager.getUserEmail(requireContext())
            tvLevel.text = "等级: ${PrefsManager.getLevel(requireContext()).ifEmpty { "0" }}"
            tvVip.text = "VIP: ${PrefsManager.getVipLevel(requireContext()).ifEmpty { "无" }}"
            btnLogin.visibility = View.GONE
            btnLogout.visibility = View.VISIBLE
        } else {
            tvNickname.text = "未登录"
            tvEmail.text = ""
            tvLevel.text = ""
            tvVip.text = ""
            btnLogin.visibility = View.VISIBLE
            btnLogout.visibility = View.GONE
        }
    }
}
