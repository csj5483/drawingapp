package com.csj.drawingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private var brushDialog: Dialog? = null
    private var smallBtn: ImageView? = null
    private var midSmallBtn: ImageView? = null
    private var mediumBtn: ImageView? = null
    private var aMediumBtn: ImageView? = null
    private var largeBtn: ImageView? = null
    var colorDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val animate = AnimationUtils.loadAnimation(this, R.anim.scale_up)
        brushDialog = Dialog(this)
        brushDialog!!.setContentView(R.layout.dialog_brush_size)
        brushDialog!!.setTitle("Brush size :")
        smallBtn = brushDialog!!.ib_small_brush
        midSmallBtn = brushDialog!!.ib_small_medium_brush
        mediumBtn = brushDialog!!.ib_medium_brush
        aMediumBtn = brushDialog!!.ib_a_medium_brush
        largeBtn = brushDialog!!.ib_large_brush
        smallBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.selected_brush
            )
        )
        colorDialog = Dialog(this)
        colorDialog!!.setContentView(R.layout.color_dialog)
        colorDialog!!.setTitle("Color")
        drawing_view.setSizeForBrush(10.toFloat())


        ib_brush.setOnClickListener {
            ib_brush.startAnimation(animate)
            showBrushSizeChooserDialog()
        }

        ib_gallery.setOnClickListener {

            if (isReadStorageAllowed()) {
                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, GALLERY)
            } else {
                requestStoragePermission()
            }
        }

        ib_undo.setOnClickListener {
            ib_undo.startAnimation(animate)
            drawing_view.onClickUndo()
        }
        ib_redo.setOnClickListener {
            ib_redo.startAnimation(animate)
            drawing_view.onClickRedo()
        }
        ib_clear.setOnClickListener {
            ib_clear.startAnimation(animate)

            drawing_view.clearAll()
        }
        ib_color.setOnClickListener {
            ib_color.startAnimation(animate)
            colorDialog!!.show()
        }

        ib_save.setOnClickListener {
            ib_save.startAnimation(animate)
            if (isReadStorageAllowed()) {

                BitmapAsyncTask(getBitmapFromView(fl_drawing_view_container)).execute()
            } else {
                requestStoragePermission()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission granted now you can read the storage files.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Oops you just denied the permission.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                try {
                    if (data!!.data != null) {
                        iv_background.visibility = View.VISIBLE

                        iv_background.setImageURI(data.data)
                    } else {
                        // If the selected image is not valid. Or not selected.
                        Toast.makeText(
                            this@MainActivity,
                            "Error in parsing the image or its corrupted.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showBrushSizeChooserDialog() {
        smallBtn!!.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(10.toFloat())
            changeColor(it)
            brushDialog!!.dismiss()
        })
        midSmallBtn!!.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(15.toFloat())
            changeColor(it)
            brushDialog!!.dismiss()
        })


        mediumBtn!!.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(20.toFloat())
            changeColor(it)
            brushDialog!!.dismiss()
        })


        aMediumBtn!!.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(25.toFloat())
            changeColor(it)
            brushDialog!!.dismiss()
        })


        largeBtn!!.setOnClickListener(View.OnClickListener {
            drawing_view.setSizeForBrush(30.toFloat())
            changeColor(it)
            brushDialog!!.dismiss()
        })
        brushDialog!!.show()
    }

    private fun changeColor(view: View) {
        smallBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.normal_brush
            )
        )
        midSmallBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.normal_brush
            )
        )
        mediumBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.normal_brush
            )
        )
        aMediumBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.normal_brush
            )
        )
        largeBtn!!.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.normal_brush
            )
        )
        when (view) {
            smallBtn -> smallBtn!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.selected_brush
                )
            )
            midSmallBtn -> midSmallBtn!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.selected_brush
                )
            )
            mediumBtn -> mediumBtn!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.selected_brush
                )
            )
            aMediumBtn -> aMediumBtn!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.selected_brush
                )
            )
            largeBtn -> largeBtn!!.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.selected_brush
                )
            )
        }

    }

    private fun requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
        }
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_PERMISSION_CODE
        )
    }
    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
           canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)

        return returnedBitmap
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BitmapAsyncTask(val mBitmap: Bitmap?) :
        AsyncTask<Any, Void, String>() {

        @Suppress("DEPRECATION")
        private var mDialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any): String {

            var result = ""

            if (mBitmap != null) {

                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(
                        externalCacheDir!!.absoluteFile.toString()
                                + File.separator + "DrawingApp" + System.currentTimeMillis() / 1000 + ".jpg"
                    )
                    val fo =
                        FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = f.absolutePath
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            cancelProgressDialog()

            if (!result.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "File saved successfully :$result",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Something went wrong while saving the file.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            MediaScannerConnection.scanFile(
                this@MainActivity, arrayOf(result), null
            ) { path, uri ->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    uri
                )
                shareIntent.type =
                    "image/jpeg"
                startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Share"
                    )
                )
            }
        }

        private fun showProgressDialog() {
            @Suppress("DEPRECATION")
            mDialog = ProgressDialog.show(
                this@MainActivity,
                "",
                "Saving your image..."
            )
        }


        private fun cancelProgressDialog() {
            if (mDialog != null) {
                mDialog!!.dismiss()
                mDialog = null
            }
        }
    }

    companion object {

        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }

    fun pickerColor(view: View) {
        val colorBtn = view as ImageView
        val colorTag = colorBtn.tag.toString()
        drawing_view.setColor(colorTag)


        ib_brush.setColorFilter(Color.parseColor(colorTag), android.graphics.PorterDuff.Mode.SRC_IN)
        colorDialog!!.dismiss()


    }

}