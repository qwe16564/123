#include "../include/GameBoard.h"
#include "../include/Point.h" // Required by test_assertions.h for Point specialization
#include "test_assertions.h"
#include <iostream> // Added this include

void TestWallCollision() {
    std::cout << "\n--- Testing Wall Collision ---" << std::endl;
    GameBoard board(10, 10); // 0-9 width, 0-9 height

    Assert(!board.CheckWallCollision(Point(0, 0)), "Inside: (0,0)");
    Assert(!board.CheckWallCollision(Point(9, 9)), "Inside: (9,9)");
    Assert(!board.CheckWallCollision(Point(5, 5)), "Inside: (5,5)");

    Assert(board.CheckWallCollision(Point(-1, 5)), "Outside: (-1,5) - Left");
    Assert(board.CheckWallCollision(Point(10, 5)), "Outside: (10,5) - Right");
    Assert(board.CheckWallCollision(Point(5, -1)), "Outside: (5,-1) - Top");
    Assert(board.CheckWallCollision(Point(5, 10)), "Outside: (5,10) - Bottom");
}

// Function to be called by a test runner
void RunGameBoardTests() {
    TestWallCollision();
}
