package dev.iconpln.mims.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.iconpln.mims.data.remote.service.ApiService
import dev.iconpln.mims.ui.inspeksi_material.AGOCreateMaterialInspectionViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetATTBViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetATTBWithRefNumViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetDataInspectionReturnTeamViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetInspectionReturnViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetManufacturerViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterBudgetStatusViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterClassificationViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterTimViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMaterialInspectionBySNViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetNoReferensiAutoViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetStorLocViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOUpdateInspectionTeamViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOUpdateMaterialInspectionBySNViewModel
import dev.iconpln.mims.ui.monitoring_stok.MonitoringStokViewModel
import dev.iconpln.mims.ui.pemakaian.ap2t.DeleteSnViewModel
import dev.iconpln.mims.ui.pemakaian.ap2t.DetailMaterialPemakaianAp2tViewModel
import dev.iconpln.mims.ui.pemakaian.ap2t.DetailPemakaianUlpAp2tViewModel
import dev.iconpln.mims.ui.pemakaian.ap2t.InputSnPemakaianAp2tViewModel
import dev.iconpln.mims.ui.pemakaian.ap2t.ResultSapViewModel
import dev.iconpln.mims.ui.pemakaian.maximo.DetailPemakaianMaximoViewModel
import dev.iconpln.mims.ui.pemakaian.maximo.InputSnPemakaianMaximoViewModel
import dev.iconpln.mims.ui.pemakaian.yantek.DetailYantekViewModel
import dev.iconpln.mims.ui.pemakaian.yantek.InputSnPemakaianYantekViewModel
import dev.iconpln.mims.ui.registrasi_approval.approval.ApprovalMaterialViewModel
import dev.iconpln.mims.ui.registrasi_approval.registrasi.RegistrasiMaterialViewModel

class ViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(RegistrasiMaterialViewModel::class.java) -> {
                return RegistrasiMaterialViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(ApprovalMaterialViewModel::class.java) -> {
                return ApprovalMaterialViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(DetailPemakaianUlpAp2tViewModel::class.java) -> {
                return DetailPemakaianUlpAp2tViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(DetailMaterialPemakaianAp2tViewModel::class.java) -> {
                return DetailMaterialPemakaianAp2tViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(DetailYantekViewModel::class.java) -> {
                return DetailYantekViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(DetailPemakaianMaximoViewModel::class.java) -> {
                return DetailPemakaianMaximoViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(InputSnPemakaianYantekViewModel::class.java) -> {
                return InputSnPemakaianYantekViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(InputSnPemakaianMaximoViewModel::class.java) -> {
                return InputSnPemakaianMaximoViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(InputSnPemakaianAp2tViewModel::class.java) -> {
                return InputSnPemakaianAp2tViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(DeleteSnViewModel::class.java) -> {
                return DeleteSnViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(ResultSapViewModel::class.java) -> {
                return ResultSapViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(MonitoringStokViewModel::class.java) -> {
                return MonitoringStokViewModel(apiService) as T
            }

            // ----------------- AGO VM ----------------- //
            modelClass.isAssignableFrom(AGOGetInspectionReturnViewModel::class.java) -> {
                return AGOGetInspectionReturnViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetATTBViewModel::class.java) -> {
                return AGOGetATTBViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetATTBWithRefNumViewModel::class.java) -> {
                return AGOGetATTBWithRefNumViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetMaterialInspectionBySNViewModel::class.java) -> {
                return AGOGetMaterialInspectionBySNViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetStorLocViewModel::class.java) -> {
                return AGOGetStorLocViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetDataInspectionReturnTeamViewModel::class.java) -> {
                return AGOGetDataInspectionReturnTeamViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetMasterTimViewModel::class.java) -> {
                return AGOGetMasterTimViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetMasterBudgetStatusViewModel::class.java) -> {
                return AGOGetMasterBudgetStatusViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetManufacturerViewModel::class.java) -> {
                return AGOGetManufacturerViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetMasterClassificationViewModel::class.java) -> {
                return AGOGetMasterClassificationViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOCreateMaterialInspectionViewModel::class.java) -> {
                return AGOCreateMaterialInspectionViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOUpdateMaterialInspectionBySNViewModel::class.java) -> {
                return AGOUpdateMaterialInspectionBySNViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOUpdateInspectionTeamViewModel::class.java) -> {
                return AGOUpdateInspectionTeamViewModel(apiService) as T
            }

            modelClass.isAssignableFrom(AGOGetNoReferensiAutoViewModel::class.java) -> {
                return AGOGetNoReferensiAutoViewModel(apiService) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}