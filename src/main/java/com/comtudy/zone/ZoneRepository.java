package com.comtudy.zone;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comtudy.domain.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Long> {

	Zone findByCityAndProvince(String cityName, String provinceName);

}
