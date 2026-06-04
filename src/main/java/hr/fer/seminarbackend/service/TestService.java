package hr.fer.seminarbackend.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public Double getSquare(Double base){
        return base * base;
    }
}
    