package com.kakao.demo.service;

import com.kakao.demo.domain.FinanceAmount;
import com.kakao.demo.domain.FinanceAmountRepository;
import com.kakao.demo.domain.FinanceDate;
import com.kakao.demo.domain.Institution;
import com.kakao.demo.service.dto.*;
import com.kakao.demo.utils.DataConverter;
import com.kakao.demo.utils.DataLoader;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceAmountService {
    private static final int COLUMN_INDEX = 0;
    private static final String YEAR_UNIT = " 년";

    private final InstitutionService institutionService;
    private final FinanceAmountRepository financeAmountRepository;

    public FinanceAmountService(InstitutionService institutionService, FinanceAmountRepository financeAmountRepository) {
        this.institutionService = institutionService;
        this.financeAmountRepository = financeAmountRepository;
    }

    public void loadCsvFile() {
        List<String[]> inputData = DataLoader.loadCsvFile();

        //기관 저장
        List<String> firstRows = Arrays.stream(inputData.get(COLUMN_INDEX)).collect(Collectors.toList());
        List<String> institutionNames = DataConverter.extractInstitutionNames(firstRows);
        List<InstitutionDto> institutionDtos = institutionService.saveInstitutions(institutionNames);

        //측정값 저장
        List<Row> measures = DataConverter.extractRows(inputData.subList(1, inputData.size()));
        save(measures, institutionDtos);
    }

    private void save(List<Row> rowByDate, List<InstitutionDto> institutionDtos) {
        for (Row row : rowByDate) {
            saveByDate(row, institutionDtos);
        }
    }

    private void saveByDate(Row financeStatusByDate, List<InstitutionDto> institutionDtos) {
        FinanceDate financeDate = FinanceDate.of(financeStatusByDate.getYear(), financeStatusByDate.getMonth());

        for (int i = 0; i < institutionDtos.size(); i++) {
            String institutionName = institutionDtos.get(i).getName();
            Institution institution = institutionService.findByName(institutionName);

            int money = financeStatusByDate.getMeasure(i);
            FinanceAmount financeAmount = FinanceAmount.of(money, financeDate, institution);
            financeAmountRepository.save(financeAmount);
        }
    }

    public List<AmountsByYear> findTotalAmountOfInstitutionsByYear() {
        List<AmountsByYear> amounts = new ArrayList<>();
        List<DetailAmountsOfInstitutionByYear> allDetailAmountsOfInstitution = findDetailAmountsByInstitutionAndYear();

        Set<Integer> years = extractAllYears(allDetailAmountsOfInstitution);
        for (int year : years) {
            List<DetailAmountsOfInstitutionByYear> extractedDetailAmountsByYear = extractDetailAmountsByYear(allDetailAmountsOfInstitution, year);

            Map<String, Long> detailAmount = convertToMap(extractedDetailAmountsByYear);
            long totalAmount = findTotalAmountByYear(detailAmount);
            amounts.add(new AmountsByYear(year + YEAR_UNIT, totalAmount, detailAmount));
        }

        return amounts;
    }

    private List<DetailAmountsOfInstitutionByYear> findDetailAmountsByInstitutionAndYear() {
        return financeAmountRepository.findTotalAmountGroupByInstitutionAndYear();
    }

    private List<DetailAmountsOfInstitutionByYear> extractDetailAmountsByYear(List<DetailAmountsOfInstitutionByYear> allDetailPricesOfInstitution, int year) {
        return allDetailPricesOfInstitution.stream()
                .filter(d -> d.getYear() == year)
                .collect(Collectors.toList());
    }

    private Set<Integer> extractAllYears(List<DetailAmountsOfInstitutionByYear> allDetailAmountsOfInstitution) {
        return allDetailAmountsOfInstitution.stream()
                .map(DetailAmountsOfInstitutionByYear::getYear)
                .collect(Collectors.toSet());
    }

    private long findTotalAmountByYear(Map<String, Long> detailAmount) {
        long totalAmount = 0;
        for (Long amount : detailAmount.values()) {
            totalAmount += amount;
        }
        return totalAmount;
    }

    private Map<String, Long> convertToMap(List<DetailAmountsOfInstitutionByYear> detailAmounts) {
        return detailAmounts.stream()
                .collect(Collectors.toMap(
                        DetailAmountsOfInstitutionByYear::getName,
                        DetailAmountsOfInstitutionByYear::getSum));
    }

    public InstitutionOfTheHighestAmount findInstitutionAndYearWithTheHighestAmount() {
        List<Object[]> InstitutionOfTheHighestAmount = financeAmountRepository.findInstitutionAndYearWithTheHighestAmount();
        int year = (int) InstitutionOfTheHighestAmount.get(0)[0];
        String institution = (String) InstitutionOfTheHighestAmount.get(0)[1];

        return new InstitutionOfTheHighestAmount(year, institution);
    }

    public StatisticAboutInstitution findStatisticAboutInstitution(String institution) {
        List<SupportedAmountOfInstitution> averageAmounts = financeAmountRepository.findAverageAmountByInstitutionName(institution);

        Comparator<SupportedAmountOfInstitution> comparator = Comparator.comparingDouble(SupportedAmountOfInstitution::getAmount);
        SupportedAmountOfInstitution min = averageAmounts.stream().min(comparator).orElseThrow(NotFoundAmountException::new);
        SupportedAmountOfInstitution max = averageAmounts.stream().max(comparator).orElseThrow(NotFoundAmountException::new);

        return new StatisticAboutInstitution(institution, Arrays.asList(min, max));
    }
}

