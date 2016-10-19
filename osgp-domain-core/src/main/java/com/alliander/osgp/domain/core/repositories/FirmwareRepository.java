package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Firmware;

@Repository
public interface FirmwareRepository extends JpaRepository<Firmware, Long> {

    public List<Firmware> findByModuleVersionCommAndModuleVersionMaAndModuleVersionFunc(String moduleVersionComm,
            String moduleVersionMa, String moduleVersionFunc);
}
