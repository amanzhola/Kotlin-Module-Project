import kotlin.system.exitProcess

class NoteApp {
    private val archives = mutableListOf<ArchiveItem>()

    fun start() {
        while (true) {
            when (showMenu()) {
                1 -> createArchive()
                2 -> viewArchives()
                3 -> searchNotes()
                4 -> {
                    println("Exiting the program.")
                    exitProcess(0)
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

            if (archives.any { it.title.equals(name, ignoreCase = true) }) {
                println("An archive with the name '$name' already exists. Please choose a different name.")
                continue
            }

            archives.add(ArchiveItem(name))
            println("Archive '$name' created successfully.")
            return
        }
    }

    private fun viewArchives() {
        if (archives.isEmpty()) {
            println("No archives available.")
            return
        }
        // Указываем, что мы работаем только с ArchiveItem
        manageList(archives) { archive -> manageArchive(archive) }
    }

    private fun manageArchive(archive: ArchiveItem) {
        while (true) {
            when (showMenuForArchive(archive)) {
                1 -> addNoteToArchive(archive)
                2 -> viewManageableItems(archive.notes.map { NoteItem(it.title, it.content) }, "notes") // Указываем, что это заметки
                0 -> return
                else -> println("Invalid option. Please try again.")
            }
        }
    }

    private fun showMenuForArchive(archive: ArchiveItem): Int? {
        return showMenu(
            "Managing Archive '${archive.title}'",
            listOf("Add Note", "View Notes")
        )
    }

    private fun addNoteToArchive(archive: ArchiveItem) {
        if (promptForTitleAndContent { title, content ->
                if (archive.notes.any { it.title.equals(title, ignoreCase = true) }) {
                    println("A note with the title '$title' already exists. Please use a different title.")
                } else {
                    archive.notes.add(NoteItem(title, content)) // Здесь используем правильный тип
                    println("Note '$title' added to archive '${archive.title}' successfully.")
                }
            }) return
    }

    private fun <T : ManageableItem> viewManageableItems(items: List<T>, type: String) {
        if (items.isEmpty()) {
            println("No $type found.")
            return
        }

        println("\n====================")
        println("List of $type:")
        println("====================")
        items.forEach { item ->
            println("Title: ${item.title}")
            if (item is NoteItem) {
                println("Content: ${item.content}")
            }
            println()
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

    private fun manageList(items: List<ArchiveItem>, action: (ArchiveItem) -> Unit) {
        while (true) {
            println("\n====================")
            println("      Archives List    ")
            println("====================")
            items.forEachIndexed { index, item -> println("${index + 1}. ${item.title}") }
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

    private fun promptForTitleAndContent(action: (String, String) -> Unit): Boolean {
        val title = readInputString("Enter title (or press Enter to go back): ")
        if (title.isBlank()) {
            println("Warning: You entered an empty value.")
            return false
        }

        val content = readInputString("Enter content (or press Enter to go back): ")
        if (content.isBlank()) {
            println("Warning: You entered an empty value.")
            return false
        }

        action(title, content) // Вызов действия
        return true
    }

    private fun readInputString(prompt: String): String {
        print(prompt)
        return readlnOrNull() ?: "" // Используем readlnOrNull
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