package com.cnweak.rebash.translate;
import android.content.Context;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
/**
 * 翻译工具
 * 用于处理安卓的String.xml文件；
 * 实现功能：
 * 1，给定一个或多个String.xml文件，翻译成指定的多种或单语言对应文件
 * 2，翻译时，先从预存的数据中查询结果，没有时再调用API翻译
 * 3，翻译完成的文件存放与工作目录下；也可存入云端；FTP等
 * 4，翻译完成的文件依照apk的资源路径对应存放，及res/value_语言/string_语言.xml
 * */
public class TranslteResString {
    private static final Language SOURCE_LANGUAGE = Language.ENGLISH;
    private static  String API_ID = "";//微软翻译APIID
    private static  String API_KEY = "";//微软翻译APIKEY
    private static  String File_PATH = "";//文件路径
    private static  String FILENAME = "";//文件名称
    private static  String VAL_PATH = "";//配置路径
    private static String ENDLINE = "";//特定结尾标识
    private static  String WORKING_DIR = "";//工作路径
    private static  String GET_STRING = "<string\\s+name=\\\"([a-zA-Z0-9_]*)\\\">([^<]*)</string>";//使用正则提取参数
    private static final Language[] TARGET_LANGUAGES ={Language.ENGLISH,
                Language.JAPANESE,Language.CHINESE_TRADITIONAL};
    private static final Pattern STRINGS_PATTERN = Pattern.compile(GET_STRING,
                Pattern.CASE_INSENSITIVE|Pattern.DOTALL|Pattern.MULTILINE);//正则表达式
    private static final String NEXT_LINE = "\r\n";//下一行换行符
    private static final long WAIT_TIME = 5 * 60 * 1000;//文件路径
    private static final int WAIT_AFTER_COUNT = 99;//文件路径
    private static int currentCount = 0;//文件路径
    /**
     * 初始化基本配置
     *
     * */
    public TranslteResString(Context contact){
        Configuration config = new Configuration(contact);
        this.API_ID = config.getTranslateID();
        this.API_KEY = config.getTranslateKey();
        this.File_PATH = config.getTranslateFilePatch();
        this.FILENAME = config.getTranslatedFileName();
        this.ENDLINE = config.getTranslatedToFileEndLine();
        this.WORKING_DIR = config.getTranslateWorkDir();
        this.VAL_PATH = config.getTranslateValuePath();
    }


    /**
     * 执行处理Init开始
     *
     * */
    public static void main(String[] args) throws Exception {
        Translate.setClientId(API_ID);
        Translate.setClientId(API_KEY);
        String file = readFile(WORKING_DIR + File_PATH + VAL_PATH + FILENAME);
        Matcher matcher = STRINGS_PATTERN.matcher(file);//使用正则提取参数
        List<StringKeyValue> strings = retrieveStrings(matcher);//存放到strings
        Map<Language, List<StringKeyValue>> translated = translateStrings(strings);//存为参照数据
        writeTranslations(WORKING_DIR, translated);//创建需要被翻译的文件，以供写入内容
    }




    /**创建需要被翻译的文件，以供写入内容*/
    private static void writeTranslations(String workingDirectory, Map<Language, List<StringKeyValue>> translated) throws IOException {
        Set<Language> langs = translated.keySet();
        for (Language language:langs) {
            List<StringKeyValue> stringsList = translated.get(language);
            File languageFile = getLanguageFile(workingDirectory, language);
            Writer fileWriter = new FileWriter(languageFile);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writeHeader(writer);
            writeStrings(writer, stringsList);
            writeFooter(writer);
            writer.close();
        }
    }








    /**
     * 格式化已翻译的内容
     *
     * */
    private static void writeStrings(BufferedWriter writer, List<StringKeyValue> stringsList) throws IOException {
        for (StringKeyValue string : stringsList) {
            writer.append("\t<string name=\"" + string.getKey() + "\">" + string.getValue() + "</string>" + NEXT_LINE);
        }
    }





    /**
     * 写入内容时的尾巴标志
     *
     * */
    private static void writeFooter(BufferedWriter writer) throws IOException {
        writer.append("</resources>");
    }





    /**
     * 写入内容时的头部标志
     *
     * */
    private static void writeHeader(BufferedWriter writer) throws IOException {
        writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + NEXT_LINE);
        writer.append("<!-- This file is generated with android bing translation tool -->" + NEXT_LINE);
        writer.append("<resources>" + NEXT_LINE);
    }








    /**
     * 获取文件的源语言
     *
     * */
    private static File getLanguageFile(String workingDirectory, Language language) throws IOException {
        String path = createDir(workingDirectory, language);
        File file = new File(path + "/" + FILENAME);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }










    /**
     * 创建所翻译的语言文件夹
     *
     * */
    private static String createDir(String baseDir, Language language) {
        String path = baseDir + File_PATH + "_" + language.toString(); //$NON-NLS-1$
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return path;
    }









    /**
     * 处理已经翻译的内容
     *
     * */
    private static Map<Language, List<StringKeyValue>> translateStrings(List<StringKeyValue> strings) throws Exception {
        Map<Language, List<StringKeyValue>> translated = new HashMap<Language, List<StringKeyValue>>();

        List<String> strignsToTranslate = new LinkedList<String>();
        for (StringKeyValue droidString : strings) {
            strignsToTranslate.add(droidString.getValue());
        }

        for (Language language : TARGET_LANGUAGES) {
            waitOnDemand();
            String[] translatedStrings = Translate.execute(strignsToTranslate.toArray(new String[]{}), SOURCE_LANGUAGE, language);
            int index = 0;
            for (StringKeyValue droidString : strings) {
                String translatedString = translatedStrings[index];
                Translate.execute(droidString.getValue(), SOURCE_LANGUAGE, language);
                StringKeyValue copy1 = droidString.copy();
                copy1.setValue(translatedString);
                StringKeyValue copy = copy1;
                List<StringKeyValue> translatedList = getListForLanguages(translated, language);
                translatedList.add(copy);
                index ++;
            }
        }
        return translated;
    }








    /**
     * 线程休眠，等待唤醒事件
     *
     * */
    private static void waitOnDemand() {
        currentCount++;
        if (currentCount % WAIT_AFTER_COUNT == 0) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }








    /**
     * 获取待翻译的语言清单
     *
     * */
    private static List<StringKeyValue> getListForLanguages(Map<Language, List<StringKeyValue>> translated, Language language) {
        List<StringKeyValue> translatedList = translated.get(language);
        if (translatedList == null) {
            translatedList = new LinkedList<StringKeyValue>();
            translated.put(language, translatedList);
        }
        return translatedList;
    }








    /**
     * 以StringKeyValue类型数据化解析后的文件参数，作为参照数据，以供后续多语言调用
     *
     * */
    private static List<StringKeyValue> retrieveStrings(Matcher matcher) {
        List<StringKeyValue> strings = new LinkedList<TranslteResString.StringKeyValue>();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            StringKeyValue droidString = new StringKeyValue(key, value);
            strings.add(droidString);
        }
        return strings;
    }










    /**
     * 读取待翻译文件
     *
     * */
    private static String readFile(String string) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(string));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            sb.append(line + ENDLINE);
            line = reader.readLine();
        }
        return sb.toString();
    }








    /**
     * 数据调用
     *
     * */
    public static class StringKeyValue {
        private String key;
        private String value;

        public StringKeyValue(String key, String value) {
            super();
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public StringKeyValue copy() {
            StringKeyValue droidString = new StringKeyValue(key, value);
            return droidString;
        }
    }


}
