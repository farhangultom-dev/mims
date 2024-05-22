package dev.iconpln.mims.utils

object Config {

    //db local naming
    const val SCHEMA_NAME = "Mims"
    const val DIRECTORY_ROOT_NAME = "mims"
    const val DATABASE_NAME = "mims-0000000012-db"

    //end region

    // Session
    const val IS_LOGIN = "IsLogin"
    const val IS_LOGIN_SSO = "IsLoginSSO"
    const val DATETIME = "yyyy-MM-dd HH:mm:ss"
    const val DATE = "yyyy-MM-dd"
    const val NO_CODE = 0
    const val KEY_SHARED_PREF = "Sh#4R3dpreFM1m52o23Pln!!"
    const val KEY_ENCRYPTION_LOCAL_DB = "M1msDB2023!M1m52o23Pln"
    const val KEY_ENCRYPTION_FOLDER = "F00IID33rrR!!!"

    //endregion

    //photo size
    const val MAX_PHOTO_SIZE = 2000000

    //endregion

    //Constant listener

    //Key Authentication
    const val DO_LOGIN = "DO LOGIN"
    const val DO_LOGOUT = "DO LOGOUT"
    const val DO_OTP = "DO OTP"
    const val OTP_NOT_FOUND = "OTP NOT FOUND"
    const val URL_LOGIN_SSO =
        "https://iam.pln.co.id/svc-core/oauth2/auth?response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fuser%2Flogin&client_id=hbKhr2QTjnrsvQnqjxU3&scope=openid+phone+email+profile+empinfo+address"
    const val OTP_FROM_LOGIN = "login"
    const val OTP_FORM_FORGOT_PASSSWORD = "forgotPassword"
    const val KEY_OTP_FROM = "otpFrom"
    const val OTP_TERVALIDASI = "OTP tervalidasi"
    const val KEY_JWT = "jwt"
    const val KEY_JWT_WEB = "jwtWeb"
    const val KEY_AUTHORIZATION = "Authorization"
    const val KEY_IS_REMEMBER_ME = "isRememberMe"
    const val KEY_URL = "url"
    //endregion

    //Key shared prefererences dan intent extras
    const val KEY_USERNAME = "username"
    const val KEY_KODE_USER = "kdUser"
    const val KEY_FULL_NAME = "fullName"
    const val KEY_ROLE_NAME = "roleName"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PLANT = "plant"
    const val KEY_PLANT_NAME = "plantName"
    const val KEY_STOR_LOC = "storloc"
    const val KEY_STOR_LOC_NAME = "storlocName"
    const val KEY_SUBROLE_ID = "subroleId"
    const val KEY_ROLE_ID = "roleId"
    const val KEY_IS_FORGET_PASSWORD = "isForgetPassword"
    const val KEY_UPDATE_PASSWORD_BERHASIL = "update password berhasil"
    const val KEY_ANDROID_ID = "android_id"
    const val KEY_DEVICE_DATA = "device_data"
    const val KEY_NOMOR_MATERIAL = "noMaterial"
    const val KEY_NOMOR_BATCH = "noBatch"
    const val KEY_TAHUN_SN = "tahunSn"
    const val KEY_CODE = "code"
    const val KEY_CODE_STATUS = "kodeStatus"
    const val EXTRA_NO_PENGIRIMAN = "extra_no_pengiriman"
    const val KEY_DITERIMA = "105"
    const val KEY_MATERIAL_DIKIRIM = "Material Dikirim"
    const val KEY_IS_LOGIN_BIOMETRIC = "isLoginBiometric"
    const val KEY_DO_SMAR = "doSmar"
    const val KEY_NO_PO = "noPo"

    //endregion

    //Key for response code
    const val KEY_FAILURE = "failure"
    const val KEY_SUCCESS = "success"
    const val KEY_NO_DO = "noDo"
    const val KEY_RATING = "rating"
    const val KEY_RESPON_PENYEDIA = "Respon Penyedia"
    const val KEY_WAKTU_PENGIRIMAN = "Waktu Pengiriman"
    const val KEY_KUALITAS_PENERIMAAN = "Kualitas Penerimaan"
    const val KEY_CODE_RATING_31 = "31"
    const val KEY_CODE_RATING_32 = "32"
    const val KEY_CODE_RATING_33 = "33"
    const val KEY_CODE_RATING_34 = "34"
    const val KEY_CODE_RATING_35 = "35"
    const val KEY_CODE_RATING_21 = "21"
    const val KEY_CODE_RATING_22 = "22"
    const val KEY_CODE_RATING_23 = "23"
    const val KEY_CODE_RATING_24 = "24"
    const val KEY_CODE_RATING_25 = "25"
    const val KEY_CODE_RATING_11 = "11"
    const val KEY_CODE_RATING_12 = "12"
    const val KEY_CODE_RATING_13 = "13"
    const val KEY_CODE_RATING_14 = "14"
    const val KEY_CODE_RATING_15 = "15"

}