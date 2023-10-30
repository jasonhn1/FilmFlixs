package com.github.klefstad_teaching.cs122b.gateway.repo;

import com.github.klefstad_teaching.cs122b.gateway.model.GatewayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Component
public class GatewayRepo
{
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public GatewayRepo(NamedParameterJdbcTemplate template)
    {
        this.template = template;
    }

    public Mono<int[]> insertRequests(List<GatewayRequest> requests)
    {
        return Mono.fromCallable(() -> insert(requests));
    }

    public int[] insert(List<GatewayRequest> requests)
    {
        MapSqlParameterSource[] arrayOfSources = new MapSqlParameterSource[requests.size()];

        List<MapSqlParameterSource> listRequests = new ArrayList<>();

        for(GatewayRequest request: requests){

            listRequests.add(        new MapSqlParameterSource()
                    .addValue("ipAddress", request.getIpAddress(), Types.VARCHAR)
                    .addValue("path", request.getPath(), Types.VARCHAR)
                    .addValue("callTime", request.getCallTime(), Types.TIMESTAMP)
            );

        }

        arrayOfSources = listRequests.toArray(arrayOfSources);

        return this.template.batchUpdate(
                "INSERT INTO gateway.request(ip_address, call_time, path) VALUES (:ipAddress, :callTime, :path)",
                arrayOfSources
        );
    }
}
