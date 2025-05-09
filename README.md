# Search Algorithms

A comprehensive Java application that demonstrates various search algorithms and data structures through an interactive GUI. This educational tool allows users to explore and visualize different search techniques including sequential search, binary search, hash-based methods, and tree-based structures.

## Features

This application implements the following search algorithms and data structures:

### Internal Search Algorithms
- **Sequential Search**: A simple linear search algorithm that checks each element in a list until it finds a match.
- **Binary Search**: An efficient search algorithm that works on sorted arrays by repeatedly dividing the search interval in half.

### Hash Functions
- **Modulo Function**: Uses the remainder of division to map keys to hash table indices.
- **Square Function**: Squares the key and extracts the middle digits for hash table indexing.
- **Truncation Function**: Removes parts of the key and uses the remaining portion for indexing.
- **Folding Function**: Divides the key into parts, combines them, and uses the result for indexing.

### Search Trees
- **Digital Tree**: A tree data structure where each node's position is determined by the digits/bits of the key.
- **Residue Tree**: A tree structure that organizes data based on the remainder when divided by a specific value.
- **Multiple Residue Tree**: An extension of the residue tree that uses multiple divisors.
- **Huffman Tree**: A tree used for data compression, where frequently occurring characters have shorter codes.

## Project Structure

The project follows the Model-View-Controller (MVC) architecture pattern:

- **Model**: Contains the data structures and algorithms implementation.
  - `DigitalTreeModel.java`
  - `HuffmanTreeModel.java`
  - `MultipleResidueTreeModel.java`
  - `ResidueHashModel.java`

- **View**: Contains the GUI components.
  - `MainView.java`: The main application window.
  - `AlgorithmMenuView.java`: Menu for selecting internal search algorithms.
  - `HashAlgorithmView.java`: Menu for selecting hash functions.
  - `TreeView.java`: Menu for selecting tree-based algorithms.
  - Various algorithm-specific views (e.g., `BinarySearchView.java`, `SequentialSearchView.java`, etc.)

- **Controller**: Contains the logic that connects the models and views.
  - `MainController.java`: Manages navigation between main views.
  - Various algorithm-specific controllers (e.g., `BinarySearchController.java`, `SequentialSearchController.java`, etc.)

- **Utilities**: Contains data files used for testing and demonstrating the algorithms.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Java Swing (included in JDK)

## How to Run

1. Clone or download this repository.
2. Open the project in your preferred Java IDE (Eclipse, IntelliJ IDEA, etc.).
3. Build the project.
4. Run the `Main.java` file.

Alternatively, you can run the compiled JAR file (if available) using:

```
java -jar search_algorithms.jar
```

## Usage

1. Launch the application.
2. From the main menu, select one of the three main categories:
   - **Búsqueda Interna** (Internal Search)
   - **Búsqueda Externa** (External Search)
   - **Árboles de Búsqueda** (Search Trees)
3. Select a specific algorithm from the submenu.
4. Follow the on-screen instructions to interact with the selected algorithm.

## Author

Daniel Velandia (Student ID: 20191020140)

## License

This project is for educational purposes. All rights reserved.