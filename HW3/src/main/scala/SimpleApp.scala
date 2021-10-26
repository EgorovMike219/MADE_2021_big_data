import java.io.{File, PrintWriter}
import breeze.linalg.{DenseMatrix, DenseVector, argsort, sum}

import scala.collection.mutable.ArrayBuffer


object SimpleApp {
  def readCsv(fileName: String): Array[Array[Double]] = {
    val result: ArrayBuffer[Array[Double]] = ArrayBuffer()
    var isHeader: Boolean = true
    val bufferedSource = io.Source.fromFile(fileName)
    for (line <- bufferedSource.getLines) {
      val Array(_, weight, length1, length2,
      length3, height, width) = line.split(",").map(_.trim)
      if (!isHeader) {
        // проигнорирую информацию о виде рыбы
        result.append(Array(weight.toDouble, length1.toDouble,
          length2.toDouble, length3.toDouble, height.toDouble, width.toDouble))
      } else {
        isHeader = false
      }
    }
    bufferedSource.close
    result.toArray
  }

  def regress(observations: DenseMatrix[Double], outputs: DenseVector[Double]):DenseVector[Double] = {
    // X * w = y
    // X.t * X dot w = X.t * y
    // cov * w = scaled
    val cov =  DenseMatrix.zeros[Double](observations.cols, observations.cols) + (observations.t * observations)
    val scaled = DenseVector.zeros[Double](observations.cols) + (observations.t * outputs)
    cov \ scaled  // аналог linalg.solve из numpy
  }

  def validate(y_true: DenseVector[Double], y_pred: DenseVector[Double], resultFile: PrintWriter): Unit = {
    resultFile.write("Реальный вес рыбы " + y_true(0 to 10) + "\n")
    resultFile.write("Предсказанный вес рыбы " + y_pred(0 to 10) + "\n")
    // посчитаем ошибку на обучающей выборке
    val error_train = ((y_true - y_pred) dot (y_true - y_pred)) / y_pred.length
    resultFile.write("MSE " + error_train + "\n")
    // посчитаем коэф детерминации
    val mean_y_true = sum(y_true) / y_true.length
    val r2_train =  1 - error_train / (((mean_y_true - y_true) dot (mean_y_true - y_true))
      / y_pred.length)
    resultFile.write("Коэффициент детерминации " + r2_train + "\n")
  }

  def main(args: Array[String]): Unit = {
    val resultFileName = "src/main/data/result.txt"
    val resultFile = new PrintWriter(new File(resultFileName))
    // считывание файла с тренировочными данными
    val trainFileName = "src/main/data/train.csv"
    val trainData = readCsv(trainFileName)
    val trainDataMatrix = DenseMatrix(trainData:_*)
    resultFile.write("Dataset: Fish market. Link: https://www.kaggle.com/aungpyaeap/fish-market")
    resultFile.write("Train data dim: " + trainDataMatrix.rows + " " + trainDataMatrix.cols + "\n")
    // будем предсказывать вес рыбы
    // сразу добавлю столбец единиц чтобы обучить свободный член в регрессии
    var X_train = DenseMatrix.horzcat(DenseMatrix.ones[Double](trainDataMatrix.rows, 1), trainDataMatrix(::, 1 to -1))
    var y_train = trainDataMatrix(::, 0)
    // загрузим тестовые данные
    val testFileName = "src/main/data/test.csv"
    val testData = readCsv(testFileName)
    val testDataMatrix = DenseMatrix(testData:_*)
    resultFile.write("Test data dim: " + testDataMatrix.rows + " " + testDataMatrix.cols + "\n")
    val X_test = DenseMatrix.horzcat(DenseMatrix.ones[Double](testDataMatrix.rows, 1), testDataMatrix(::, 1 to -1))
    val y_test = testDataMatrix(::, 0)
    // обучим нашу "модель"
    resultFile.write("\nОбучим обычную линейную регрессию\n")
    var regressionWeights = regress(X_train, y_train)
    resultFile.write("Веса модели " + regressionWeights + "\n")
    var prediction_train = X_train * regressionWeights
    resultFile.write("\nОбучающая выборка" + "\n")
    validate(y_train, prediction_train, resultFile)
    // теперь проверим как работает наша модель на тестовом наборе данных
    var prediction_test = X_test * regressionWeights
    resultFile.write("\nТестовая выборка" + "\n")
    validate(y_test, prediction_test, resultFile)
    // Попробуем улучшить модель выкинув выбросы
    val train_errors = (y_train - prediction_train) *:* (y_train - prediction_train)
    val good_indexes = argsort(train_errors).reverse.drop(5)
    y_train = y_train(good_indexes).toDenseVector
    X_train = X_train(good_indexes, ::).toDenseMatrix
    // обучим нашу новую "модель"
    resultFile.write("\nУдалим из данных, те примеры на которых модель ошибалась сильнее всего, и переобучим модель.\n")
    regressionWeights = regress(X_train, y_train)
    resultFile.write("Веса новой модели " + regressionWeights + "\n")
    prediction_train = X_train * regressionWeights
    resultFile.write("\nОбучающая выборка\n")
    validate(y_train, prediction_train, resultFile)
    // теперь проверим как работает наша модель на тестовом наборе данных
    prediction_test = X_test * regressionWeights
    resultFile.write("\nТестовая выборка\n")
    validate(y_test, prediction_test, resultFile)
    resultFile.close()
  }
}
