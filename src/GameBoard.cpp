#include "GameBoard.h"

GameBoard::GameBoard(int width, int height) : boardWidth(width), boardHeight(height) {}

bool GameBoard::CheckWallCollision(const Point& p) const {
    return p.x < 0 || p.x >= boardWidth || p.y < 0 || p.y >= boardHeight;
}

int GameBoard::GetWidth() const {
    return boardWidth;
}

int GameBoard::GetHeight() const {
    return boardHeight;
}
