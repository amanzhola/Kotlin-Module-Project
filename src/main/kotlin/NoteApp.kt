class NoteApp {
    private val archives = mutableListOf<Archive>()

    fun start() {
        while (true) {
            when (showMenu()) {
                1 -> createArchive()
                2 -> viewArchives()
                3 -> searchNotes()
                4 -> {
                    println("Exiting the program.")
                    return
                }
                0 -> println("Going back to the main menu.")
                null -> println("No entry done. Please try again.")
                else -> println("Invalid option. Please try again.")
            }
        }
    }

    private fun showMenu(): Int? {
        return showMenu(
            "Main Menu",
            listOf("Create Archive", "View Archives", "Search Notes", "Exit")
        )
    }

    private fun createArchive() {
        while (true) {
            val name = readInputString("Enter archive name (or press Enter to go back): ")
            if (name.isBlank()) {
                println("Warning: You entered an empty value. Going back to the main menu.")
                return
            }

            if (archives.any { it.name.equals(name, ignoreCase = true) }) {
                println("An archive with the name '$name' already exists. Please choose a different name.")
                continue
            }

            archives.add(Archive(name))
            println("Archive '$name' created successfully.")
            return
        }
    }

    private fun viewArchives() {
        if (archives.isEmpty()) {
            println("No archives available.")
            return
        }
        manageList(archives) { archive -> manageArchive(archive) }
    }

    private fun manageArchive(archive: Archive) {
        while (true) {
            when (showMenuForArchive(archive)) {
                1 -> addNoteToArchive(archive)
                2 -> viewNotesInArchive(archive)
                0 -> return
                else -> println("Invalid option. Please try again.")
            }
        }
    }

    private fun showMenuForArchive(archive: Archive): Int? {
        return showMenu(
            "Managing Archive '${archive.name}'",
            listOf("Add Note", "View Notes")
        )
    }

    private fun addNoteToArchive(archive: Archive) {
        while (true) {
            val title = readInputString("Enter note title (or press Enter to go back): ")
            if (title.isBlank()) {
                println("Warning: You entered an empty value. Going back to archive management.")
                return
            }

            if (archive.notes.any { it.title.equals(title, ignoreCase = true) }) {
                println("A note with the title '$title' already exists in archive '${archive.name}'. Please use a different title.")
                continue
            }

            val content = readInputString("Enter note content (or press Enter to go back): ")
            if (content.isBlank()) {
                println("Warning: You entered an empty value. Going back to archive management.")
                return
            }

            archive.notes.add(Note(title, content))
            println("Note '$title' added to archive '${archive.name}' successfully.")
            return
        }
    }

    private fun viewNotesInArchive(archive: Archive) {
        if (archive.notes.isEmpty()) {
            println("No notes in archive '${archive.name}'.")
            return
        }

        println("\n====================")
        println("Notes in Archive '${archive.name}':")
        println("====================")
        archive.notes.forEach { note ->
            println("Title: ${note.title}")
            println("Content: ${note.content}\n")
        }
        println("====================")
    }

    private fun searchNotes() {
        val keyword = readInputString("Enter keyword to search (or press Enter to go back): ")
        if (keyword.isBlank()) {
            println("Warning: You entered an empty value. Going back to the main menu.")
            return
        }

        val foundNotes = archives.flatMap { it.notes }
            .filter { it.title.contains(keyword, ignoreCase = true) || it.content.contains(keyword, ignoreCase = true) }

        if (foundNotes.isEmpty()) {
            println("No notes found with keyword '$keyword'.")
        } else {
            println("\n====================")
            println("Found Notes:")
            println("====================")
            foundNotes.forEach { note ->
                println("Title: ${note.title}\nContent: ${note.content}\n")
            }
            println("====================")
        }
    }

    private fun manageList(items: List<Archive>, action: (Archive) -> Unit) {
        while (true) {
            println("\n====================")
            println("      Items List    ")
            println("====================")
            items.forEachIndexed { index, item -> println("${index + 1}. ${item.name}") }
            println("0. Back")
            println("====================")

            when (val choice = readInput("Select an item to manage (0 to cancel): ", items.size)) {
                null -> println("Invalid input. Please try again.")
                0 -> return
                in 1..items.size -> action(items[choice - 1])
                else -> println("Invalid option. Please try again.")
            }
        }
    }

    private fun showMenu(title: String, options: List<String>): Int? {
        println("\n====================")
        println("       $title     ")
        println("====================")
        options.forEachIndexed { index, option -> println("${index + 1}. $option") }
        println("0. Back")
        println("====================")
        return readInput("Choose an option: ", options.size)
    }

    private fun readInputString(prompt: String): String {
        print(prompt)
        return readlnOrNull() ?: ""
    }

    private fun readInput(prompt: String, maxOption: Int): Int? {
        while (true) {
            val userInput = readInputString(prompt)

            if (userInput.isBlank()) {
                return null
            }

            try {
                val input = userInput.toInt()
                if (input in 0..maxOption) {
                    return input
                } else {
                    println("Warning: Invalid option. Please enter a number between 1 and $maxOption.")
                }
            } catch (e: NumberFormatException) {
                println("Warning: Invalid input. Please enter a valid number between 1 and $maxOption.")
            }
        }
    }
}