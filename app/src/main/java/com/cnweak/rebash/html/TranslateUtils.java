package com.cnweak.rebash.html;
import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.*;
import android.util.*;
import com.memetix.mst.language.*;
import com.memetix.mst.translate.*;
import java.util.*;
public class TranslateUtils{
	private TextToSpeech tts;
	private String BingTranslateID="translatebingcnweak";
	private String BingTranslateKey="Iz04pI1gsyeFgg/KI2oZTcIvCUIhoPTL9vy3T1BKgqg=";
	//翻译
    public String BingTranslate(String text) 
	throws Exception {
		Translate.setClientId(BingTranslateID);
		Translate.setClientSecret(BingTranslateKey);
		String translatedText = Translate.execute(text, Language.CHINESE_SIMPLIFIED);
		return translatedText;
	}
	//日志
	@TargetApi(Build.VERSION_CODES.DONUT)
    public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.CHINESE);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "不支持当前语言或输入错误");
			}
		} else {
			Log.e("TTS", "初始化失败");
		}
	}
    //发音
    @TargetApi(Build.VERSION_CODES.DONUT)
    private void speakOut(String text) {
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

}
