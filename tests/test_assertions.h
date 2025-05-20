#ifndef TEST_ASSERTIONS_H
#define TEST_ASSERTIONS_H

#include <iostream>
#include <string>
#include <vector> // For vector comparison

// Basic assertion function
inline void Assert(bool condition, const std::string& message) {
    if (!condition) {
        std::cerr << "Assertion Failed: " << message << std::endl;
        // In a real test suite, you might throw an exception or exit
    } else {
        std::cout << "Assertion Passed: " << message << std::endl;
    }
}

// Overload for comparing Points (assuming Point.h is included where this is used)
// and Point has operator==
template <typename T>
void AssertEqual(const T& expected, const T& actual, const std::string& message) {
    if (!(expected == actual)) {
        std::cerr << "Assertion Failed: " << message 
                  << " | Expected: (some representation) " 
                  << " | Actual: (some representation)" << std::endl;
        // Note: Need a way to print T or specialize for Point
    } else {
        std::cout << "Assertion Passed: " << message << std::endl;
    }
}

// Specialization for Point to print coordinates
inline void AssertEqual(const Point& expected, const Point& actual, const std::string& message) {
    if (!(expected == actual)) {
        std::cerr << "Assertion Failed: " << message 
                  << " | Expected: (" << expected.x << "," << expected.y << ")"
                  << " | Actual: (" << actual.x << "," << actual.y << ")" << std::endl;
    } else {
        std::cout << "Assertion Passed: " << message << std::endl;
    }
}


// Overload for comparing std::vector<Point>
inline void AssertEqual(const std::vector<Point>& expected, const std::vector<Point>& actual, const std::string& message) {
    bool equal = expected.size() == actual.size();
    if (equal) {
        for (size_t i = 0; i < expected.size(); ++i) {
            if (!(expected[i] == actual[i])) {
                equal = false;
                break;
            }
        }
    }

    if (!equal) {
        std::cerr << "Assertion Failed: " << message << " | Vector contents differ." << std::endl;
        // Optionally print vector contents for debugging
    } else {
        std::cout << "Assertion Passed: " << message << std::endl;
    }
}

#endif // TEST_ASSERTIONS_H
