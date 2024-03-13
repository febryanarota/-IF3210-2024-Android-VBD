import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.bondoman.models.TokenRes
import com.example.bondoman.services.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenManager {
    private val KEY = "token"
    private var EXP = 0
    private var IAT = 0
    private val SHARED_PREFS_NAME = "app_shared_prefs"

    private lateinit var encryptedSharedPreferences: EncryptedSharedPreferences
    fun init(context: Context) {
        EXP = 0
        IAT = 0
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as EncryptedSharedPreferences

        val apiService = RetrofitInstance.auth
        var token = getToken()
        if (token != null) {
            apiService.checkToken("Bearer $token").enqueue(object : Callback<TokenRes> {
                override fun onResponse(call: Call<TokenRes>, response: Response<TokenRes>) {
                    if (response.isSuccessful) {
                        EXP = response.body()?.exp!!
                        IAT = response.body()?.iat!!
                        Log.i("TOKEN MANAGER", "EXP = ${EXP}; IAT = ${IAT}")
                    }
                }
                override fun onFailure(call: Call<TokenRes>, t: Throwable) {
                    Log.e("getExpFail", "${t.message}", t)
                }
            })
        }
    }

    fun saveToken(token : String) {
        encryptedSharedPreferences.edit().putString(KEY, token).apply()
    }

    fun getToken(): String? {
        return  encryptedSharedPreferences.getString(KEY, null)
    }

    fun getRemainingTime(): Long {
        var currTime = System.currentTimeMillis() / 1000
        return EXP - currTime
    }

    fun removeToken() {
        encryptedSharedPreferences.edit().remove(KEY).apply()
    }
}
