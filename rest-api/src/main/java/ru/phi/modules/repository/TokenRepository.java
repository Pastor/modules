package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.phi.modules.entity.Token;

@Repository
public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {
    Token findByKey(String key);
}
