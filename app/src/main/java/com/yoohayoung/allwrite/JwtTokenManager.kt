import android.content.Context
import android.content.SharedPreferences
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.TokenExpiredException

class JwtTokenManager(
    private val secret: String,
    private val context: Context
) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("JwtTokenPrefs", Context.MODE_PRIVATE)
    }

    /**
     * JWT 토큰을 SharedPreferences에 저장합니다.
     */
    fun saveToken(token: String) {
        with(sharedPreferences.edit()) {
            putString("jwt_token", token)
            apply()
        }
    }

    /**
     * SharedPreferences에서 저장된 JWT 토큰을 가져옵니다.
     */
    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    /**
     * 저장된 JWT 토큰을 검증합니다.
     */
    fun verifyToken(): Boolean {
        val token = getToken() ?: return false
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm).build()
            verifier.verify(token)
            true
        } catch (e: JWTDecodeException) {
            false
        } catch (e: TokenExpiredException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * SharedPreferences에서 저장된 JWT 토큰을 삭제합니다.
     */
    fun deleteToken() {
        with(sharedPreferences.edit()) {
            remove("jwt_token")
            apply()
        }
    }

    /**
     * 저장된 JWT 토큰을 해독하여 payload를 반환합니다.
     */
    fun decodeToken(): Map<String, Any?>? {
        val token = getToken() ?: return null
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm).build()
            val decodedJWT = verifier.verify(token)

            // 해독된 토큰에서 클레임 추출
            val claims = decodedJWT.claims
            val payload = mutableMapOf<String, Any?>()

            for ((key, value) in claims.entries) {
                payload[key] = value.asString() ?: value.asDate() ?: value.asBoolean() ?: value.asInt() ?: value.asLong()
            }

            payload
        } catch (e: JWTDecodeException) {
            null
        } catch (e: TokenExpiredException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
