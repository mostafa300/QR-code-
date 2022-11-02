package com.ganeshrajappan.qrcode.Printing;

public class PrintElement{
    public PrintUtility.TextAllignment alignment;
    public PrintUtility.PrintFontSize fontSize;
    public String element;
    public boolean forceEnglish;

    public PrintElement(boolean ForceEnglish, PrintUtility.TextAllignment alignment, PrintUtility.PrintFontSize fontSize, String element) {
        this.alignment = alignment;
        this.fontSize = fontSize;
        this.element = element;
        this.forceEnglish = ForceEnglish;
    }
    public PrintElement(boolean ForceEnglish, String element) {
        this.alignment = PrintUtility.TextAllignment.Relative;
        this.fontSize = PrintUtility.PrintFontSize.Small;
        this.element = element;
        this.forceEnglish = ForceEnglish;
    }

}