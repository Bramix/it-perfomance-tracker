package com.bramix.perfomance.tracker.service;

import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.bramix.perfomance.tracker.kafka.producer.JiraReportKafkaProducer;
import com.bramix.perfomance.tracker.utility.JqlUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Slf4j
public class JiraDailyProcessService implements DailyProcessService {
    private final SearchRestClient jiraClient;
    private final JiraReportKafkaProducer jiraReportKafkaProducer;

    private static final int BATCH_SIZE = 50;
    private static final int THREAD_SIZE = 5;
    private static final Set<String> FIELDS_TO_SEARCH = Set.of("*all");


    @Override
    public void startDailyProcess() {
        String searchQuery = JqlUtility.createSearchByUpdatedDateDateQuery(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        IntStream.iterate(0, i -> i + BATCH_SIZE * THREAD_SIZE )
                .map(startFrom -> {
                    List<Iterable<Issue>> resultOfIteration = IntStream.iterate(startFrom, i -> i + BATCH_SIZE).limit(THREAD_SIZE)
                            .parallel()
                            .mapToObj(batchIndex -> doSearch(batchIndex, searchQuery))
                            .map(SearchResult::getIssues)
                            .filter(tickets -> tickets.iterator().hasNext())
                            .toList();

                    jiraReportKafkaProducer.send(resultOfIteration);

                    return resultOfIteration.size();
                })
                .anyMatch(countOfFullBatches -> countOfFullBatches != THREAD_SIZE);

        log.info("Processing has been finished");
    }

    private SearchResult doSearch(int start, String searchQuery) {
        return jiraClient.searchJql(searchQuery, BATCH_SIZE, start, FIELDS_TO_SEARCH)
                .claim();
    }
}
