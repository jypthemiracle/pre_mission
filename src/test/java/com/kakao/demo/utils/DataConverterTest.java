package com.kakao.demo.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DataConverterTest {
    @Test
    @DisplayName("단위 없애는 테스트")
    void deleteBenchMark() {
        List<String> inputs = Arrays.asList("주택도시기금1)(억원)","국민은행(억원)","우리은행(억원)","신한은행(억원)",
                "한국시티은행(억원)","하나은행(억원)","농협은행/수협은행(억원)","외환은행(억원)","기타은행(억원)");

        List<String> expectedInputs = Arrays.asList("주택도시기금","국민은행","우리은행","신한은행",
                "한국시티은행","하나은행","농협은행/수협은행","외환은행","기타은행");

        assertThat(DataConverter.deleteBenchMark(inputs)).isEqualTo(expectedInputs);
    }

    @Test
    @DisplayName("빈 값 없애는 테스트")
    void deleteEmptyValue() {
        List<String> inputs = Arrays.asList("년도","월","주택도시기금1)(억원)","국민은행(억원)","우리은행(억원)","신한은행(억원)",
                "한국시티은행(억원)","하나은행(억원)","농협은행/수협은행(억원)","외환은행(억원)","기타은행(억원)","","");

        List<String> expectedInputs = Arrays.asList("년도","월","주택도시기금1)(억원)","국민은행(억원)","우리은행(억원)","신한은행(억원)",
                "한국시티은행(억원)","하나은행(억원)","농협은행/수협은행(억원)","외환은행(억원)","기타은행(억원)");

        List<String> result = DataConverter.deleteEmptyValue(inputs);

        assertThat(result.size()).isEqualTo(expectedInputs.size());
        assertThat(result).isEqualTo(expectedInputs);
    }

    @Test
    @DisplayName("기관에 해당하는 값만 추출하는 테스트")
    void extractInstitutionNames() {
        List<String> inputs = Arrays.asList("년도","월","주택도시기금1)(억원)","국민은행(억원)","우리은행(억원)","신한은행(억원)",
                "한국시티은행(억원)","하나은행(억원)","농협은행/수협은행(억원)","외환은행(억원)","기타은행(억원)");

        List<String> expectedInputs = Arrays.asList("주택도시기금1)(억원)","국민은행(억원)","우리은행(억원)","신한은행(억원)",
                "한국시티은행(억원)","하나은행(억원)","농협은행/수협은행(억원)","외환은행(억원)","기타은행(억원)");

        List<String> results = DataConverter.extractInstitutionNames(inputs);

        assertThat(results.size()).isEqualTo(expectedInputs.size());
        assertThat(results).isEqualTo(expectedInputs);
    }
}