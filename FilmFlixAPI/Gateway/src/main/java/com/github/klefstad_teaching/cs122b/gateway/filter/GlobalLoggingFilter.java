package com.github.klefstad_teaching.cs122b.gateway.filter;

import com.github.klefstad_teaching.cs122b.gateway.config.GatewayServiceConfig;
import com.github.klefstad_teaching.cs122b.gateway.model.GatewayRequest;
import com.github.klefstad_teaching.cs122b.gateway.repo.GatewayRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered
{
    private static final Logger    LOG          = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final Scheduler DB_SCHEDULER = Schedulers.boundedElastic();

    private final GatewayRepo          gatewayRepo;
    private final GatewayServiceConfig config;

    private final LinkedBlockingQueue<GatewayRequest> requests = new LinkedBlockingQueue<GatewayRequest>();

    @Autowired
    public GlobalLoggingFilter(GatewayRepo gatewayRepo, GatewayServiceConfig config)
    {
        this.gatewayRepo = gatewayRepo;
        this.config = config;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        GatewayRequest request = new GatewayRequest();

        request.setCallTime(Timestamp.from(Instant.now()));
        request.setPath(String.valueOf(exchange.getRequest().getPath()));

        String address = exchange.getRequest().getRemoteAddress().toString();
        String[] remoteAddress = address.split("/");
        String ipAddress = remoteAddress[1].split(":")[0];
        request.setIpAddress(ipAddress);


        requests.add(request);

        // If the request is reaching the maxLogs we want to drain
        if (requests.size() >= config.getMaxLogs()){
            drainRequests();
         }

        return chain.filter(exchange);
    }

    public void drainRequests()
    {
        List<GatewayRequest> drainRequest = new ArrayList<>();
        // We are draining to the list and
        requests.drainTo(drainRequest);

        this.gatewayRepo.insertRequests(drainRequest)
                .subscribeOn(DB_SCHEDULER)
                .flatMap(res -> System)
                .subscribe();

    }

    @Override
    public int getOrder()
    {
        return -1;
    }
}
