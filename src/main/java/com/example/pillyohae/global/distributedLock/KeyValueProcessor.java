package com.example.pillyohae.global.distributedLock;

/**
 * KeyValue 처리를 위한 유틸리티 클래스. 로그 또는 기타 시스템에서 Key-Value 형태로 데이터를 가공하여 사용할 수 있도록 함.
 */
public class KeyValueProcessor {

    /**
     * Key-Value를 문자열 형태로 변환하는 메서드.
     *
     * @param key   키 값
     * @param value 값
     * @return "Processing Key: key, Value: value" 형태의 문자열 반환
     */
    public static String kv(String key, String value) {
        return ("Processing Key: " + key + ", Value: " + value);
    }
}
