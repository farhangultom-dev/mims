package dev.iconpln.mims.ui.pengujian

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.databinding.ActivityPengujianBinding
import dev.iconpln.mims.ui.pengujian.pengujian_detail.PengujianDetailActivity
import dev.iconpln.mims.ui.pengujian.petugas.PetugasPengujianActivity
import dev.iconpln.mims.ui.role.pabrikan.pengujian.PengujianViewModel

class PengujianActivity : BaseActivity() {
    private lateinit var binding: ActivityPengujianBinding
    private val pengujianViewModel: PengujianViewModel by viewModels()
    private lateinit var adapter: PengujianAdapter
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listPengujian: List<dev.iconpln.mims.data.local.database.TPengujian>
    private val listKategori: ArrayList<String> = ArrayList()
    private var kategori = "ALL"
    private var noPengujian = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengujianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        listPengujian = daoSession.tPengujianDao.queryBuilder().list()
        listKategori.add("ALL")
        for (i in listPengujian) {
            listKategori.add(i.statusUji)
        }

        adapter = PengujianAdapter(arrayListOf(), object : PengujianAdapter.OnAdapterListener {
            override fun onClick(pengujian: dev.iconpln.mims.data.local.database.TPengujian) {
                startActivity(
                    Intent(this@PengujianActivity, PengujianDetailActivity::class.java)
                        .putExtra("noPengujian", pengujian.noPengujian)
                )
            }

        }, object : PengujianAdapter.OnAdapterListenerPetugas {
            override fun onClick(pengujian: dev.iconpln.mims.data.local.database.TPengujian) {
                startActivity(
                    Intent(this@PengujianActivity, PetugasPengujianActivity::class.java)
                        .putExtra("noPengujian", pengujian.noPengujian)
                )
            }

        })

        adapter.setPengujianList(listPengujian)

        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }

            rvPengujian.adapter = adapter
            rvPengujian.setHasFixedSize(true)
            rvPengujian.layoutManager =
                LinearLayoutManager(this@PengujianActivity, LinearLayoutManager.VERTICAL, false)

            val adapterKategori = ArrayAdapter(
                this@PengujianActivity,
                R.layout.simple_dropdown_item_1line,
                listKategori.distinct()
            )
            binding.dropdownKategori.setAdapter(adapterKategori)
            binding.dropdownKategori.setOnItemClickListener { parent, view, position, id ->
                kategori = binding.dropdownKategori.text.toString()
                doSearch()
            }

            srcNoPengujian.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    noPengujian = s.toString()
                    doSearch()
                }

            })
        }

    }

    private fun doSearch() {
        if (kategori == "ALL") {
            val listSearch = daoSession.tPengujianDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDao.Properties.NoPengujian.like(
                        "%" + noPengujian + "%"
                    )
                ).list()
            adapter.setPengujianList(listSearch)
        } else {
            val listSearch = daoSession.tPengujianDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDao.Properties.StatusUji.eq(
                        kategori
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDao.Properties.NoPengujian.like(
                        "%" + noPengujian + "%"
                    )
                ).list()
            adapter.setPengujianList(listSearch)
        }
    }

}