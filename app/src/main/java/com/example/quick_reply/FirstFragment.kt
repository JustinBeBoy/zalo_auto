package com.example.quick_reply

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quick_reply.databinding.FragmentFirstBinding
import com.google.android.material.slider.Slider


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(){

    private var _binding: FragmentFirstBinding? = null
    private lateinit var listView: RecyclerView
    private lateinit var adapter: AppListAdapter
    private val appsList = mutableListOf<AppInfo>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var appIds = setOf("com.zing.zalo","com.facebook.orca","org.thunderdog.challegram", "org.telegram.messenger")

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        listView = binding.root.findViewById(R.id.all_apps)
        listView.layoutManager = LinearLayoutManager(context)
        loadInstalledApps()
        adapter = AppListAdapter(appsList)
        listView.adapter = adapter
        val saveButton: Button = binding.root.findViewById(R.id.save_btn)
        saveButton.setOnClickListener {
            saveCheckedApps()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadInstalledApps() {
        val sharedPreferences = this.requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val checkedApps = sharedPreferences.getStringSet(CHECKED_APPS_KEY, emptySet())
        val replyText = sharedPreferences.getString(REPLY_TEXT_KEY, "Reply from QuickReply App")
        val quoteReply = sharedPreferences.getBoolean(QUOTE_REPLY_KEY, false)
        val speed = sharedPreferences.getFloat(SPEECH_SPEED_KEY, 1.5f)
        val speechNoti = sharedPreferences.getBoolean(SPEECH_NOTI_KEY, true)
//        val filterRegexText = sharedPreferences.getString(FILTER_REGEX_TEXT_KEY, "^[^:]*\$")
//        val filterRegexTitle = sharedPreferences.getString(FILTER_REGEX_TITLE_KEY, "")

        binding.root.findViewById<EditText>(R.id.replyText).setText(replyText)
        binding.root.findViewById<CheckBox>(R.id.quote_reply).isChecked = quoteReply
        binding.root.findViewById<CheckBox>(R.id.speech_notifcation).isChecked = speechNoti
        binding.root.findViewById<Slider>(R.id.speech_speed).value = speed
        binding.root.findViewById<TextView>(R.id.speed_text).text = "Speech speed: $speed"
        binding.root.findViewById<Slider>(R.id.speech_speed).addOnChangeListener { _, value, _ ->
            binding.root.findViewById<TextView>(R.id.speed_text).text = "Speech speed: $value"
        }
//        binding.root.findViewById<EditText>(R.id.regex_title).setText(filterRegexTitle)
//        binding.root.findViewById<EditText>(R.id.regex_text).setText(filterRegexText)

        val packageManager = this.requireContext().packageManager
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (pkg in packages) {
            if (appIds.contains(pkg.packageName)) {
                val appName = packageManager.getApplicationLabel(pkg).toString()
                val appIcon = packageManager.getApplicationIcon(pkg.packageName)
                val packageName = pkg.packageName
                appsList.add(AppInfo(appName, packageName, appIcon, checkedApps?.contains(packageName) ?: false))
            }
        }
    }

    // Save the checked apps (package names) to SharedPreferences
    private fun saveCheckedApps() {
        val checkedApps =
            appsList.filter { it.isChecked }.map { it.packageName }.toSet()

        val sharedPreferences = this.requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(CHECKED_APPS_KEY, checkedApps)
        val replyText = binding.root.findViewById<EditText>(R.id.replyText).text
        editor.putString(REPLY_TEXT_KEY, replyText.toString())
        editor.putBoolean(QUOTE_REPLY_KEY, binding.root.findViewById<CheckBox>(R.id.quote_reply).isChecked)
        editor.putFloat(SPEECH_SPEED_KEY, binding.root.findViewById<Slider>(R.id.speech_speed).value)
        editor.putBoolean(SPEECH_NOTI_KEY,  binding.root.findViewById<CheckBox>(R.id.speech_notifcation).isChecked)
//        editor.putString(FILTER_REGEX_TITLE_KEY, binding.root.findViewById<EditText>(R.id.regex_title).text.toString())
//        editor.putString(FILTER_REGEX_TEXT_KEY, binding.root.findViewById<EditText>(R.id.regex_text).text.toString())
        editor.apply()

        Toast.makeText(this.requireContext(), "Filter apps saved!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}