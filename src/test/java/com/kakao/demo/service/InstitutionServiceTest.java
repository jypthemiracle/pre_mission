package com.kakao.demo.service;

import com.kakao.demo.domain.Institution;
import com.kakao.demo.domain.InstitutionRepository;
import com.kakao.demo.service.dto.InstitutionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class InstitutionServiceTest {

    @InjectMocks
    private InstitutionService institutionService;

    @Mock
    private InstitutionRepository institutionRepository;

    @Test
    @DisplayName("입력정보 중 기관들을 찾아 저장하는 기능 테스트")
    void saveInstitutions() {
        List<String> inputs = Arrays.asList("주택도시기금", "국민은행", "우리은행", "신한은행",
                "한국시티은행", "하나은행", "농협은행/수협은행", "외환은행", "기타은행");
        List<InstitutionDto> expectedResults = Arrays.asList(
                new InstitutionDto("주택도시기금"),
                new InstitutionDto("국민은행"),
                new InstitutionDto("우리은행"),
                new InstitutionDto("신한은행"),
                new InstitutionDto("한국시티은행"),
                new InstitutionDto("하나은행"),
                new InstitutionDto("농협은행/수협은행"),
                new InstitutionDto("외환은행"),
                new InstitutionDto("기타은행"));

        List<InstitutionDto> institutionDtos = institutionService.saveInstitutions(inputs);

        assertThat(institutionDtos.size()).isEqualTo(expectedResults.size());
        for (int i = 0; i < institutionDtos.size(); i++) {
            assertThat(institutionDtos.get(i)).isEqualTo(expectedResults.get(i));
        }
    }

    @Test
    @DisplayName("금융기관 목록을 조회하는 기능 테스")
    void findInstitutions() {
        List<Institution> expectedResults = Arrays.asList(
                Institution.of("주택도시기금"),
                Institution.of("국민은행"),
                Institution.of("우리은행"),
                Institution.of("신한은행"),
                Institution.of("한국시티은행"),
                Institution.of("하나은행"),
                Institution.of("농협은행/수협은행"),
                Institution.of("외환은행"),
                Institution.of("기타은행"));
        given(institutionRepository.findAll()).willReturn(expectedResults);

        List<InstitutionDto> institutions = institutionService.findInstitutions();

        assertThat(institutions.size()).isEqualTo(expectedResults.size());
        for (int i = 0; i < institutions.size(); i++) {
            assertThat(institutions.get(i).getName()).isEqualTo(expectedResults.get(i).getName());
        }
    }
}