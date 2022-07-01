package com.github.MaierFlorian.testsmelldetection.parsing;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CodeParserTest {

    @Test
    public void getBodyOfOneLinersInputOneForLoopOnelinerOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tfor(int i=0; i<10; i++)\n" +
                "\t\tSystem.out.println(i);\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getBodyOfOneLiners(pattern, code);
        Assert.assertEquals("System.out.println(i);", result.get(0));
    }

    @Test
    public void getBodyOfOneLinersInputMultipleForLoopOnelinerOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tfor (int i=0; i<10; i++)\n" +
                "\t\tSystem.out.println(i);\n" +
                "\tint j = 0;\n" +
                "\tfor (j<10)\n" +
                "\t\tSystem.out.println(j);\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getBodyOfOneLiners(pattern, code);
        Assert.assertEquals("System.out.println(i);", result.get(0));
        Assert.assertEquals("System.out.println(j);", result.get(1));
    }

    @Test
    public void getBodyOfOneLinersInputOneWhileLoopOnelinerOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tint i = 0;\n" +
                "\twhile (i<10)\n" +
                "\t\tSystem.out.println(i++);\n" +
                "\t\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getBodyOfOneLiners(pattern, code);
        Assert.assertEquals("System.out.println(i++);", result.get(0));
    }

    @Test
    public void getBodyOfOneLinersInputOneIfOnelinerOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tint i = 0;\n" +
                "\tif (i<10)\n" +
                "\t\tSystem.out.println(i);\n" +
                "\t\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getBodyOfOneLiners(pattern, code);
        Assert.assertEquals("System.out.println(i);", result.get(0));
    }

    @Test
    public void getCodeBetweenCurvedBracketsInputOneIfOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tint i = 0;\n" +
                "\tif (i<10){\n" +
                "\t\tSystem.out.println(i++);\n" +
                "\t\tsomeMethod();\n" +
                "\t}\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getCodeBetweenCurvedBrackets(pattern, code);
        Assert.assertEquals("\n\t\tSystem.out.println(i++);\n" +
                "\t\tsomeMethod();\n\t", result.get(0));
    }

    @Test
    public void getCodeBetweenCurvedBracketsInputMultipleIfOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tint i = 0;\n" +
                "\tif (i<10){\n" +
                "\t\tSystem.out.println(i++);\n" +
                "\t\tsomeMethod();\n" +
                "\t}\n" +
                "\tif (i<5){\n" +
                "\t\tSystem.out.println(i++);\n" +
                "\t\tsomeOtherMethod();\n" +
                "\t}\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((if)|(switch))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getCodeBetweenCurvedBrackets(pattern, code);
        Assert.assertEquals("\n\t\tSystem.out.println(i++);\n" +
                "\t\tsomeMethod();\n\t", result.get(0));
        Assert.assertEquals("\n\t\tSystem.out.println(i++);\n" +
                "\t\tsomeOtherMethod();\n\t", result.get(1));
    }

    @Test
    public void getCodeBetweenCurvedBracketsInputOneForOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tfor (int i=0; i<10; i++){\n" +
                "\t\tSystem.out.println(i);\n" +
                "\t\tsomeMethod();\n" +
                "\t}\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getCodeBetweenCurvedBrackets(pattern, code);
        Assert.assertEquals("\n\t\tSystem.out.println(i);\n" +
                "\t\tsomeMethod();\n\t", result.get(0));
    }

    @Test
    public void getCodeBetweenCurvedBracketsInputForAndWhileOutputEquals(){
        String code = "@Test\n" +
                "public void exampleMethod() {\n" +
                "\tfor (int i=0; i<10; i++){\n" +
                "\t\tSystem.out.println(i);\n" +
                "\t\tsomeMethod();\n" +
                "\t}\n" +
                "\tint j = 0;\n" +
                "\twhile (j<10){\n" +
                "\t\tSystem.out.println(j);\n" +
                "\t}\n" +
                "\tsomeMethod();\n" +
                "}";
        String pattern = "(\n)(\s|\t)*((while)|(for))(\s)*(?=[(])";
        CodeParser cp = new CodeParser();
        List<String> result = cp.getCodeBetweenCurvedBrackets(pattern, code);
        Assert.assertEquals("\n\t\tSystem.out.println(i);\n" +
                "\t\tsomeMethod();\n\t", result.get(0));
        Assert.assertEquals("\n\t\tSystem.out.println(j);\n\t", result.get(1));
    }

    @Test
    public void getContentWithoutStringsBetweenDoubleQuotationMarks(){
        String test = "This is an \"example\" of \"content between\" quotation \" marks\"";
        CodeParser cp = new CodeParser();
        Assert.assertEquals("This is an  of  quotation ", cp.removeStringsInsideQuotationMarks(test));
    }

    @Test
    public void getContentWithoutStringsBetweenSingleQuotationMarks(){
        String test = "This is an 'example' of 'content between' quotation ' marks'";
        CodeParser cp = new CodeParser();
        Assert.assertEquals("This is an  of  quotation ", cp.removeStringsInsideQuotationMarks(test));
    }

}
