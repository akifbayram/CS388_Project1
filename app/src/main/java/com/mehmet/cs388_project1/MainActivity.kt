package com.mehmet.cs388_project1

import FourLetterWordList
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.jinatonic.confetti.CommonConfetti
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    private var wordToGuess = FourLetterWordList.getRandomFourLetterWord()
    private var numGuesses = 0
    private var successfullGuesses = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enteredGuess: EditText = findViewById(R.id.enteredGuess)
        val guessButton: Button = findViewById(R.id.guessButton)
        val subheader: TextView = findViewById(R.id.subheader)
        val mainLayout: ConstraintLayout = findViewById(R.id.container)
        val dropdown : Spinner = findViewById(R.id.dropdown)
        val wordList = arrayOf("Default", "Sports", "Food", "Animals")




        subheader.text = getString(R.string.streak, successfullGuesses)

        val answers = mutableListOf(
            findViewById<TextView>(R.id.answer1),
            findViewById<TextView>(R.id.answer2),
            findViewById<TextView>(R.id.answer3)
        )
        val guesses = mutableListOf(
            findViewById<TextView>(R.id.guess1),
            findViewById<TextView>(R.id.guess2),
            findViewById<TextView>(R.id.guess3)
        )

        val correct = findViewById<TextView>(R.id.correctAnswer)

        /**
         * Parameters / Fields:
         *   wordToGuess : String - the target word the user is trying to guess
         *   guess : String - what the user entered as their guess
         *
         * Returns a String of 'O', '+', and 'X', where:
         *   'O' represents the right letter in the right place
         *   '+' represents the right letter in the wrong place
         *   'X' represents a letter not in the target word
         */
        fun checkGuess(guess: String): String {
            var result = ""
            for (i in 0..3) {
                if (guess[i] == wordToGuess[i]) {
                    result += "O"
                } else if (guess[i] in wordToGuess) {
                    result += "+"
                } else {
                    result += "X"
                }
            }
            return result
        }

        fun createSpannableResult(guess: String, result: String): SpannableString {
            val spannableGuess = SpannableString(guess)
            for (i in result.indices) {
                when (result[i]) {
                    'O' -> {
                        spannableGuess.setSpan(ForegroundColorSpan(Color.GREEN), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    '+' -> {
                        spannableGuess.setSpan(ForegroundColorSpan(Color.YELLOW), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    'X' -> {
                        spannableGuess.setSpan(ForegroundColorSpan(Color.RED), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            return spannableGuess
        }


        fun resetGame() {
            wordToGuess = FourLetterWordList.getRandomFourLetterWord()
            numGuesses = 0
            for (i in 0..2) {
                guesses[i].text = ""
                guesses[i].visibility = TextView.INVISIBLE
                answers[i].text = ""
                answers[i].visibility = TextView.INVISIBLE
            }
            correct.text = ""
            correct.visibility = TextView.INVISIBLE

            enteredGuess.setText("")
            enteredGuess.isFocusable = true
            enteredGuess.isEnabled = true
            enteredGuess.isCursorVisible = true
            enteredGuess.keyListener = EditText(this).keyListener

            guessButton.text = getString(R.string.guess_btn)
        }

        fun switchWordList(wordList: String) {
            FourLetterWordList.switchWordList(wordList)
            resetGame()
        }

        dropdown.adapter = ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, wordList)
        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (wordList[position]) {
                    "Default" -> switchWordList(FourLetterWordList.defaultFourLetterWords)
                    "Sports" -> switchWordList(FourLetterWordList.sportFourLetterWords)
                    "Food" -> switchWordList(FourLetterWordList.foodFourLetterWords)
                    "Animals" -> switchWordList(FourLetterWordList.animalFourLetterWords)
                }
                resetGame()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        fun processGuess() {
            if (numGuesses < 3) {
                val guess: String = enteredGuess.text.toString().uppercase()
                val result: String = checkGuess(guess)
                val spannableResult: SpannableString = createSpannableResult(guess, result)

                enteredGuess.setText("")

                guesses[numGuesses].text = guess
                guesses[numGuesses].visibility = TextView.VISIBLE
                answers[numGuesses].setText(spannableResult, TextView.BufferType.SPANNABLE)
                answers[numGuesses].visibility = TextView.VISIBLE
                numGuesses++

                if (result == "OOOO") {
                    Toast.makeText(this, R.string.correct, Toast.LENGTH_SHORT).show()
                    successfullGuesses++
                    subheader.text = getString(R.string.streak, successfullGuesses)
                    CommonConfetti.rainingConfetti(mainLayout, intArrayOf(Color.RED, Color.GREEN, Color.BLUE))
                        .stream(3000)
                    resetGame()
                } else if (numGuesses == 3) {
                    Toast.makeText(this, R.string.incorrect, Toast.LENGTH_SHORT).show()
                    correct.text = wordToGuess
                    correct.visibility = TextView.VISIBLE

                    // Disable the EditText and Button
                    enteredGuess.setText("")
                    enteredGuess.isFocusable = false
                    enteredGuess.isEnabled = false
                    enteredGuess.isCursorVisible = false
                    enteredGuess.keyListener = null

                    successfullGuesses = 0
                    subheader.text = getString(R.string.streak, successfullGuesses)

                    guessButton.text = getString(R.string.reset)
                }
            }
        }

        enteredGuess.setOnEditorActionListener { _, actionId, _ ->
            val input = enteredGuess.text.toString()

            if (!input.matches(Regex("^[a-zA-Z]{4}$"))) {
                Toast.makeText(this, "Input must be 4 letters", Toast.LENGTH_SHORT).show()
                true
            } else {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    processGuess()
                    true
                } else {
                    false
                }
            }
        }

        guessButton.setOnClickListener {
            val input = enteredGuess.text.toString()

            if (guessButton.text.toString() == "Reset") {
                resetGame()
            } else if (!input.matches(Regex("^[a-zA-Z]{4}$"))) {
                Toast.makeText(this, "Input must be 4 letters", Toast.LENGTH_SHORT).show()
            } else {
                processGuess()
            }
        }

    }
}