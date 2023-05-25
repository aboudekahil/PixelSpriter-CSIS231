package com.abdelkader.backend.modals;

public record ImageDTO (int id,
                        String title,
                        String description,
                        String imagePath,
                        int gridWidth,
                        int gridHeight) {
}
