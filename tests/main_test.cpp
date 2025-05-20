#include <iostream> // Added this include

// Forward declarations of test functions
void RunSnakeTests();
void RunGameBoardTests();
void RunFoodTests();

int main() {
    RunSnakeTests();
    RunGameBoardTests();
    RunFoodTests();
    
    std::cout << "\nAll tests completed." << std::endl;
    // In a more advanced setup, you'd check for a count of failed assertions.
    return 0; 
}
