package com.example.main.global.distributedLock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


/**
 * Spring Expression Language(SpEL)를 사용하여 런타임 시 동적으로 값 추출하는 유틸리티 클래스.
 */
public class CustomSpringELParser {

    private CustomSpringELParser() {
    }

    /**
     * 메서드 파라미터에서 SpEL 표현식을 이용해 특정 값을 추출하는 메서드.
     *
     * @param parameterNames 메서드 파라미터 이름 배열
     * @param args           메서드 실행 시 전달된 인자 값 배열
     * @param key            SpEL 표현식 (예: "#userId")
     * @return 추출된 값
     */
    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]); // 파라미터 값을 SpEL 컨텍스트에 저장
        }

        return parser.parseExpression(key).getValue(context, Object.class); // SpEL 표현식 평가 및 값 반환
    }
}
