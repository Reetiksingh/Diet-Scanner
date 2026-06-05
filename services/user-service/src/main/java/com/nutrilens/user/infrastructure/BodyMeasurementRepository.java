package com.nutrilens.user.infrastructure;

import com.nutrilens.user.domain.BodyMeasurement;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, UUID> {
}

