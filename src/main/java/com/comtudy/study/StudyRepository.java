package com.comtudy.study;

import org.springframework.data.jpa.repository.JpaRepository;

import com.comtudy.domain.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {
	
	boolean existsByPath(String path);
}
