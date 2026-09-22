package com.example.myapplicationpruebacaludefinal;


public class Question {

    // Atributos
    private final int videoPart1ResId;

    private final int videoPart2ResId;

    private final String[] options;

    private final int correctOptionIndex;

    // Constructor
    public Question(int videoPart1ResId, int videoPart2ResId, String[] options, int correctOptionIndex) {

        //Validacion de seguridad
        if (options == null || options.length != 4) {
            throw new IllegalArgumentException("Cada jugada debe tener exactamente 4 opciones");
        }

        this.videoPart1ResId = videoPart1ResId;
        this.videoPart2ResId = videoPart2ResId;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }


    //Getters
    public int getVideoPart1ResId() {
        return videoPart1ResId;
    }
    public int getVideoPart2ResId() {
        return videoPart2ResId;
    }
    public String[] getOptions() {
        return options;
    }
    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}
