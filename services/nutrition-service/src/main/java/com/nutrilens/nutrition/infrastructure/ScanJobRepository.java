package com.nutrilens.nutrition.infrastructure;

import com.nutrilens.nutrition.domain.ScanJob;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanJobRepository extends JpaRepository<ScanJob, UUID> {
}

