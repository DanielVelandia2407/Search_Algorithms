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
- **Hash Expansion**: Technique to resize a hash table when it becomes too full.
- **Partial Expansion**: Method to partially resize a hash table to optimize space usage.

### External Search Algorithms
- **External Sequential Search**: Implementation of sequential search for external storage.
- **External Binary Search**: Implementation of binary search for external storage.
- **External Hash Functions**: External versions of all hash functions (Modulo, Square, Truncation, Folding).

### Index-Based Search
- **Primary Index**: A search structure that uses a primary key to locate records.
- **Multilevel Primary Index**: An extension of primary index that uses multiple levels for more efficient searching.
- **Improved Multilevel Index**: An optimized version of multilevel primary index.
- **Secondary Index**: A search structure that uses non-primary keys to locate records.

### Search Trees
- **Digital Tree**: A tree data structure where each node's position is determined by the digits/bits of the key.
- **Residue Tree**: A tree structure that organizes data based on the remainder when divided by a specific value.
- **Multiple Residue Tree**: An extension of the residue tree that uses multiple divisors.
- **Huffman Tree**: A tree used for data compression, where frequently occurring characters have shorter codes.

## Project Structure

The project follows the Model-View-Controller (MVC) architecture pattern:

- **Model**: Contains the data structures and algorithms implementation.
  - `DigitalTreeModel.java`: Implementation of digital tree data structure.
  - `HuffmanTreeModel.java`: Implementation of Huffman tree for data compression.
  - `MultipleResidueTreeModel.java`: Implementation of multiple residue tree.
  - `ResidueHashModel.java`: Implementation of residue hash function.
  - `IndicePrimarioModelo.java`: Implementation of primary index.
  - `IndiceSecundarioModelo.java`: Implementation of secondary index.
  - `HashExpansionModel.java`: Implementation of hash table expansion.
  - `PartialExpansionModel.java`: Implementation of partial hash table expansion.
  - `ResiduoSimpleModel.java`: Implementation of simple residue tree.
  - `ResiduoMultipleModel.java`: Implementation of multiple residue tree.
  - `ArbolDigital.java`, `ArbolResiduoSimple.java`, `ArbolResiduoMultiple.java`: Tree implementations.

- **View**: Contains the GUI components.
  - `MainView.java`: The main application window.
  - `AlgorithmMenuView.java`: Menu for selecting internal search algorithms.
  - `HashAlgorithmView.java`: Menu for selecting hash functions.
  - `TreeView.java`: Menu for selecting tree-based algorithms.
  - `ExternalSearchMenuView.java`: Menu for selecting external search algorithms.
  - `IndicesMenuView.java`: Menu for selecting index-based search algorithms.
  - `PrimaryIndexView.java`: UI for primary index search.
  - `MultilevelPrimaryIndexView.java`: UI for multilevel primary index search.
  - `ImprovedMultilevelIndexView.java`: UI for improved multilevel index search.
  - `SecondaryIndexView.java`: UI for secondary index search.
  - `ExternalSequentialSearchView.java`, `ExternalBinarySearchView.java`: UIs for external search algorithms.
  - `ExternalModSearchView.java`, `ExternalSquaredSearchView.java`, `ExternalTruncSearchView.java`, `ExternalFoldingSearchView.java`: UIs for external hash functions.
  - Various other algorithm-specific views.

- **Controller**: Contains the logic that connects the models and views.
  - `MainController.java`: Manages navigation between main views.
  - `IndicesMenuController.java`: Controls the indices menu.
  - `PrimaryIndexController.java`: Controls the primary index search.
  - `MultilevelPrimaryIndexController.java`: Controls the multilevel primary index search.
  - `ImprovedMultilevelIndexController.java`: Controls the improved multilevel index search.
  - `SecondaryIndexController.java`: Controls the secondary index search.
  - `ExternalSearchMenuController.java`: Controls the external search menu.
  - `ExternalSequentialSearchController.java`, `ExternalBinarySearchController.java`: Control external search algorithms.
  - `ExternalModSearchController.java`, `ExternalSquaredSearchController.java`, `ExternalTruncSearchController.java`, `ExternalFoldingSearchController.java`: Control external hash functions.
  - Various other algorithm-specific controllers.

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
2. From the main menu, select one of the main categories:
   - **Búsqueda Interna** (Internal Search): Access sequential search, binary search, and hash functions.
   - **Búsqueda Externa** (External Search): Access external versions of search algorithms and hash functions.
   - **Índices** (Indices): Access primary index, multilevel primary index, improved multilevel index, and secondary index.
   - **Árboles de Búsqueda** (Search Trees): Access digital tree, residue tree, multiple residue tree, and Huffman tree.
3. Select a specific algorithm from the submenu.
4. Follow the on-screen instructions to interact with the selected algorithm.

### Working with Internal Search Algorithms
- For sequential and binary search, you can input data manually or load from a file.
- For hash functions, you can specify the hash table size and collision resolution method.
- Hash expansion and partial expansion allow you to experiment with dynamic hash table resizing.

### Working with External Search Algorithms
- These algorithms simulate working with data that doesn't fit entirely in memory.
- You can specify block size, file size, and other parameters to observe how the algorithms perform with external storage.

### Working with Indices
- Primary index allows searching using a primary key.
- Multilevel primary index and improved multilevel index provide more efficient searching for large datasets.
- Secondary index allows searching using non-primary keys.

### Working with Search Trees
- Digital tree visualizes how data is organized based on the digits/bits of the key.
- Residue tree and multiple residue tree show how data can be organized based on remainders.
- Huffman tree demonstrates data compression techniques.

## Author

Grupo 1

## License

This project is for educational purposes. All rights reserved.
