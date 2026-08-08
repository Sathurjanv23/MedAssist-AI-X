package com.medassist.user.repository;

import com.medassist.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User repository â€” handles core user CRUD and authentication queries.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailAndActive(String email, boolean active);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);

    // Admin: paginated user search
    @Query("{ $or: [ { 'first_name': { $regex: ?0, $options: 'i' } }, { 'last_name': { $regex: ?0, $options: 'i' } }, { 'email': { $regex: ?0, $options: 'i' } } ] }")
    Page<User> searchUsers(String keyword, Pageable pageable);

    // Users registered after a date â€” for analytics
    long countByCreatedAtAfter(LocalDateTime date);

    // Active users with specific role
    @Query("{ 'roles': ?0, 'is_active': true }")
    List<User> findByRoleAndActive(String role);

    // Admin: list all users by role
    Page<User> findByRolesContaining(String role, Pageable pageable);

    long countByActive(boolean active);
}

