import java.io.File

import org.bytedeco.javacpp.opencv_core.{Size, Mat}
import org.bytedeco.javacpp.opencv_highgui._

/**
  * Created by Mayanka on 16-Mar-16.
  */
object ResizingImage {

  def main(args: Array[String]) {
    val IMAGE_CATEGORIES = List("coats", "pants", "addias", "suit", "watches", "hat", "dresses", "shoes", "shirts")
    val PATH = "data4/test/"
    val PATH2 = "data4/test2/"
    IMAGE_CATEGORIES.foreach(f => {
      val file = new File(PATH + f)
      val listOffiles = file.listFiles()
      val file2 = new File(PATH2 + f)
      file2.mkdirs()
      var count = 1
      listOffiles.foreach(fi => {
        println(fi.getPath)
        val img = imread(fi.getPath, CV_LOAD_IMAGE_UNCHANGED)
        //src image
        var src = new Mat
        //src = img.reshape(img.channels(), (img.rows() / 4))
        org.bytedeco.javacpp.opencv_imgproc.resize(img,src,new Size(img.rows()/2,img.cols()/2))


        imwrite(file2.getPath + "/" + count + ".jpg", src)
        count = count + 1
      })
    })
  }
}
