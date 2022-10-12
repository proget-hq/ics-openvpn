package pl.proget.openvpn.logs

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod
import net.lingala.zip4j.model.enums.EncryptionMethod
import java.io.File

class ZipCompressor {

    fun zip(
        files: List<File>,
        destination: File,
        password: String? = null
    ) {
        ZipFile(destination).run {
            ZipParameters().apply {
                compressionMethod = CompressionMethod.DEFLATE
                compressionLevel = CompressionLevel.MAXIMUM

                password?.let {
                    isEncryptFiles = true
                    encryptionMethod = EncryptionMethod.ZIP_STANDARD
                    setPassword(password.toCharArray())
                }
            }.let {
                addFiles(files, it)
            }
        }
    }
}
