package connectfour
import java.util.Collections ; import kotlin.system.exitProcess

var P1 = ""; var P2 = ""; var currentPlayer = ""; var p1Score = 0; var p2Score = 0
var P1Squares = mutableListOf<Int>(); var P2Squares = mutableListOf<Int>(); var currentSquares = mutableListOf<Int>()
var FSB:MutableList<MutableList<String>> = mutableListOf()
val FSBSquares = mutableListOf<MutableList<Int>>()
var rows = 6; var columns = 7; var move = " "; var moveCounter = 0
var numGames = 1; var totalGames = 0; var onlyOneGame = true

fun main() {
    println("Connect Four")
    println("First Player's name:")
    P1 = readln()
    currentPlayer = P1
    println("Second Player's name:")
    P2 = readln()
    dimChecker()
    fourSquareBoard(rows, columns)
    amountOfGames()
    currGame(numGames, onlyOneGame)
    playermove(rows, columns)
}

fun dimChecker(): String {
    println("Set the board dimensions (Rows x Columns)\nPress Enter for default (6 x 7)")
    val dimPattern = "\\d+[xX]\\d+".toRegex()
    var dimension = readln().uppercase().replace("\t", "").replace(" ", "")
    when {
        dimension.isEmpty() -> return "$rows x $columns"

        !dimension.matches(dimPattern) -> {
            println("Invalid input"); dimension = dimChecker() }

        else -> {
            rows = dimension.split("x", "X", ignoreCase = true).map { it.trim().toInt() }[0]
            columns = dimension.split("x", "X", ignoreCase = true).map { it.trim().toInt() }[1]
            when {
                rows !in 5..9 -> {
                    println("Board rows should be from 5 to 9"); dimension = dimChecker() }
                columns !in 5..9 -> {
                    println("Board columns should be from 5 to 9"); dimension = dimChecker() }
            }
        }
    }
    return dimension
}

fun amountOfGames() {
    println("Do you want to play single or multiple games?\nFor a single game, input 1 or press Enter")
    println("Input a number of games:")
    val gameAmount = readln()
    when {
        !gameAmount.isEmpty() && (gameAmount.toIntOrNull() == null || gameAmount.toInt() <= 0) ->  {
            println("Invalid input"); amountOfGames() }

        gameAmount.isEmpty() || gameAmount.toInt() == 1 -> {
            numGames = 1; println("$P1 VS $P2\n$rows X $columns board"); println("Single game") }

        gameAmount.toInt() > 1 -> {
            numGames = gameAmount.toInt(); onlyOneGame = false
            println("$P1 VS $P2\n$rows X $columns board"); println("Total $numGames games") }
    }
}

fun fourSquareBoard(row:Int, column:Int) {
    for (i in 0..column) {
        var currentColumn = mutableListOf<String>()
        for (j in 0 until row) {
            currentColumn.add("║ ") }
        FSB.add(currentColumn) }

    for (i in 1..row) {
        var currentSlot = mutableListOf<Int>(); var dim = 10* i
        currentSlot.addAll((dim..dim + column - 1).toMutableList())
        FSBSquares.add(currentSlot) }
}

fun boardPrint(rows:Int, columns:Int) {
    for (i in 1..columns) {
        print(" $i") }
    println()
    for (i in 0 until rows) {
        for (j in 0..columns) {
            print(FSB[j][i]) }
        println() }
    println("╚" + "═╩".repeat(columns -1) + "═╝")
}

fun playermove(rows:Int, columns:Int) {
    println("${currentPlayer}'s turn: ")
    move = readln().lowercase()
    when {
        move == "end" -> {
            println("Game over!"); exitProcess(1) }

        move == "ff" || move == "forfeit" -> tallyAndSwap(currentPlayer, false, true)

        move.toIntOrNull() == null -> {
            println("Incorrect column number"); playermove(rows, columns) }

        move.toInt() !in 1..columns -> {
            println("The column number is out of range (1 - $columns)"); playermove(rows, columns) }

        Collections.frequency(FSB[move.toInt() - 1], "║ ") == 0 -> {
            println("Column $move is full"); playermove(rows, columns) }

        else -> {
            for (i in rows - 1 downTo 0) {
                when {
                    FSB[move.toInt() - 1][i] == "║ " && currentPlayer == P1 -> {
                        FSB[move.toInt() - 1][i] = "║o"; P1Squares.add(FSBSquares[i][move.toInt() - 1]); moveCounter++
                        boardPrint(rows, columns); winCondition()
                        currentPlayer = P2; currentSquares = P2Squares; playermove(rows, columns) }

                    FSB[move.toInt() - 1][i] == "║ " && currentPlayer == P2 -> {
                        FSB[move.toInt() - 1][i] = "║*"; P2Squares.add(FSBSquares[i][move.toInt() - 1]); moveCounter++
                        boardPrint(rows, columns); winCondition()
                        currentPlayer = P1; currentSquares = P1Squares; playermove(rows, columns) }

                    else -> continue
                }
            }
        }
    }
}

fun winCondition() {
    P1Squares.sort(); P2Squares.sort()
    if (moveCounter < 4 ) {
        return}

    else if (moveCounter == rows * columns) {
        tallyAndSwap(currentPlayer, true)
        println("It is a draw\nScore\n${P1}: ${p1Score} ${P2}: ${p2Score}"); currGame(numGames, onlyOneGame) }

    else {
        for (i in currentSquares) {
            when {
                currentSquares.containsAll(listOf(i, i + 1, i + 2, i + 3)) -> {
                    println("Player $currentPlayer won"); tallyAndSwap(currentPlayer)
                    println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}"); currGame(numGames, onlyOneGame) }
                // Winning by horizontal consecutives
                currentSquares.containsAll(listOf(i, i + 10, i + 10 * 2, i + 10 * 3)) -> {
                    println("Player $currentPlayer won"); tallyAndSwap(currentPlayer)
                    println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}"); currGame(numGames, onlyOneGame) }
                // Winning by vertical consecutives
                currentSquares.containsAll(listOf(i, i + 11, i + 11 * 2, i + 11 * 3)) -> {
                    println("Player $currentPlayer won"); tallyAndSwap(currentPlayer)
                    println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}"); currGame(numGames, onlyOneGame) }
                // Winning by foward diagonal consecutives
                currentSquares.containsAll(listOf(i, i + 9, i + 9 * 2, i + 9 * 3)) -> {
                    println("Player $currentPlayer won"); tallyAndSwap(currentPlayer)
                    println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}"); currGame(numGames, onlyOneGame) }
                // Winning by backward diagonal consecutives
                else -> continue
            }
        }
    }
}

fun tallyAndSwap(currPlayer: String, drawChecker: Boolean = false, surrChecker: Boolean = false) {
    when {
        currPlayer == P1 && drawChecker == false && surrChecker == false -> {
            p1Score += 2; currentPlayer = P2 }

        currPlayer == P2 && drawChecker == false && surrChecker == false -> {
            p2Score += 2; currentPlayer = P1 }

        currPlayer == P1 && drawChecker == true && surrChecker == false -> {
            p1Score++; p2Score++; currentPlayer = P2 }

        currPlayer == P2 && drawChecker == true && surrChecker == false -> {
            p1Score++; p2Score++; currentPlayer = P1 }

        currPlayer == P1 && drawChecker == false && surrChecker == true -> {
            p2Score += 2; println("Player $P2 won"); println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}")
            currentPlayer = P1; currGame(numGames, onlyOneGame) }

        currPlayer == P2 && drawChecker == false && surrChecker == true -> {
            p1Score += 2; println("Player $P1 won"); println("Score\n${P1}: ${p1Score} ${P2}: ${p2Score}")
            currentPlayer = P2; currGame(numGames, onlyOneGame)}

        else -> tallyAndSwap(currentPlayer, true, false)
    }
}

fun currGame(games: Int, only1Game: Boolean = true) {
    when {
        totalGames == numGames -> {
            println("Game over!"); exitProcess(1) }

        games == 1 && only1Game == true -> {
            totalGames++; boardPrint(rows, columns); playermove(rows, columns) }

        games >= 1 && only1Game == false -> {
            totalGames++; println("Game #$totalGames")
            FSB.clear(); FSBSquares.clear(); P1Squares.clear(); P2Squares.clear(); moveCounter = 0
            fourSquareBoard(rows, columns); boardPrint(rows , columns)
            playermove(rows, columns) }
    }
}