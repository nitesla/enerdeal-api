package com.enerdeal.service;

import com.enerdeal.model.LandPicture;
import com.enerdeal.repo.LandPictureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LandPictureService {

    @Autowired
    LandPictureRepository landPicture;

    @Transactional
    public List<LandPicture> saveLandPicture(List<String> imageurls, Long projectId){
        List<LandPicture> pictures = new ArrayList<>();
        if(imageurls != null && !imageurls.isEmpty()){
            landPicture.deleteAllByProjectId(projectId);
            log.info("Saving Building Picture with project Id {}", projectId);
            imageurls.forEach(imageurl -> {
                LandPicture picture = new LandPicture();
                picture.setImageUrl(imageurl);
                picture.setProjectId(projectId);
                LandPicture save = landPicture.save(picture);
                log.info("Building picture saved successfully ");
                pictures.add(save);
            });
        }
        return pictures;
    }

    public List<LandPicture> getLandPictures(long projectId){
        List<LandPicture> pictures = landPicture.findByProjectId(projectId);
        return pictures;
    }
}
