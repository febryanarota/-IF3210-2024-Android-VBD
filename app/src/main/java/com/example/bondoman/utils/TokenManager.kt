import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
object TokenManager {
    private val KEY = "token"
    private val SHARED_PREFS_NAME = "app_shared_prefs"

    private lateinit var encryptedSharedPreferences: EncryptedSharedPreferences
    fun init(context: Context) {
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
    }

    fun saveToken(token : String) {
        encryptedSharedPreferences.edit().putString(KEY, token).apply()
    }

    fun getToken(): String? {
        return  encryptedSharedPreferences.getString(KEY, null)
    }

    fun removeToken() {
        encryptedSharedPreferences.edit().remove(KEY).apply()
    }


}
