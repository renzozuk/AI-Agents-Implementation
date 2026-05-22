package com.seap.emailapi.repository;

import com.seap.emailapi.model.EmailInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<EmailInteraction, UUID> {
}