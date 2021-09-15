package com.comtudy.study;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comtudy.domain.Account;
import com.comtudy.domain.Study;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository repository;

    public Study createNewStudy(Study study, Account account) {
        Study newStudy = repository.save(study);
        newStudy.addManager(account);
        return newStudy;
    }
}
