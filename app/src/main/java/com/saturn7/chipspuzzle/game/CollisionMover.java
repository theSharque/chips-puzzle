package com.saturn7.chipspuzzle.game;

public final class CollisionMover {
    private static final int MAX_STEPS = Board.MAX_POS * 4;

    private CollisionMover() {
    }

    public static boolean slideToward(Chip chip, int[][] map, float targetX, float targetY) {
        boolean moved = false;
        int lastVx = 0;
        int lastVy = 0;
        for (int step = 0; step < MAX_STEPS; step++) {
            float remainingX = targetX - (chip.x + chip.grabOffsetX);
            float remainingY = targetY - (chip.y + chip.grabOffsetY);
            int velocityX = axisVelocity(remainingX, chip.x, map, chip.y, true);
            int velocityY = axisVelocity(remainingY, chip.y, map, chip.x, false);

            if (velocityX != 0 && velocityY != 0) {
                if (Math.abs(remainingX) > Math.abs(remainingY)) {
                    velocityY = 0;
                } else {
                    velocityX = 0;
                }
            }
            if (velocityX != 0 && lastVx != 0 && velocityX != lastVx) {
                velocityX = 0;
            }
            if (velocityY != 0 && lastVy != 0 && velocityY != lastVy) {
                velocityY = 0;
            }
            chip.velocityX = velocityX;
            chip.velocityY = velocityY;
            if (velocityX == 0 && velocityY == 0) {
                return moved;
            }
            if (velocityX != 0) {
                chip.x = clampPos(chip.x + stepDelta(velocityX, remainingX));
                moved = true;
            }
            if (velocityY != 0) {
                chip.y = clampPos(chip.y + stepDelta(velocityY, remainingY));
                moved = true;
            }
            lastVx = velocityX;
            lastVy = velocityY;
        }
        chip.velocityX = 0;
        chip.velocityY = 0;
        return moved;
    }

    private static int axisVelocity(float remaining, float pos, int[][] map, float other, boolean horizontal) {
        if (remaining > 0 && pos < Board.MAX_POS && canStep(map, pos, other, 1, horizontal)) {
            return 1;
        }
        if (remaining < 0 && pos > 0 && canStep(map, pos, other, -1, horizontal)) {
            return -1;
        }
        return 0;
    }

    private static boolean canStep(int[][] map, float pos, float other, int direction, boolean horizontal) {
        if (horizontal) {
            float nextX = direction > 0 ? pos + Board.CELL : pos - 1;
            return empty(map, other, nextX) && empty(map, other + Board.CELL - 1, nextX);
        }
        float nextY = direction > 0 ? pos + Board.CELL : pos - 1;
        return empty(map, nextY, other) && empty(map, nextY, other + Board.CELL - 1);
    }

    private static float stepDelta(int velocity, float remaining) {
        if (velocity > 0) {
            return Math.min(1f, remaining);
        }
        return Math.max(-1f, remaining);
    }

    private static float clampPos(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > Board.MAX_POS) {
            return Board.MAX_POS;
        }
        return value;
    }

    static boolean empty(int[][] map, float pixelY, float pixelX) {
        if (pixelX < 0 || pixelY < 0) {
            return false;
        }
        int row = (int) (pixelY / Board.CELL);
        int col = (int) (pixelX / Board.CELL);
        if (row < 0 || col < 0 || row >= Board.SIZE || col >= Board.SIZE) {
            return false;
        }
        return map[row][col] == Tile.EMPTY;
    }
}
