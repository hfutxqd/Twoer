package xyz.imxqd.twoer.utils;

/**
 * Created by imxqd on 17-4-3.
 */
public class MorseText {

    private static char[] characters = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1',
            '2','3','4','5','6','7','8','9','0'};

    private static String[] morseCode = {".-","-...","-.-.",
            "-..",".","..-.","--.","....","..",".---",
            "-.-",".-..","--","-.","---",".--.","--.-",
            ".-.","...","-","..-","...-",".--","-..-",
            "-.--","--..", ".----","..---","...--","....-",".....",
            "-....","--...","---..","----.","-----"};

    /**
     * Convert single character of tex to morse code
     * @param character single character of text
     * @return morse code
     */
    private static String getCode(char character){
        if (character == ' ')
            return "/";
        for(int i = 0; i < characters.length; i++)
            if(characters[i] == character)
                return morseCode[i];
        return  "#";
    }

    /**
     * Convert single morse code to character
     * @param code morse code
     * @return character
     */
    private static char getChar(String code){
        if(code.equals("/"))
            return ' ';
        for(int i = 0; i < morseCode.length; i++)
            if(code.equals(morseCode[i]))
                return characters[i];
        return '#';
    }

    /**
     * Convert usual text to Morse code.
     * @param expression the text to be translated
     * @return <code>String</code> Morse code
     */
    public static String textToMorse(String expression){
        StringBuilder result = new StringBuilder();
        char[] charsToTranslate = expression.toUpperCase().toCharArray();
        for(Character character :charsToTranslate){
            String translatedCode = getCode(character);
            result.append(translatedCode).append(" ");
        }
        return result.toString();
    }

    /**
     * Convert Morse code to usual text
     * @param expression the Morse code to be translated
     * @return <code>String</code> text
     */
    public static String morseToText(String expression){
        StringBuilder result = new StringBuilder();
        String[] tokens = expression.split(" ");
        for(String token: tokens)
            result.append(getChar(token));
        return result.toString();
    }


}