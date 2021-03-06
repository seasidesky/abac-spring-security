package com.github.joffryferrater.pep.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.joffryferrater.pep.PdpConfiguration;
import com.github.joffryferrater.request.XacmlRequest;
import com.github.joffryferrater.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PdpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdpClient.class);

    private final RestTemplate restTemplate;
    private PdpConfiguration pdpConfiguration;

    public PdpClient(RestTemplateBuilder restTemplateBuilder, PdpConfiguration pdpConfiguration) {
        this.restTemplate = restTemplateBuilder.build();
        this.pdpConfiguration = pdpConfiguration;
    }

    public Response sendXacmlJsonRequest(XacmlRequest xacmlRequest) {
        printAuthorizationRequest(xacmlRequest);
        HttpHeaders headers = createHeaders();
        HttpEntity<XacmlRequest> entity = new HttpEntity<>(xacmlRequest, headers);
        final String url = pdpConfiguration.getAuthorizeEndpoint();
        return restTemplate.postForObject(url, entity, Response.class);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/xacml+json");
        headers.add("Accept", "application/json");
        return headers;
    }

    private void printAuthorizationRequest(XacmlRequest xacmlRequest) {
        if (pdpConfiguration.isPrintAuthorizationRequest()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                final String requestInString = objectMapper.writeValueAsString(xacmlRequest);
                LOGGER.info("Authorization Request --> {}", requestInString);
            } catch (JsonProcessingException e) {
                //Do nothing
            }
        }
    }
}

