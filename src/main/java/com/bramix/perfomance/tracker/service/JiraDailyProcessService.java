package com.bramix.perfomance.tracker.service;

import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.bramix.perfomance.tracker.redis.producer.JiraRedisEventProducer;
import com.bramix.perfomance.tracker.utility.JqlUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class JiraDailyProcessService implements DailyProcessService {
    private final SearchRestClient jiraClient;
    private final JiraRedisEventProducer jiraRedisEventProducer;

    private static final int BATCH_SIZE = 50;
    private static final int THREAD_SIZE = 5;
    private static final Set<String> FIELDS_TO_SEARCH = Set.of("*all");


    @Override
    public void startDailyProcess() {
        String searchQuery = JqlUtility.createSearchByUpdatedDateDateQuery(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        IntStream.iterate(0, i -> i + BATCH_SIZE * THREAD_SIZE )
                .map(startFrom -> {
                    List<Issue> resultOfIteration = IntStream.iterate(startFrom, i -> i + BATCH_SIZE).limit(THREAD_SIZE)
                            .parallel()
                            .mapToObj(batchIndex -> doSearch(batchIndex, searchQuery))
                            .map(SearchResult::getIssues)
                            .filter(tickets -> tickets.iterator().hasNext())
                            .flatMap(issues -> StreamSupport.stream(issues.spliterator(), Boolean.FALSE))
                            .toList();

                    jiraRedisEventProducer.send(resultOfIteration);

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
