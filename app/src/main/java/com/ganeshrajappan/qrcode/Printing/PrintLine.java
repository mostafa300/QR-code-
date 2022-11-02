package com.ganeshrajappan.qrcode.Printing;

public class PrintLine{
    public PrintUtility.PrintLineType lineType;
    public int NumberOfElements;
    public int[] elementsRelativeWidth;
    public PrintElement[] elements;
    public PrintUtility.PrintLineScale lineScale = PrintUtility.PrintLineScale.Medium;
    public PrintUtility.PrintLineCharNum printLineCharNum = PrintUtility.PrintLineCharNum.Normal;

    public PrintLine() {
        this.lineType = PrintUtility.PrintLineType.Space;
        NumberOfElements = 0;
    }
    public PrintLine(PrintUtility.PrintLineType type) {
        this.lineType = type;
        NumberOfElements = 0;
    }
    public PrintLine(PrintUtility.PrintLineScale LineScale) {
        this.lineType = PrintUtility.PrintLineType.Line;
        this.printLineCharNum = PrintUtility.PrintLineCharNum.Normal;
        this.lineScale = LineScale;
        NumberOfElements = 0;
    }
    public PrintLine(PrintElement element1) {
        this.lineType = PrintUtility.PrintLineType.Elements;
        this.printLineCharNum = PrintUtility.PrintLineCharNum.Normal;
        NumberOfElements = 1;
        elementsRelativeWidth = new int[1];
        elements = new PrintElement[1];
        elementsRelativeWidth[0] = 100;
        elements[0] = element1;
    }
    public PrintLine(int element1RelativeWidth, PrintElement element1, PrintElement element2) {
        this.lineType = PrintUtility.PrintLineType.Elements;
        this.printLineCharNum = PrintUtility.PrintLineCharNum.Normal;
        NumberOfElements = 2;
        elementsRelativeWidth = new int[2];
        elements = new PrintElement[2];
        elementsRelativeWidth[0] = element1RelativeWidth;
        elements[0] = element1;
        elementsRelativeWidth[1] = 100 - element1RelativeWidth;
        elements[1] = element2;
    }
    public PrintLine(int element1RelativeWidth, PrintElement element1, int element2RelativeWidth, PrintElement element2, PrintElement element3) {
        this.lineType = PrintUtility.PrintLineType.Elements;
        this.printLineCharNum = PrintUtility.PrintLineCharNum.Normal;
        NumberOfElements = 3;
        elementsRelativeWidth = new int[3];
        elements = new PrintElement[3];
        elementsRelativeWidth[0] = element1RelativeWidth;
        elements[0] = element1;
        elementsRelativeWidth[1] = element2RelativeWidth;
        elements[1] = element2;
        elementsRelativeWidth[2] = 100 - element1RelativeWidth - element2RelativeWidth;
        elements[2] = element3;
    }
    public PrintLine(int element1RelativeWidth, PrintElement element1, int element2RelativeWidth, PrintElement element2, int element3RelativeWidth, PrintElement element3, PrintElement element4) {
        this.lineType = PrintUtility.PrintLineType.Elements;
        this.printLineCharNum = PrintUtility.PrintLineCharNum.Normal;
        NumberOfElements = 4;
        elementsRelativeWidth = new int[4];
        elements = new PrintElement[4];
        elementsRelativeWidth[0] = element1RelativeWidth;
        elements[0] = element1;
        elementsRelativeWidth[1] = element2RelativeWidth;
        elements[1] = element2;
        elementsRelativeWidth[2] = element3RelativeWidth;
        elements[2] = element3;
        elementsRelativeWidth[3] = 100 - element1RelativeWidth - element2RelativeWidth - element3RelativeWidth;
        elements[3] = element4;
    }

    public PrintLine(PrintUtility.PrintLineCharNum printLineCharNum,int[] elementsRelativeWidth,PrintElement[] elements) {
        this.lineType = PrintUtility.PrintLineType.Elements;
        this.printLineCharNum = printLineCharNum;
        NumberOfElements = elementsRelativeWidth.length;
        this.elements = elements;
        this.elementsRelativeWidth = elementsRelativeWidth;
    }
}