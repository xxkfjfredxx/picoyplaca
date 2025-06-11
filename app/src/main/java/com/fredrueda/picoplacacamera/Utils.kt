package com.fredrueda.picoplacacamera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.googlecode.tesseract.android.TessBaseAPI
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    companion object {
        val ciudadesDisponibles = listOf(
            "Selecciona tu ciudad",
            "Armenia",
            "Barbosa",
            "Barranquilla",
            "Bello",
            "Bogotá",
            "Bucaramanga",
            "Buenaventura",
            "Caldas",
            "Cali",
            "Cartagena",
            "Copacabana",
            "Cúcuta",
            "Dosquebradas",
            "Envigado",
            "Fusagasugá",
            "Girardota",
            "Girardot",
            "Ibagué",
            "Ipiales",
            "Itagüí",
            "La Estrella",
            "Malambo",
            "Manizales",
            "Medellín",
            "Montería",
            "Murillo",
            "Ocaña",
            "Pamplona",
            "Pasto",
            "Pereira",
            "Popayán",
            "Quibdó",
            "Sabaneta",
            "Santa Cruz de Lorica",
            "Santa Marta",
            "Sincelejo",
            "Soacha",
            "Soledad",
            "Tunja",
            "Turbaco",
            "Villavicencio"
        )
    }
/*
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            val processedBitmap = preprocessImage(bitmap)
            val recognizedText = extractTextFromBitmap(processedBitmap)
            binding.etPlaca.setText(recognizedText)
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(mat, mat, org.opencv.core.Size(5.0, 5.0), 0.0)
        Imgproc.threshold(mat, mat, 0.0, 255.0, Imgproc.THRESH_OTSU)
        val processedBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, processedBitmap)
        return processedBitmap
    }

    private fun extractTextFromBitmap(bitmap: Bitmap): String {
        val tessBaseAPI = TessBaseAPI()
        val dataPath = filesDir.absolutePath + "/tesseract/"
        val lang = "spa"

        try {
            tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
            tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE)

            tessBaseAPI.init(dataPath, lang)
            tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO)
            tessBaseAPI.setImage(bitmap)
            val extractedText = tessBaseAPI.utF8Text
            tessBaseAPI.end()
            return extractedText.trim()
        } catch (e: Exception) {
            Log.e("extractTextFromBitmap", "Error al inicializar TessBaseAPI", e)
            return ""
        }
    }

    private fun copyTessDataIfNeeded() {
        val dataPath = File(filesDir, "tesseract")
        val tessDataDir = File(dataPath, "tessdata")
        val lang = "spa"
        val trainedDataFile = File(tessDataDir, "$lang.traineddata")

        if (!trainedDataFile.exists()) {
            tessDataDir.mkdirs()
            try {
                assets.open("tessdata/$lang.traineddata").use { inputStream ->
                    trainedDataFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d("copyTessData", "Archivo $lang.traineddata copiado correctamente a: ${trainedDataFile.absolutePath}")
            } catch (e: IOException) {
                Log.e("copyTessData", "Error al copiar $lang.traineddata", e)
            }
        } else {
            Log.d("copyTessData", "El archivo $lang.traineddata ya existe en: ${trainedDataFile.absolutePath}")
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT)
                    .show()
                null
            }
            photoFile?.also {
                imageUri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }


 */
}