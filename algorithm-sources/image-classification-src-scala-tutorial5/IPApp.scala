/**
  * Created by pradyumnad on 10/07/15.
  */

import java.nio.file.{Files, Paths}

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


object IPApp {
  val IMAGE_CATEGORIES = List("airplanes", "bonsai", "buddha", "addias", "coats", "shoes", "watches","pants", "suit","hat","dresses","shirts")

  /**
    * @note Test method for classification on Spark
    * @param sc : Spark Context
    * @return
    */
  def testImageClassification(sc: SparkContext, path: String): String ={

    val model = KMeansModel.load(sc, IPSettings.KMEANS_PATH)
    val vocabulary = ImageUtils.vectorsToMat(model.clusterCenters)
    val desc = ImageUtils.bowDescriptors(path, vocabulary)
    val histogram = ImageUtils.matToVector(desc)

    println("-- Histogram size : " + histogram.size)
    println(histogram.toArray.mkString(" "))

    val nbModel = RandomForestModel.load(sc, IPSettings.RANDOM_FOREST_PATH)
    val p = nbModel.predict(histogram)
    (s"Test image predicted as : " + IMAGE_CATEGORIES(p.toInt))
  }


  def testImage(string: String):String = {
    val conf = new SparkConf()
      .setAppName(s"IPApp")
      .setMaster("local[*]")
      .set("spark.executor.memory", "6g")
      .set("spark.driver.memory", "6g")

    val sparkConf = new SparkConf().setAppName("ImgClassifier").setMaster("local[*]")
    val sc= SparkContext.getOrCreate(sparkConf)
    val res = testImageClassification(sc, string)

    printf(res);
    res
  }
}