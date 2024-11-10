package com.yl3k.kbsf.counsel.service;

import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.counsel.repository.UserCounselRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CounselService {

    private final CounselRoomRepository counselRoomRepository;
    private final UserCounselRoomRepository userCounselRoomRepository;
}
