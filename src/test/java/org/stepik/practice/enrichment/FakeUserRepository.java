package org.stepik.practice.enrichment;

import java.util.Map;
import java.util.Optional;
import org.stepik.practice.enrichment.model.User;
import org.stepik.practice.enrichment.repository.UserRepository;

public class FakeUserRepository implements UserRepository {

    private static Map<String, User> msisdnToUsers;

    private FakeUserRepository(Map<String, User> users) {
        msisdnToUsers = users;
    }

    public static FakeUserRepository init(Map<String, User> users) {
        return new FakeUserRepository(users);
    }

    @Override
    public Optional<User> findByMSISDN(String msisdn) {
        return Optional.ofNullable(msisdnToUsers.get(msisdn));
    }
}
