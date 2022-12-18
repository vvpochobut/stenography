import javax.imageio.ImageIO
import java.io.File
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.pow

fun main(){
    var work = true
    while(work){
        println("Task (hide, show, exit):")
        val command = readln()
        when(command){
            "hide" -> {
                hideImageRGB()
            }
            "show" -> {
                println("Obtaining message from image.")
                showImageRGB()
            }
            "exit" -> {
                println("Bye!")
                work = false
            }
            else ->{
                println("Wrong task: $command")
            }
        }
    }
}

fun hideImageRGB() {
    try {
        println("Input image file:")
        val inputImage = readln() //Путь и имя изображения
        println("Output image file:")
        val outputImage = readln() // Путь и имя для сохранения изображения
        println("Message to hide:")
        var messageHide = readln() //Сообщение которое мы хотим зашифровать
        println("Password:")
        var password = readln()  //пароль начальная форма
        val arrayPassword = password.toCharArray().map { it.hashCode() }  //Переводим строку password в массив символов,и каждый символ в utf_8
        password = ""
        for(i in arrayPassword){ //Переводим хэшкод каждого символа в двоичную форму
            password += tenInTwo(i)
        }
        val arrayMessage = messageHide.toCharArray().map { it.hashCode() }  //Переводим строку в массив символов,и каждый символ в utf_8
        messageHide = ""
        for(i in arrayMessage){ //Переводим хэшкод каждого символа в двоичную форму
            messageHide += tenInTwo(i)
        }

        password = password(messageHide,password)     // Обновляем длину пароля до длины сообщения
        messageHide = newHideMessage(messageHide,password) // Шифруем сообщение
        messageHide += tenInTwo(0)+tenInTwo(0)+tenInTwo(3) //Добавляем наше "окончание" массива
        val oldImage = ImageIO.read(File(inputImage)) // читаем изображение
       // var oldImage = BufferedImage(newImage.width,newImage.height,BufferedImage.TYPE_INT_RGB)
        if(oldImage.width * oldImage.height >= messageHide.length) {
            var a = 0
        Loop@   for (y in 0 until oldImage.height) {
                for (x in 0 until oldImage.width) {
                    if (a < messageHide.length) {
                        val color = Color(oldImage.getRGB(x, y)) // Получаем цветовую гамму бита
                        var oldBlue = (tenInTwo(color.blue)) // Переводим цвет в двоичную систему счисления
                        oldBlue = oldBlue.substring(0..oldBlue.length - 2) + messageHide[a]  //Изменяем последний бит цвета в доичной системе
                        var newBlue = twoInTenInt(oldBlue,2.0) // Возвращаем десятичное представление цвета
                  //    println("Old Blue color() = ${color.blue},New Blue = $newBlue, oldBlue in two = $oldBlue, message[a] = ${messageHide[a]},a = $a")
                        val newColor = Color(color.red, color.green, newBlue) //Записываем бит,с новым цветом
                        oldImage.setRGB(x, y, newColor.rgb)
                        a++
                    } else{
                    break@Loop
                    }
                }
            }
        } else {
            println("The input image is not large enough to hold this message.")
        }
        ImageIO.write(oldImage, "png",File(outputImage))
        println("Input Image: $inputImage")
        println("Output Image: $outputImage")
        println("Message saved in ${outputImage.substringAfter('/')} image.")
    } catch (e: Exception) {
        println("Can't read input file!")
    }
}

fun newHideMessage(a:String,b:String):String{
    val stop = a.length
    var new = ""
    var count = 0
    while(new.length<a.length){
        new +=((a[count]).toInt() xor (b[count]).toInt())
        count++
    }
    return new
}

fun tenInTwo(number_:Int):String{
    var number = number_
    var result = ""
    while(number != 0){
        result += (number % 2)
        number /= 2
    }
    while(result.length<8){
        result+=0
    }
    return (result.reversed())
}

fun twoInTenInt(comand: String, scale: Double): Int {
    val number = comand.reversed()
    var result = 0.toBigDecimal()
    var stepen = 0.0
    for (i in number) {
        result += i.toString().toInt().toBigDecimal() * scale.pow(stepen).toBigDecimal()
        stepen++
    }
    return result.toInt()
}

fun password(message: String,password_:String):String{
    var password = password_
    var a =0
    while(password.length<message.length){
        password += password[a]
        a++
    }
    return password
}

fun showImageRGB(){
    println("Input image file:")
    val inputImage = readln() // Читаем путь к изображению
    val oldImage = ImageIO.read(File(inputImage)) // Открываем изображение
    println("Password:")
    val password = readln() // читаем пароль
    var word =""
    var showMessage =""
    var inversString =""
    var start = 0
    Loop@  for (y in 0 until oldImage.height) {  //Перебираем высоту от 0 до макс
        for (x in 0 until oldImage.width) {  // Перебираем ширину от 0 до макс
                val color = Color(oldImage.getRGB(x, y)) // получаем цвет пикселя
                val startColor = tenInTwo(color.blue) //Получаем количество синего в двоичной форме
                val bit = startColor[startColor.length - 1] //Получаем последний бит синего цвета в двоичной форме
                if (inversString != "000000000000000000000011") { //Условие для продолжения, значение stop увеличивается при нахождении символов \u0003 и \u0000
                    if (word.length == 7) { //Если количество символов в слове равно 7,то мы добавляем еще один бит и получаем символ в двоичном представлении
                        word += bit //   Добавляем последний бит,двоичного представления символа
                        if(start == password.length){
                            start = 0
                        }
                        val char = newHideMessage(tenInTwo(password[start].hashCode()),word)
                        val newChar = twoInTenInt(char,2.0).toChar()
                        start++
                        if(word == "00000000" || word == "00000011"){
                            showMessage+=newChar //Добавляем символ к строке
                            when {
                                (inversString == "00000000" && word == "00000000") ||
                                        (inversString == "0000000000000000" && word == "00000011") ||
                                                (inversString == "" && word == "00000000") -> inversString += word
                                else -> word = ""
                            }
                            word = ""
                        }else { //Если данный символ не совпадает
                            inversString = ""
                            showMessage += newChar // Добавляем символ к нашей строке
                            word = "" // обновляем значение символа,для расшифровки нового символа
                        }
                    } else {
                        word += bit // добавляем считанный бит к двоичному представлению
                    }
                } else{
                    break@Loop  // Если переменная stop достигла значения 3,внешний цикл прерывается
                }
            }
        }
    showMessage = showMessage.substring(0,showMessage.length - 3)
    println("Message:")
    println(showMessage)
    }
