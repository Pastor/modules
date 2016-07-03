package ru.phi.modules.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.phi.modules.entity.Token;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {
    Token findByKey(String key);
}
