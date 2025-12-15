package rut.miit.grpc.sopcreditcalc.service;

import rut.miit.grpc.OfferRequest;
import rut.miit.grpc.OfferResponse;

public interface OfferGenerator {
    OfferResponse generateOffer(OfferRequest model);
}
