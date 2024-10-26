package roomme

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File
import java.io.FileNotFoundException
import java.security.SecureRandom

class Utilities {
    companion object {
        val EMAIL_REGEX =
            Regex("""^(?:[a-z\d!#${'$'}%&'*+/=?^_`{|}~-]+(?:\.[a-z\d!#${'$'}%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z\d](?:[a-z\d-]*[a-z\d])?\.)+[a-z\d](?:[a-z\d-]*[a-z\d])?|\[(?:(2(5[0-5]|[0-4]\d)|1\d\d|[1-9]?\d)\.){3}(?:(2(5[0-5]|[0-4]\d)|1\d\d|[1-9]?\d)|[a-z\d-]*[a-z\d]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)])""")
        val PASSWORD_REGEX = Regex("""^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\.)(?!.*\$).{6,60}""")
        val FULLNAME_REGEX = Regex("""\w{1,20} \w{1,20}""")

        fun getHTMLFile(path: String): File {
            val file = File("resources/html/$path")

            if (file.exists())
                return file
            else
                throw FileNotFoundException("Unable to find ${file.path}")
        }

        fun generateRandomSalt(): ByteArray {
            return ByteArray(16).also {
                SecureRandom.getInstanceStrong().nextBytes(it)
            }
        }
    }
}