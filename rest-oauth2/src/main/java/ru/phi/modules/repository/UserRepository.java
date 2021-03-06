package ru.phi.modules.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.phi.modules.entity.User;

@Repository("userRepository.v1")
@Service
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query(value = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    User find(@Param("username") String username, @Param("password") String password);

    User findByUsername(String username);
}
