package org.stepik.practice.enrichment.repository;

import java.util.Optional;
import org.stepik.practice.enrichment.model.User;

public interface UserRepository {
    Optional<User> findByMSISDN(String msisdn);
}
