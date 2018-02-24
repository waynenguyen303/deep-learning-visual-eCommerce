import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD

/**
 * Created by Mayanka on 14-Jul-15.
 */
object ModelEvaluation {
  def evaluateModel(predictionAndLabels: RDD[(Double, Double)]) = {
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val cfMatrix = metrics.confusionMatrix
    println(" |=================== Confusion matrix ==========================")
    println(cfMatrix)
    println(metrics.fMeasure)

//
//    printf(
//      s"""
//         |=================== Confusion matrix ==========================
//         |          | %-15s                     %-15s
//         |----------+----------------------------------------------------
//         |Actual = 0| %-15f                     %-15f
//         |Actual = 1| %-15f                     %-15f
//         |===============================================================
//         """.stripMargin, "Predicted = 0", "Predicted = 1",
//      cfMatrix.apply(0, 0), cfMatrix.apply(0, 1), cfMatrix.apply(1, 0), cfMatrix.apply(1, 1))
//
//    println("\nACCURACY " + ((cfMatrix(0,0) + cfMatrix(1,1))/(cfMatrix(0,0) + cfMatrix(0,1) + cfMatrix(1,0) + cfMatrix(1,1))))


    //cfMatrix.toArray

//    val fpr = metrics.falsePositiveRate(0)
//    val tpr = metrics.truePositiveRate(0)
//
//    println(
//      s"""
//         |False positive rate = $fpr
//          |True positive rate = $tpr
//     """.stripMargin)
//
//    val m = new BinaryClassificationMetrics(predictionAndLabels)
//    println("PR " + m.areaUnderPR())
//    println("AUC " + m.areaUnderROC())
  }
}
