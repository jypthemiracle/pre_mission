package com.kakao.demo.utils;

import com.kakao.demo.service.dto.Measures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataConverter {
    private static final int INSTITUTION_START_INDEX = 2;
    private static final String THOUSAND_SEPARATOR = ",";
    private static final String BLANK = "";

    // TODO: 13/12/2019 replace 2번하지 않고 파싱하는 작업 필요
    public static List<String> deleteBenchMark(List<String> institutions) {
        return institutions.stream()
                .map(name -> name.replaceAll("\\(억원\\)", BLANK))
                .map(name -> name.replaceAll("1\\)", BLANK))
                .collect(Collectors.toList());
    }

    private static List<String> deleteEmptyValue(List<String> values) {
        return values.stream()
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    public static List<String> extractInstitutionNames(List<String> institutionNames) {
        List<String> filteredInstitutions = institutionNames.subList(INSTITUTION_START_INDEX, institutionNames.size());
        List<String> realInstitutions = deleteEmptyValue(filteredInstitutions);

        return deleteBenchMark(realInstitutions);
    }

    public static List<Integer> convertStringToInt(List<String> financeStatusByDate) {
        return financeStatusByDate.stream()
                .map(result -> result.replaceAll(THOUSAND_SEPARATOR, BLANK))
                .mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
    }

    public static List<Measures> extractMeasures(List<String[]> inputData) {
        List<Measures> measures = new ArrayList<>();

        for (String[] input : inputData) {
            Measures measureByDate = Measures.valueOf(input);
            measures.add(measureByDate);
        }
        return measures;
    }
}
