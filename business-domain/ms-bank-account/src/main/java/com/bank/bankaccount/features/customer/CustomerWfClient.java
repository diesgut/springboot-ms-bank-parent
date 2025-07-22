package com.bank.bankaccount.features.customer;


import com.bank.bankaccount.features.customer.dto.CustomerDto;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerWfClient {
    private final WebClient.Builder webClientBuilder;
    HttpClient httpClient;


    @PostConstruct
    public void init() {
        httpClient = HttpClient.create()
                // Connection Timeout: is a period within which a connection between a client and a server must be established
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(EpollChannelOption.TCP_KEEPIDLE, 300)
                .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                // Response Timeout: The maximum time we wait to receive a response after sending a request
                .responseTimeout(Duration.ofSeconds(1))
                // Read and Write Timeout: A read timeout occurs when no data was read within a certain
                // period of time, while the write timeout when a write operation cannot finish at a specific time
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                });
    }

    public CustomerDto findCustomerById(UUID customerUuid) {
        WebClient build = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("http://MS-CUSTOMER")
                .filter(logRequest())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
             //   .defaultUriVariables(Collections.singletonMap("url", "http://MS-CUSTOMER/ms-customer/api/v1/customers"))
                .build();

        return build.method(HttpMethod.GET).uri("/customer-service/api/v1/customers/{customerUuid}", customerUuid)
                .retrieve().bodyToMono(CustomerDto.class).block();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.debug("Method {}, URL {}", clientRequest.method(), clientRequest.url());
            // Es importante retornar el request para que la cadena continúe
            return Mono.just(clientRequest);
        });
    }
}
