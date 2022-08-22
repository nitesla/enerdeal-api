package com.enerdeal.service;

import com.enerdeal.model.CentralDistributionBoardPicture;
import com.enerdeal.repo.CentralDistributionBoardPictureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CentralDistributionBoardPictureService {
    @Autowired
    CentralDistributionBoardPictureRepository centralDistributionBoardPicture;

    @Transactional
    public List<CentralDistributionBoardPicture> saveCentralDistributionBoardPicture(List<String> imageurls, Long projectId){
        List<CentralDistributionBoardPicture> pictures = new ArrayList<>();
        if(imageurls != null && !imageurls.isEmpty()){
            centralDistributionBoardPicture.deleteAllByProjectId(projectId);
            log.info("Saving Building Picture with project Id {}", projectId);
            imageurls.forEach(imageurl -> {
                CentralDistributionBoardPicture picture = new CentralDistributionBoardPicture();
                picture.setImageUrl(imageurl);
                picture.setProjectId(projectId);
                CentralDistributionBoardPicture save = centralDistributionBoardPicture.save(picture);
                log.info("Building picture saved successfully ");
                pictures.add(save);
            });
        }
        return pictures;
    }

    public List<CentralDistributionBoardPicture> getCentralDistributionBoardPictures(long projectId){
        List<CentralDistributionBoardPicture> pictures = centralDistributionBoardPicture.findByProjectId(projectId);
        return pictures;
    }
}
