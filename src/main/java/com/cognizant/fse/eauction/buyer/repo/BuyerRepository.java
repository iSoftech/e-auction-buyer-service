package com.cognizant.fse.eauction.buyer.repo;

import com.cognizant.fse.eauction.buyer.model.Buyer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerRepository extends MongoRepository<Buyer, Integer> {

    Optional<Buyer> findByEmail(String email);
}
