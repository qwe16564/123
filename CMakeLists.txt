cmake_minimum_required(VERSION 3.10)
project(SnakeGame CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# Add executable for the main game
add_executable(SnakeGame src/main.cpp src/Point.cpp src/Snake.cpp src/Food.cpp src/GameBoard.cpp)

# Include directory
target_include_directories(SnakeGame PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)

# Link ncurses library for Linux/macOS
if (UNIX AND NOT APPLE)
    find_package(Curses REQUIRED)
    if (CURSES_FOUND)
        target_link_libraries(SnakeGame PRIVATE ${CURSES_LIBRARIES})
    endif()
endif()

# Example for adding tests (assuming Google Test)
# enable_testing()
# find_package(GTest REQUIRED)
# add_executable(RunTests tests/test_snake.cpp) # Add more test files as needed
# target_link_libraries(RunTests PRIVATE GTest::GTest GTest::Main)
# include(GoogleTest)
# gtest_discover_tests(RunTests)

# --- Tests ---
# Enable testing (if not already enabled)
if(NOT DEFINED CMAKE_TESTING_ENABLED)
  enable_testing()
endif()

# Create a list of test source files
set(TEST_SOURCES
    tests/main_test.cpp
    tests/test_snake.cpp
    tests/test_gameboard.cpp
    tests/test_food.cpp
    # Add Point.cpp, Snake.cpp, Food.cpp, GameBoard.cpp 
    # as they are needed by the tests
    src/Point.cpp
    src/Snake.cpp
    src/Food.cpp
    src/GameBoard.cpp
)

# Add an executable for running tests
add_executable(RunTests ${TEST_SOURCES})

# Include directory for tests (to find Point.h, Snake.h etc.)
target_include_directories(RunTests PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)

# If using ncurses in main lib and it's a header-only dependency for tests,
# it might not be needed here, but if tests directly use ncurses features:
# if(UNIX AND NOT APPLE AND CURSES_FOUND)
#   target_link_libraries(RunTests PRIVATE ${CURSES_LIBRARIES})
# endif()

# Add test to CTest
# The first argument is the CTest test name, the second is the executable
add_test(NAME GameLogicTests COMMAND RunTests)