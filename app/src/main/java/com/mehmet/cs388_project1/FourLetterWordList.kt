// author: calren
object FourLetterWordList {
    // List of most common 4 letter words from: https://7esl.com/4-letter-words/
    const val defaultFourLetterWords =
        "Area,Army,Baby"
    const val sportFourLetterWords = "Ball,Base,Ring,Rink,Goal"
    const val foodFourLetterWords = "Food,Meat,Rice, Fish,Cake"
    const val animalFourLetterWords = "Bear,Lion,Deer, Wolf,Duck,Fish"

    var currentFourLetterWords = defaultFourLetterWords

    fun switchWordList(wordList: String) {
        currentFourLetterWords = wordList
    }

    // Returns a list of four letter words as a list
    fun getAllFourLetterWords(): List<String> {
        return currentFourLetterWords.split(",")
    }

    // Returns a random four letter word from the list in all caps
    fun getRandomFourLetterWord(): String {
        val allWords = getAllFourLetterWords()
        val randomNumber = (0..allWords.size).shuffled().last()
        return allWords[randomNumber].uppercase()
    }
}