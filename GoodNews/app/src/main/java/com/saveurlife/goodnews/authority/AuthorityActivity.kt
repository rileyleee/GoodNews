package com.saveurlife.goodnews.authority

import android.content.Intent
import android.health.connect.datatypes.units.Length
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.saveurlife.goodnews.R
import com.saveurlife.goodnews.databinding.ActivityAuthorityBinding
import com.saveurlife.goodnews.enterinfo.EnterInfoActivity
import com.saveurlife.goodnews.tutorial.TutorialActivity

class AuthorityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.authButton.setOnClickListener {
            Toast.makeText(this, "권한 설정", Toast.LENGTH_SHORT).show()
            val i = Intent(this, EnterInfoActivity::class.java)
            startActivity(i)
            finish()
        }


    }
}